package com.sos.jade.backgroundservice.enums;

import java.util.Date;

public enum JadeFileColumns {
    ID("id", Long.class, null), MANDATOR("mandator", String.class, ""), SOURCE_HOST("sourceHost", String.class, ""), SOURCE_HOST_IP("sourceHostIp",
            String.class, ""), SOURCE_USER("sourceUser", String.class, ""), SOURCE_DIR("sourceDir", String.class, ""), SOURCE_FILENAME(
            "sourceFilename", String.class, ""), MD5("md5", String.class, ""), FILE_SIZE("fileSize", Long.class, 0), CREATED("created", Date.class,
            null), CREATED_BY("createdBy", String.class, ""), JADE_FILE_MODIFIED("modified", Date.class, ""), JADE_FILE_MODIFIED_BY("modifiedBy",
            String.class, "");

    private String name;
    private Class<?> type;
    private Object defaultValue;

    private JadeFileColumns(String name, Class<?> type, Object defaultValue) {
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }
}
