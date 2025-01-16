package com.guenbon.siso.dto.page;

public enum SortProperty {
    ID("id"),
    RATE("rate"),
    LIKE("like"),
    DISLIKE("dislike"),
    TOPICALITY("topicality"),
    ;

    private final String value;

    SortProperty(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static boolean isValid(String property) {
        for (SortProperty sortProperty : values()) {
            if (sortProperty.getValue().equals(property)) {
                return true;
            }
        }
        return false;
    }
}
