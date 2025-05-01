package com.guenbon.siso.dto.bill;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class BillScrapResult {
    private final boolean success;
    private final String content;
    private final String errorMessage;

    private BillScrapResult(boolean success, String content, String errorMessage) {
        this.success = success;
        this.content = content;
        this.errorMessage = errorMessage;
    }

    public static BillScrapResult success(String content) {
        return new BillScrapResult(true, content, null);
    }

    public static BillScrapResult failure(String errorMessage) {
        return new BillScrapResult(false, null, errorMessage);
    }
}
