package com.guenbon.siso.dto.bill;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class BillSummaryParseResult {
    private final boolean success;
    private final BillSummaryDTO summary;
    private final String errorMessage;

    private BillSummaryParseResult(boolean success, BillSummaryDTO summary, String errorMessage) {
        this.success = success;
        this.summary = summary;
        this.errorMessage = errorMessage;
    }

    public static BillSummaryParseResult success(BillSummaryDTO summary) {
        return new BillSummaryParseResult(true, summary, null);
    }

    public static BillSummaryParseResult failure(String errorMessage) {
        return new BillSummaryParseResult(false, null, errorMessage);
    }
}