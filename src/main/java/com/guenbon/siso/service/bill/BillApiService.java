package com.guenbon.siso.service.bill;

import com.fasterxml.jackson.databind.JsonNode;
import com.guenbon.siso.client.CongressApiClient;
import com.guenbon.siso.dto.bill.BillListDTO;
import com.guenbon.siso.dto.bill.response.BillBatchResultDTO;
import com.guenbon.siso.entity.bill.Bill;
import com.guenbon.siso.entity.congressman.Congressman;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.guenbon.siso.support.constants.ApiConstants.*;
import static com.guenbon.siso.support.constants.ApiConstants.BILL_API_PATH;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillApiService {

    private final CongressApiClient congressApiClient;
    private final BillService billService;

    public BillBatchResultDTO fetchAndSyncBill() {

        // 국회 api 로 받아온 최신 발의안 목록
        // 데이터 없을 때까지 모든 페이지에 대해 요청
        int page = 1;
        while (true) {
            // page 에 대해 api 요청
            result = congressApiClient.getBillResponse(page, billApiUrl);

            // result 가 마지막 페이지인지 확인
            // if 마지막 페이지 -> break
            // else -> page += 1
        }


        // db 에서 발의안 목록 가져오기
        List<Bill> dbBillList = billService.getAllBillList();

        // 동기화하기


        return null;
    }
}
