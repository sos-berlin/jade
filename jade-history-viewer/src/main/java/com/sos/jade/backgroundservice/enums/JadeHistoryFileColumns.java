package com.sos.jade.backgroundservice.enums;

import java.util.Date;

import sos.ftphistory.db.JadeFilesDBItem;

public enum JadeHistoryFileColumns {
    GUID("guid", String.class, ""), SOSFTP_ID("sosftpId", Long.class, 0), OPERATION("operation", String.class, ""), TRANSFER_TIMESTAMP("transferTimestamp",
            Date.class, null), PID("pid", Integer.class, 0), PPID("pPid", Integer.class, 0), TARGET_HOST("targetHost", String.class, ""), TARGET_HOST_IP(
            "targetHostIp", String.class, ""), TARGET_USER("targetUser", String.class, ""), TARGET_DIR("targetDir", String.class, ""), TARGET_FILENAME(
            "targetFilename", String.class, ""), PROTOCOL("protocol", String.class, ""), PORT("port", Integer.class, 0), STATUS("status", String.class, ""), LAST_ERROR_MESSAGE(
            "lastErrorMessage", String.class, ""), LOG_FILENAME("logFilename", String.class, ""), JUMP_HOST("jumpHost", String.class, ""), JUMP_HOST_IP(
            "jumpHostIp", String.class, "0.0.0.0"), JUMP_USER("jumpUser", String.class, ""), JUMP_PROTOCOL("jumpProtocol", String.class, ""), JUMP_PORT(
            "jumpPort", Integer.class, 0), CREATED("created", Date.class, null), CREATED_BY("createdBy", String.class, ""), MODIFIED("modified", Date.class,
            null), MODIFIED_BY("modifiedBy", String.class, ""), JADE_FILES_DB_ITEM("jadeFilesDBItem", JadeFilesDBItem.class, null);

    private String name;
    private Class<?> type;
    private Object defaultValue;

    private JadeHistoryFileColumns(String name, Class<?> type, Object defaultValue) {
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
