package com.sos.jade.backgroundservice.enums;

public enum JadeCountry {
    GERMANY("de_DE", String.class), UK("en_GB", String.class), US("en_US", String.class), SPAIN("es_ES", String.class);

    private String value;
    private Class<?> type;

    private JadeCountry(String value, Class<?> type) {
        this.value = value;
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public Class<?> getType() {
        return type;
    }

}
