package com.guenbon.siso.controller;

import com.guenbon.siso.dto.bill.BillListDTO;
import com.guenbon.siso.dto.bill.response.BillBatchResultDTO;
import com.guenbon.siso.service.bill.BillApiService;
import com.guenbon.siso.service.bill.BillService;
import com.guenbon.siso.service.congressman.CongressmanApiService;
import com.guenbon.siso.support.annotation.Login;
import com.guenbon.siso.support.annotation.LoginId;
import com.guenbon.siso.support.annotation.page.PageConfig;
import com.guenbon.siso.support.constants.MemberRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static com.guenbon.siso.support.constants.SortProperty.PROPOSE_DATE;

@Slf4j
@RestController
@RequestMapping("/api/v1/bill")
@RequiredArgsConstructor
public class BillController {

    private final BillApiService billApiService;
    private final CongressmanApiService congressmanApiService;
    private final BillService billService;

    // 발의안 동기화
    @Login(role = MemberRole.ADMIN)
    @PostMapping("/sync")
    public ResponseEntity<BillBatchResultDTO> congressmanSync(@LoginId String encryptedId) {
        return ResponseEntity.ok(billApiService.fetchAndSyncBillAndBillSummary());
    }

    /**
     * 0 페이지부터
     *
     * @param congressmanId
     * @param pageable
     * @return
     * @throws IOException
     */
    @GetMapping("/bills/{congressmanId}")
    public ResponseEntity<BillListDTO> billList(@PathVariable String congressmanId,
                                                @PageConfig(allowedSorts = PROPOSE_DATE, defaultSort = "proposeDate, DESC", defaultPage = 0) Pageable pageable) throws IOException {
        return ResponseEntity.ok().body(billService.findBillList(congressmanId, pageable));
    }
}
