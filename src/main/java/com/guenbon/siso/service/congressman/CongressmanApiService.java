package com.guenbon.siso.service.congressman;

import com.fasterxml.jackson.databind.JsonNode;
import com.guenbon.siso.client.CongressApiClient;
import com.guenbon.siso.dto.bill.BillDTO;
import com.guenbon.siso.dto.bill.BillListDTO;
import com.guenbon.siso.dto.congressman.SyncCongressmanDTO;
import com.guenbon.siso.dto.congressman.response.CongressmanBatchResultDTO;
import com.guenbon.siso.dto.news.NewsListDTO;
import com.guenbon.siso.entity.congressman.Congressman;
import com.guenbon.siso.exception.ApiException;
import com.guenbon.siso.exception.CustomException;
import com.guenbon.siso.exception.errorCode.CongressApiErrorCode;
import com.guenbon.siso.util.JsonParserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
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

    @Value("${api.perplexity.key}")
    private String perplexityApikey;

    private final CongressmanService congressmanService;

    private final CongressApiClient congressApiClient;

    public NewsListDTO findNewsList(final String encryptedCongressmanId, final Pageable pageable) {
        Congressman congressman = congressmanService.getCongressman(encryptedCongressmanId);

        Map<String, String> params = Map.of(COMP_MAIN_TITLE, congressman.getName());
        JsonNode jsonNode = fetchApiResponse(pageable, API_NEWS_URL, newsApikey, params);

        int lastPage = calculateTotalPages(extractTotalCount(jsonNode, NEWS_API_PATH), pageable.getPageSize());
        return NewsListDTO.of(getContent(jsonNode, NEWS_API_PATH), lastPage);
    }

    public BillListDTO findBillList(final String encryptedCongressmanId, final Pageable pageable) throws IOException {
        Congressman congressman = congressmanService.getCongressman(encryptedCongressmanId);
        // 발의안 목록 api 요청 및 파싱
        BillListDTO billListDTO = fetchBillList(pageable, congressman);
//        // 발의안 스크랩 + 요약 및 파싱
//        scrapAndSummaryBillContent(billListDTO);
        return billListDTO;
    }

    private BillListDTO fetchBillList(Pageable pageable, Congressman congressman) {
        Map<String, String> params = Map.of(
                AGE, "22",
                PROPOSER, congressman.getName() + "의원"
        );
        JsonNode jsonNode = fetchApiResponse(pageable, API_BILL_URL, billApikey, params);
        int totalPage = calculateTotalPages(extractTotalCount(jsonNode, BILL_API_PATH), pageable.getPageSize());
        BillListDTO billListDTO = BillListDTO.of(getContent(jsonNode, BILL_API_PATH), totalPage);
        return billListDTO;
    }

//    private void scrapAndSummaryBillContent(BillListDTO billListDTO) throws IOException {
//        for (BillDTO billDTO : billListDTO.getBillList()) {
//            // 스크랩
//            String text = scrapBillContent(billDTO);
//            // 요약 - api 요청
//            billDTO.setBillSummary(congressApiClient.getBillSummaryResponse(SUMMARY_URL, perplexityApikey, text));
//        }
//    }

    private static String scrapBillContent(BillDTO billDTO) throws IOException {
        String link = billDTO.getLink();
        Document doc = Jsoup.connect(link).get(); // HTML 가져오기
        Element summary = doc.getElementById("summaryContentDiv"); // ID로 요소 찾기
        String text = summary.text();
        return text;
    }

    private JsonNode fetchApiResponse(final Pageable pageable, final String apiUrl, final String apiKey, final Map<String, String> params) {
        String apiResponse = congressApiClient.getApiResponse(pageable, apiUrl, apiKey, params);
        JsonNode jsonNode = JsonParserUtil.parseJson(apiResponse);

        if (isApiResponseError(jsonNode)) {
            handleApiError(jsonNode);
        }

        return jsonNode;
    }

    /**
     * @return
     */
    public List<SyncCongressmanDTO> fetchRecentCongressmanList() {
        int call = 1;
        int page = 0;
        final int SIZE = 1000;
        final int CALL_LIMIT = 5;

        List<SyncCongressmanDTO> recentSyncList = new ArrayList<>();

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
            filterAndAddCongressman(jsonNode, recentSyncList);
            page++;
            call++;
        }
        return recentSyncList;
    }

    public void filterAndAddCongressman(JsonNode jsonNode, List<SyncCongressmanDTO> recentSyncList) {
        JsonNode rows = getContent(jsonNode, CONGRESSMAN_INFO_API_PATH);
        if (rows.isArray()) {
            for (JsonNode row : rows) {
                String assemblySessions = row.path(ASSEMBLY_SESSIONS_PATH).asText();
                if (assemblySessions.contains(ASSEMBLY_SESSION_NOW)) { // "제22대" 포함 여부 확인
                    recentSyncList.add(SyncCongressmanDTO.of(Congressman.of(row), parseAssemblySessions(assemblySessions)));
                }
            }
        }
    }

    public Set<Integer> parseAssemblySessions(String assemblySessions) {
        Set<Integer> sessions = new HashSet<>();

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
        log.info("국회의원 동기화 fetchAndSyncCongressmen 메서드 호출 -> {}", LocalDateTime.now());
        List<SyncCongressmanDTO> recentSyncList = fetchRecentCongressmanList();
        CongressmanBatchResultDTO congressmanBatchResultDTO = congressmanService.syncCongressman(recentSyncList);
        log.info("국회의원 동기화 fetchAndSyncCongressmen 메서드 완료 -> {}", congressmanBatchResultDTO.getTime());
        return congressmanBatchResultDTO;
    }
}
