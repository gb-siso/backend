package com.guenbon.siso.service.bill;

import com.guenbon.siso.dto.bill.response.BillBatchResultDTO;
import com.guenbon.siso.entity.bill.Bill;
import com.guenbon.siso.entity.congressman.Congressman;
import com.guenbon.siso.entity.congressmanbill.CongressmanBill;
import com.guenbon.siso.exception.CustomException;
import com.guenbon.siso.exception.errorCode.CongressmanErrorCode;
import com.guenbon.siso.repository.bill.BillRepository;
import com.guenbon.siso.repository.congressman.CongressmanRepository;
import com.guenbon.siso.util.AESUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class BillService {
    private final AESUtil aesUtil;
    private final BillRepository billRepository;
    private final CongressmanRepository congressmanRepository;

    public List<Bill> getAllBillList() {
        return billRepository.findAll();
    }

    @Transactional
    public void deleteBill(final Bill bill) {
        // Bill → CongressmanBill 목록
        List<CongressmanBill> congressmanBills = bill.getCongressmanBills();

        // 각 Congressman 에서도 congressmanBills 제거
        for (CongressmanBill cb : congressmanBills) {
            Congressman congressman = cb.getCongressman();
            if (congressman != null) {
                congressman.getCongressmanBills().remove(cb);
            }
        }

        // Bill 에서 congressmanBills 전체 제거 (orphanRemoval 발동)
        bill.getCongressmanBills().clear();

        // 마지막으로 Bill 삭제
        billRepository.delete(bill);
    }

    @Transactional
    public BillBatchResultDTO syncBill(List<Bill> apiBillList, Map<String, List<String>> billProposerNameMap) {
        final List<Bill> dbBillList = getAllBillList();
        final Map<String, Bill> dbBillMap = dbBillList.stream()
                .collect(Collectors.toMap(Bill::getBillId, Function.identity()));
        final Map<String, Bill> apiBillMap = apiBillList.stream()
                .collect(Collectors.toMap(Bill::getBillId, Function.identity()));

        List<Bill> insertList = apiBillList.stream()
                .filter(apiBill -> !dbBillMap.containsKey(apiBill.getBillId()))
                .collect(Collectors.toList());

        log.info("syncBill 메서드 insertList : {}", insertList.size());

        int count = 0;

        for (Bill bill : insertList) {
            for (String congressmanName : billProposerNameMap.get(bill.getBillId())) {
                final CongressmanBill congressmanBill = CongressmanBill.of(congressmanRepository.findByName(congressmanName).orElseThrow(() -> new CustomException(CongressmanErrorCode.NOT_EXISTS)), bill);
            }
            billRepository.save(bill);
            log.info("bill 삽입 완료 : {}", ++count);
        }

        log.info("syncBill 메서드 insert 처리 완료");

        List<Bill> deleteList = dbBillList.stream()
                .filter(dbBill -> !apiBillMap.containsKey(dbBill.getBillId()))
                .collect(Collectors.toList());

        for (Bill bill : deleteList) {
            deleteBill(bill);
        }

        List<Bill> updateList = dbBillList.stream()
                .filter(dbBill -> {
                    final Bill apiBill = apiBillMap.get(dbBill.getBillId());
                    return apiBill != null && isBillChanged(apiBill, dbBill);
                })
                .collect(Collectors.toList());

        for (Bill bill : updateList) {
            final Bill apiBill = apiBillMap.get(bill.getBillId());
            bill.updateFrom(apiBill);
        }

        return BillBatchResultDTO.of(insertList, deleteList, updateList);
    }

    /**
     * 발의자 목록 제외 모든 필드 확인해서 발의안 변경 여부 파악
     * 발의자 목록은 바뀔 수가 없음
     *
     * @param a
     * @param b
     * @return
     */
    private boolean isBillChanged(final Bill a, final Bill b) {
        return !Objects.equals(a.getBillNo(), b.getBillNo()) ||
                !Objects.equals(a.getBillName(), b.getBillName()) ||
                !Objects.equals(a.getCommittee(), b.getCommittee()) ||
                !Objects.equals(a.getProposeDt(), b.getProposeDt()) ||
                !Objects.equals(a.getProcResult(), b.getProcResult()) ||
                !Objects.equals(a.getAge(), b.getAge()) ||
                !Objects.equals(a.getDetailLink(), b.getDetailLink()) ||
                !Objects.equals(a.getLawProcDt(), b.getLawProcDt()) ||
                !Objects.equals(a.getLawPresentDt(), b.getLawPresentDt()) ||
                !Objects.equals(a.getLawSubmitDt(), b.getLawSubmitDt()) ||
                !Objects.equals(a.getCmtProcResultCd(), b.getCmtProcResultCd()) ||
                !Objects.equals(a.getCmtProcDt(), b.getCmtProcDt()) ||
                !Objects.equals(a.getCmtPresentDt(), b.getCmtPresentDt()) ||
                !Objects.equals(a.getCommitteeDt(), b.getCommitteeDt()) ||
                !Objects.equals(a.getProcDt(), b.getProcDt()) ||
                !Objects.equals(a.getCommitteeId(), b.getCommitteeId()) ||
                !Objects.equals(a.getLawProcResultCd(), b.getLawProcResultCd());
    }
}

