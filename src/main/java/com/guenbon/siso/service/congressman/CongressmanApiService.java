package com.guenbon.siso.service.congressman;

import com.fasterxml.jackson.databind.JsonNode;
import com.guenbon.siso.client.CongressApiClient;
import com.guenbon.siso.dto.bill.BillListDTO;
import com.guenbon.siso.dto.congressman.response.CongressmanBatchResultDTO;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
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

    public List<Congressman> fetchRecentCongressmanList() {
        int call = 1;
        int page = 0;
        final int SIZE = 1000;
        final int CALL_LIMIT = 5;

        List<Congressman> recentCongressmanList = new ArrayList<>();

        while (true) {
            if (call > CALL_LIMIT) {
                // 외부 api 에 의한 예외가 아니라 내가 정한 최대 횟수 초과 예외이므로 CustomException 처리
                throw new CustomException(MAX_REQUEST_LIMIT_EXCEEDED);
            }

            String apiResponse = congressApiClient.getApiResponse(PageRequest.of(page, SIZE), API_CONGRESSMAN_INFO_URL, infoApikey, null);
            JsonNode jsonNode = JsonParserUtil.parseJson(apiResponse);

            if (isApiResponseError(jsonNode)) {
                if (isLastPage(jsonNode)) {
                    break; // 마지막 페이지까지 요청 시 중단
                } else {
                    // 마지막 페이지 에러 외에는 예외처리
                    handleApiError(jsonNode);
                }
            }

            // 22대 국회의원 데이터(현재)만 리스트에 추가
            filterAndAddCongressman(jsonNode, recentCongressmanList);
            page++;
            call++;
        }
        return recentCongressmanList;
    }

    public void filterAndAddCongressman(JsonNode jsonNode, List<Congressman> recentCongressmanList) {
        JsonNode rows = getContent(jsonNode, CONGRESSMAN_INFO_API_PATH);
        if (rows.isArray()) {
            for (JsonNode row : rows) {
                String assemblySessions = row.path(ASSEMBLY_SESSIONS_PATH).asText();
                if (assemblySessions.contains(ASSEMBLY_SESSION_NOW)) { // "제22대" 포함 여부 확인
                    recentCongressmanList.add(Congressman.of(row, parseAssemblySessions(assemblySessions)));
                }
            }
        }
    }

    public List<Integer> parseAssemblySessions(String assemblySessions) {
        List<Integer> sessions = new ArrayList<>();

        // 정규표현식으로 "제XX대" 형식의 문자열을 찾음
        Pattern pattern = Pattern.compile("제(\\d+)대");
        Matcher matcher = pattern.matcher(assemblySessions);

        // 매칭된 부분에서 숫자만 추출하여 List에 추가
        while (matcher.find()) {
            int sessionNumber = Integer.parseInt(matcher.group(1)); // 숫자 부분 추출
            sessions.add(sessionNumber);
        }

        return sessions;
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

    // 매일 05 시 스프링 스케쥴러로 호출
    @Scheduled(cron = "0 0 5 * * *")
    public CongressmanBatchResultDTO fetchAndSyncCongressmen() {
        log.info("fetchAndSyncCongressmen 호출 : {}", LocalDateTime.now());
        List<Congressman> recentCongressmanList = fetchRecentCongressmanList();
        return congressmanService.syncCongressman(recentCongressmanList);
    }
}
