package com.guenbon.siso.repository.billsummary;

import com.guenbon.siso.entity.bill.BillSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillSummaryRepository extends JpaRepository<BillSummary, Long> {

}
