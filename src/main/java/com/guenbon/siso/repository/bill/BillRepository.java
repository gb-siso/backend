package com.guenbon.siso.repository.bill;

import com.guenbon.siso.dto.bill.projection.BillListProjectionDTO;
import com.guenbon.siso.entity.bill.Bill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

    @Query("""
            SELECT new com.guenbon.siso.dto.bill.projection.BillListProjectionDTO(
                b.id, b.billNo, b.billName, b.committee, b.proposeDt,
                b.procResult, b.age, b.detailLink, b.lawProcDt, b.lawPresentDt,
                b.lawSubmitDt, b.cmtProcResultCd, b.cmtProcDt, b.cmtPresentDt,
                b.committeeDt, b.procDt, b.committeeId, b.lawProcResultCd,
                bs.category, bs.content, bs.reason, bs.expected
            )
            FROM Bill b
            JOIN b.billSummary bs
            JOIN b.congressmanBills cb
            WHERE cb.congressman.id = :congressmanId
            """)
    Page<BillListProjectionDTO> getBillListByCongressman(@Param("congressmanId") Long congressmanId, Pageable pageable);

}
