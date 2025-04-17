package com.guenbon.siso.service.bill;

import com.fasterxml.jackson.databind.JsonNode;
import com.guenbon.siso.client.CongressApiClient;
import com.guenbon.siso.dto.bill.response.BillBatchResultDTO;
import com.guenbon.siso.entity.bill.Bill;
import com.guenbon.siso.exception.ApiException;
import com.guenbon.siso.exception.errorCode.CongressApiErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.guenbon.siso.exception.errorCode.CongressApiErrorCode.NO_DATA_FOUND;
import static com.guenbon.siso.support.constants.ApiConstants.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillApiService {

    private final CongressApiClient congressApiClient;
    private final BillService billService;

    public BillBatchResultDTO fetchAndSyncBill() {
        // 국회 api 로 받아온 최신 발의안 목록
        List<Bill> apiBillList = new ArrayList<>();

        // 데이터 없을 때까지 api 요청
        int page = 1;
        while (true) {
            // page 에 대해 api 요청
            JsonNode jsonNode = congressApiClient.getBillResponse(page);

            // result 가 마지막 페이지인지 확인
            if (isApiResponseError(jsonNode)) {
                if (isLastPage(jsonNode)) {
                    break; // 마지막 페이지까지 요청 시 중단
                } else {
                    // 마지막 페이지 에러 외에는 예외처리
                    handleApiError(jsonNode);
                }
            }

            getContent(jsonNode, BILL_API_PATH);

            apiBillList.add();

            page += 1;
        }

        // db 에서 발의안 목록 가져오기
        List<Bill> dbBillList = billService.getAllBillList();

        // 동기화하기


        return null;
    }

    private boolean isApiResponseError(final JsonNode rootNode) {
        return congressApiClient.getFieldValue(rootNode, RESULT, CODE).contains("-");
    }

    private boolean isLastPage(JsonNode jsonNode) {
        return NO_DATA_FOUND.equals(CongressApiErrorCode.from(congressApiClient.getFieldValue(jsonNode, RESULT, CODE).split("-")[1]));
    }

    private void handleApiError(final JsonNode rootNode) {
        String errorCode = congressApiClient.getFieldValue(rootNode, RESULT, CODE).split("-")[1];
        throw new ApiException(CongressApiErrorCode.from(errorCode));
    }

    private JsonNode getContent(final JsonNode jsonNode, final String apiPath) {
        return jsonNode.path(apiPath).get(1).path("row");
    }
}
