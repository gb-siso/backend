package com.guenbon.siso.service.congressman;

import com.fasterxml.jackson.databind.JsonNode;
import com.guenbon.siso.client.CongressApiClient;
import com.guenbon.siso.dto.bill.BillListDTO;
import com.guenbon.siso.dto.congressman.CongressmanInfoDTO;
import com.guenbon.siso.dto.news.NewsListDTO;
import com.guenbon.siso.entity.Congressman;
import com.guenbon.siso.exception.ApiException;
import com.guenbon.siso.exception.CustomException;
import com.guenbon.siso.exception.errorCode.CongressApiErrorCode;
import com.guenbon.siso.util.JsonParserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.guenbon.siso.exception.errorCode.CongressApiErrorCode.MAX_REQUEST_LIMIT_EXCEEDED;
import static com.guenbon.siso.exception.errorCode.CongressApiErrorCode.NO_DATA_FOUND;
import static com.guenbon.siso.support.constants.ApiConstants.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CongressmanApiService {

    public static final String ASSEMBLY_SESSION_NOW = "제22대";

    @Value("${api.news.key}")
    private String newsApikey;

    @Value("${api.bill.key}")
    private String billApikey;

    @Value("${api.info.key}")
    private String infoApikey;

    private final CongressmanService congressmanService;

    private final CongressApiClient congressApiClient;

    public NewsListDTO findNewsList(final String encryptedCongressmanId, final Pageable pageable) {
        Congressman congressman = congressmanService.getCongressman(encryptedCongressmanId);

        Map<String, String> params = Map.of(COMP_MAIN_TITLE, congressman.getName());
        JsonNode jsonNode = fetchApiResponse(pageable, API_NEWS_URL, newsApikey, params);

        int lastPage = calculateTotalPages(extractTotalCount(jsonNode, NEWS_API_PATH), pageable.getPageSize());
        return NewsListDTO.of(getContent(jsonNode, NEWS_API_PATH), lastPage);
    }

    public BillListDTO findBillList(final String encryptedCongressmanId, final Pageable pageable) {
        Congressman congressman = congressmanService.getCongressman(encryptedCongressmanId);

        Map<String, String> params = Map.of(
                AGE, "22", // todo 대수 : 만약 22대 말고 다른 국회의원도 다룰 거면 변경 필요
                PROPOSER, congressman.getName() + "의원"
        );

        JsonNode jsonNode = fetchApiResponse(pageable, API_BILL_URL, billApikey, params);
        int totalPage = calculateTotalPages(extractTotalCount(jsonNode, BILL_API_PATH), pageable.getPageSize());

        return BillListDTO.of(getContent(jsonNode, BILL_API_PATH), totalPage);
    }

    private JsonNode fetchApiResponse(final Pageable pageable, final String apiUrl, final String apiKey, final Map<String, String> params) {
        String apiResponse = congressApiClient.getApiResponse(pageable, apiUrl, apiKey, params);
        JsonNode jsonNode = JsonParserUtil.parseJson(apiResponse);

        if (isApiResponseError(jsonNode)) {
            handleApiError(jsonNode);
        }
        return jsonNode;
    }

    public List<CongressmanInfoDTO> fetchAndParseCongressmanData() {
        int call = 1;
        int page = 0;
        int size = 1000;
        int callLimit = 5;

        List<CongressmanInfoDTO> congressmanInfoDTOList = new ArrayList<>();

        while (true) {
            if (call > callLimit) {
                // 외부 api 에 의한 예외가 아니라 내가 정한 최대 횟수 초과 예외이므로 CustomException 처리
                throw new CustomException(MAX_REQUEST_LIMIT_EXCEEDED);
            }

            String apiResponse = congressApiClient.getApiResponse(PageRequest.of(page, size), API_CONGRESSMAN_INFO_URL, infoApikey, null);
            JsonNode jsonNode = JsonParserUtil.parseJson(apiResponse);

            if (isApiResponseError(jsonNode)) {
                if (isLastPage(jsonNode)) {
                    break; // 마지막 페이지까지 요청 시 중단
                } else {
                    // 마지막 페이지 에러 외에는 예외처리
                    handleApiError(jsonNode);
                }
            }

            filterAndAddCongressmanInfo(jsonNode, congressmanInfoDTOList);
            page++;
            call++;
        }
        return congressmanInfoDTOList;
    }

    public void filterAndAddCongressmanInfo(JsonNode jsonNode, List<CongressmanInfoDTO> congressmanInfoDTOList) {
        JsonNode rows = getContent(jsonNode, CONGRESSMAN_INFO_API_PATH);
        if (rows.isArray()) {
            for (JsonNode row : rows) {
                String assemblySessions = row.path(ASSEMBLY_SESSIONS_PATH).asText();
                if (assemblySessions.contains(ASSEMBLY_SESSION_NOW)) { // "제22대" 포함 여부 확인
                    congressmanInfoDTOList.add(CongressmanInfoDTO.of(row, assemblySessions));
                }
            }
        }
    }

    private boolean isLastPage(JsonNode jsonNode) {
        return NO_DATA_FOUND.equals(CongressApiErrorCode.from(congressApiClient.getFieldValue(jsonNode, RESULT, CODE).split("-")[1]));
    }

    private boolean isApiResponseError(final JsonNode rootNode) {
        return congressApiClient.getFieldValue(rootNode, RESULT, CODE).contains("-");
    }

    private void handleApiError(final JsonNode rootNode) {
        String errorCode = congressApiClient.getFieldValue(rootNode, RESULT, CODE).split("-")[1];
        throw new ApiException(CongressApiErrorCode.from(errorCode));
    }

    private int extractTotalCount(final JsonNode rootNode, final String apiPath) {
        return rootNode.path(apiPath).get(0)
                .path(HEAD).get(0).path(LIST_TOTAL_COUNT).asInt();
    }

    private int calculateTotalPages(final int totalCount, final int pageSize) {
        return (int) Math.ceil((double) totalCount / pageSize);
    }

    private JsonNode getContent(final JsonNode jsonNode, final String apiPath) {
        return jsonNode.path(apiPath).get(1).path("row");
    }

    public void syncCongressmanData() {
        // 외부 api로 받아온 congressman dto list
        List<CongressmanInfoDTO> apiCongressmanList = fetchAndParseCongressmanData();

        // todo 수정 필요 : apiCongressmanList 를 congressmanService 에 파라미터로 넘기고
        //  이하 로직은 congressmanService 에서 처리해야함
        // 그래야 하나의 트랜잭션 내에서 관리 가능하다.

        // db congressman 테이블에서 현재 목록 가져오기
        List<Congressman> dbCongressmanList = congressmanService.getCongressmanList();

        Map<String, CongressmanInfoDTO> congressmanMapFromDB = dbCongressmanList.stream()
                .collect(Collectors.toMap(Congressman::getCode, CongressmanInfoDTO::from));

        List<CongressmanInfoDTO> toInsert = new ArrayList<>();
        List<CongressmanInfoDTO> toUpdate = new ArrayList<>();
        List<CongressmanInfoDTO> toDelete = new ArrayList<>(dbCongressmanList.stream().map(CongressmanInfoDTO::from).toList());

        for (CongressmanInfoDTO apiCongressman : apiCongressmanList) {

            CongressmanInfoDTO dbCongressman = congressmanMapFromDB.get(apiCongressman.getCode());

            if (dbCongressman == null) {
                toInsert.add(apiCongressman);
            } else {
                if (!apiCongressman.equals(dbCongressman)) {
                    // 만약 여기서 리스트에 넣지 않고 개별 Congressman 에 대해 update 한다면?
                    toUpdate.add(apiCongressman);
                }
                toDelete.remove(apiCongressman);
            }
        }

        // 리스트로 배치 처리
        congressmanService.batchAddCongressman(toInsert);
        congressmanService.batchModifyCongressman(toUpdate);
        congressmanService.batchRemoveCongressman(toDelete);
    }
}
