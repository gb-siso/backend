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
import java.util.Map;
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

    public NewsListDTO findNewsList(final String encryptedCongressmanId, final Pageable pageable) {
        Congressman congressman = congressmanService.getCongressman(encryptedCongressmanId);

        Map<String, String> params = Map.of(COMP_MAIN_TITLE, congressman.getName());
        JsonNode jsonNode = fetchApiResponse(pageable, API_NEWS_URL, newsApikey, params);

        int totalPage = calculateTotalPages(extractTotalCount(jsonNode, NEWS_API_PATH), pageable.getPageSize());
        return NewsListDTO.of(getContent(jsonNode, NEWS_API_PATH), totalPage);
    }

    public BillListDTO findBillList(final String encryptedCongressmanId, final Pageable pageable) {
        Congressman congressman = congressmanService.getCongressman(encryptedCongressmanId);

        Map<String, String> params = Map.of(
                AGE, "22",
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

    private boolean isApiResponseError(final JsonNode rootNode) {
        return congressApiClient.getFieldValue(rootNode, "RESULT", "CODE").contains("-");
    }

    private void handleApiError(final JsonNode rootNode) {
        String errorCode = congressApiClient.getFieldValue(rootNode, "RESULT", "CODE").split("-")[1];
        throw new CustomException(CongressApiErrorCode.from(errorCode));
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
}
