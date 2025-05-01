package com.guenbon.siso.support.fixture.bill;

import com.guenbon.siso.entity.bill.Bill;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class BillFixture {

    private Long id;
    private String detailLink = "https://example.com/bill-detail";

    public static BillFixture builder() {
        return new BillFixture();
    }

    public BillFixture setId(Long id) {
        this.id = id;
        return this;
    }

    public BillFixture setDetailLink(String detailLink) {
        this.detailLink = detailLink;
        return this;
    }

    public Bill build() {
        return Bill.builder()
                .id(id)
                .detailLink(detailLink)
                .build();
    }
}
