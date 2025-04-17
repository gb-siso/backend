package com.guenbon.siso.entity.congressmanbill;


import com.guenbon.siso.entity.bill.Bill;
import com.guenbon.siso.entity.congressman.Congressman;
import jakarta.persistence.*;

@Entity
public class CongressmanBill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "congressman_id")
    private Congressman congressman; // 다대일 관계 (국회의원 -> 중간 엔티티)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bill_id")
    private Bill bill; // 다대일 관계 (발의안 -> 중간 엔티티)
}
