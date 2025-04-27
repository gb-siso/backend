package com.guenbon.siso;

import com.guenbon.siso.dto.bill.response.BillBatchResultDTO;
import com.guenbon.siso.repository.bill.BillRepository;
import com.guenbon.siso.service.bill.BillApiService;
import com.guenbon.siso.service.congressman.CongressmanApiService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class IntegratedTest {

    @Autowired
    BillApiService billApiService;
    @Autowired
    private CongressmanApiService congressmanApiService;
    @Autowired
    BillRepository billRepository;

    @Test
    public void test() {
        congressmanApiService.fetchAndSyncCongressmen();
        BillBatchResultDTO billBatchResultDTO = billApiService.fetchAndSyncBill();
        log.info(billBatchResultDTO.toString());
    }
}
