package com.guenbon.siso.service.congressman;

import static com.guenbon.siso.support.constants.ApiConstants.AGE;
import static com.guenbon.siso.support.constants.ApiConstants.API_BILL_URL;
import static com.guenbon.siso.support.constants.ApiConstants.API_NEWS_URL;
import static com.guenbon.siso.support.constants.ApiConstants.BILL_API_PATH;
import static com.guenbon.siso.support.constants.ApiConstants.COMP_MAIN_TITLE;
import static com.guenbon.siso.support.constants.ApiConstants.HEAD;
import static com.guenbon.siso.support.constants.ApiConstants.LIST_TOTAL_COUNT;
import static com.guenbon.siso.support.constants.ApiConstants.NEWS_API_PATH;
import static com.guenbon.siso.support.constants.ApiConstants.PROPOSER;

import com.fasterxml.jackson.databind.JsonNode;
import com.guenbon.siso.client.CongressApiClient;
import com.guenbon.siso.dto.bill.BillListDTO;
import com.guenbon.siso.dto.news.NewsListDTO;
import com.guenbon.siso.entity.Congressman;
import com.guenbon.siso.exception.CustomException;
import com.guenbon.siso.exception.errorCode.CongressApiErrorCode;
import com.guenbon.siso.util.JsonParserUtil;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CongressmanApiService {

    @Value("${api.news.key}")
    private String newsApikey;
    @Value("${api.bill.key}")
    private String billApikey;

    private final CongressmanService congressmanService;
    private final CongressApiClient congressApiClient;

    public NewsListDTO findNewsList(String encryptedCongressmanId, Pageable pageable) {

        Congressman congressman = congressmanService.getCongressman(encryptedCongressmanId);

        HashMap<String, String> params = new HashMap<>();
        params.put(COMP_MAIN_TITLE, congressman.getName());

        String apiResponse = congressApiClient.getApiResponse(pageable, API_NEWS_URL, newsApikey, params);
        JsonNode jsonNode = JsonParserUtil.parseJson(apiResponse);

        if (isApiResponseError(jsonNode)) {
            handleApiError(jsonNode);
        }

        int totalCount = extractTotalCount(jsonNode, NEWS_API_PATH);
        int totalPage = calculateTotalPages(totalCount, pageable.getPageSize());

        return NewsListDTO.of(getContent(jsonNode, NEWS_API_PATH), totalPage);
    }

    public BillListDTO findBillList(String encryptedCongressmanId, Pageable pageable) {
        Congressman congressman = congressmanService.getCongressman(encryptedCongressmanId);

        HashMap<String, String> params = new HashMap<>();
        params.put(AGE, "22");
        params.put(PROPOSER, congressman.getName() + "의원");

        String apiResponse = congressApiClient.getApiResponse(pageable, API_BILL_URL, billApikey, params);
        JsonNode jsonNode = JsonParserUtil.parseJson(apiResponse);

        if (isApiResponseError(jsonNode)) {
            handleApiError(jsonNode);
        }

        int totalCount = extractTotalCount(jsonNode, BILL_API_PATH);
        int totalPage = calculateTotalPages(totalCount, pageable.getPageSize());

        return BillListDTO.of(getContent(jsonNode, BILL_API_PATH), totalPage);
    }

    private boolean isApiResponseError(final JsonNode rootNode) {
        // ✅ CongressApiClient의 getFieldValue 활용
        String code = congressApiClient.getFieldValue(rootNode, "RESULT", "CODE");
        return code.contains("-");
    }

    private void handleApiError(final JsonNode rootNode) {
        String errorCode = congressApiClient.getFieldValue(rootNode, "RESULT", "CODE").split("-")[1];
        throw new CustomException(CongressApiErrorCode.from(errorCode));
    }

    private int extractTotalCount(final JsonNode rootNode, String apiPath) {
        return rootNode.path(apiPath).get(0)
                .path(HEAD).get(0).path(LIST_TOTAL_COUNT).asInt();
    }

    private int calculateTotalPages(final int totalCount, final int pageSize) {
        return (totalCount + pageSize - 1) / pageSize; // 올림 계산
    }

    private JsonNode getContent(JsonNode jsonNode, String apiPath) {
        return jsonNode.path(apiPath).get(1).path("row");
    }
}
