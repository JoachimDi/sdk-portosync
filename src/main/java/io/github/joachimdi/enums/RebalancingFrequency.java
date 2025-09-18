package io.github.joachimdi.enums;

public enum RebalancingFrequency {

    MONTHLY("MENSUEL"),
    QUARTERLY("TRIMESTRIEL"),
    SEMI_ANNUAL("SEMESTRIEL"),
    ANNUAL("ANNUEL");

    private final String value;

    RebalancingFrequency(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
