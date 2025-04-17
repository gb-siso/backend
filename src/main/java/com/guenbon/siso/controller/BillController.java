package com.guenbon.siso.controller;

import com.guenbon.siso.dto.bill.response.BillBatchResultDTO;
import com.guenbon.siso.service.bill.BillApiService;
import com.guenbon.siso.support.annotation.Login;
import com.guenbon.siso.support.annotation.LoginId;
import com.guenbon.siso.support.constants.MemberRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/bill")
@RequiredArgsConstructor
public class BillController {

    private final BillApiService billApiService;

    // todo : 발의안 목록도 여기로 이동 필요

    // 발의안 동기화
    @Login(role = MemberRole.ADMIN)
    @PostMapping("/sync")
    public ResponseEntity<BillBatchResultDTO> congressmanSync(@LoginId String encryptedId) {
        return ResponseEntity.ok(billApiService.fetchAndSyncBill());
    }
}
