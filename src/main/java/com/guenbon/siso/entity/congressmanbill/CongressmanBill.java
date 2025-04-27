package com.guenbon.siso.entity.congressmanbill;


import com.guenbon.siso.entity.bill.Bill;
import com.guenbon.siso.entity.congressman.Congressman;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
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

    public static CongressmanBill of(final Congressman congressman, final Bill bill) {
        CongressmanBill cb = CongressmanBill.builder()
                .congressman(congressman)
                .bill(bill)
                .build();

        // 양방향 연관관계 수동 설정
        congressman.getCongressmanBills().add(cb);
        bill.getCongressmanBills().add(cb);

        return cb;
    }
}
