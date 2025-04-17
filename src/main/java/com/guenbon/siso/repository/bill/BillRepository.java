package com.guenbon.siso.repository.bill;

import com.guenbon.siso.entity.bill.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

}
