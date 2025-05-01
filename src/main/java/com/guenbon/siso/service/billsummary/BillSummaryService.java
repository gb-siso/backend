package com.guenbon.siso.service.billsummary;

import com.guenbon.siso.dto.bill.BillSummaryDTO;
import com.guenbon.siso.entity.bill.BillSummary;
import com.guenbon.siso.repository.billsummary.BillSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillSummaryService {

    private final BillSummaryRepository billSummaryRepository;

    /**
     * 요약결과 dto로 BillSummary 엔티티를 만들어 저장하고 Bill 과 연관관계를 설정한다.
     *
     * @param billSummaryDTO
     * @param bill
     */
    @Transactional
    public void saveAll(List<BillSummary> list) {
        billSummaryRepository.saveAll(list);
        for (BillSummary billSummary : list) {
            billSummary.getBill().setBillSummary(billSummary);
        }
    }

    public Map<Long, BillSummary> getBillSummaryMap() {
        return billSummaryRepository.findAll().stream()
                .collect(Collectors.toMap(
                        billSummary -> billSummary.getBill().getId(), // key: bill의 id
                        Function.identity()
                ));
    }

    @Transactional
    public BillSummary updateFrom(BillSummary billSummary, BillSummaryDTO billSummaryDTO) {
        return billSummary.updateFrom(billSummaryDTO);
    }
}
