package com.guenbon.siso.service.bill;

import com.guenbon.siso.dto.bill.response.SyncBillResultDTO;
import com.guenbon.siso.entity.bill.Bill;
import com.guenbon.siso.entity.congressman.Congressman;
import com.guenbon.siso.entity.congressmanbill.CongressmanBill;
import com.guenbon.siso.repository.bill.BillRepository;
import com.guenbon.siso.repository.congressman.CongressmanRepository;
import com.guenbon.siso.service.congressman.CongressmanService;
import com.guenbon.siso.util.AESUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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
    private final CongressmanService congressmanService;

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
    public SyncBillResultDTO syncBill(List<Bill> apiBillList, Map<String, List<String>> billProposerNameMap) {
        final List<Bill> dbBillList = getAllBillList();
        final Map<String, Bill> dbBillMap = dbBillList.stream()
                .collect(Collectors.toMap(Bill::getBillId, Function.identity()));
        final Map<String, Bill> apiBillMap = apiBillList.stream()
                .collect(Collectors.toMap(Bill::getBillId, Function.identity()));

        List<Bill> insertList = apiBillList.stream()
                .filter(apiBill -> !dbBillMap.containsKey(apiBill.getBillId()))
                .collect(Collectors.toList());

        // 국회의원을 전부 찾아서 map 에 넣어놓고 get 으로 가져다 쓰기  (이름 - 엔티티 맵)
        List<Congressman> congressmanList = congressmanService.findAll();
        // 이름을 key로 하고 Congressman 엔티티를 value로 하는 Map 생성
        Map<String, Congressman> congressmanMap = congressmanList.stream()
                .collect(Collectors.toMap(Congressman::getName, Function.identity()));


        for (Bill bill : insertList) {
            for (String congressmanName : billProposerNameMap.get(bill.getBillId())) {
                // 중간 엔티티 CongressmanBill 생성하면서 연관관계 설정
                CongressmanBill.of(congressmanMap.get(congressmanName), bill);
            }
        }

        // bill batch insert 하면서 CongressmanBill 도 insert 됨
        billRepository.saveAll(insertList);

        List<Bill> deleteList = dbBillList.stream()
                .filter(dbBill -> !apiBillMap.containsKey(dbBill.getBillId()))
                .collect(Collectors.toList());

        // 배치 delete 처리 (BillSummary 자동 삭제)
        billRepository.deleteAll(deleteList);

        List<Bill> updateList = dbBillList.stream()
                .filter(dbBill -> {
                    final Bill apiBill = apiBillMap.get(dbBill.getBillId());
                    return apiBill != null && isBillChanged(apiBill, dbBill);
                })
                .collect(Collectors.toList());

        // 변경감지에 의해 update
        for (Bill bill : updateList) {
            final Bill apiBill = apiBillMap.get(bill.getBillId());
            bill.updateFrom(apiBill);
        }

        return SyncBillResultDTO.of(insertList, deleteList, updateList);
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

    /**
     * 링크에서 summaryContentDiv 의 텍스트를 스크랩해온다
     *
     * @param detailLink
     * @return
     */
    @Transactional(propagation = Propagation.NEVER)
    public String scrapData(String detailLink) {
        Document doc = null;
        try {
            doc = Jsoup.connect(detailLink).get();
        } catch (IOException e) {
            // todo : 예외 , 에러코드 수정 필요
            throw new RuntimeException(e);
        }
        return doc.getElementById("summaryContentDiv").text();
    }
}

