package com.guenbon.siso.service.bill;

import com.guenbon.siso.entity.bill.Bill;
import com.guenbon.siso.repository.bill.BillRepository;
import com.guenbon.siso.util.AESUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class BillService {
    private final AESUtil aesUtil;
    private final BillRepository billRepository;

    public List<Bill> getAllBillList() {
        return billRepository.findAll();
    }

}

