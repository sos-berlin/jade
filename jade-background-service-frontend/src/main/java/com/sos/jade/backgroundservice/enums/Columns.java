package com.sos.jade.backgroundservice.enums;

import java.util.Date;

public enum Columns {
	CREATED("created", Date.class, null), CREATED_BY("createdBy", String.class, ""), GUID("guid", String.class, ""), 
	JUMP_HOST("jumpHost", String.class, ""), JUMP_HOST_IP("jumpHostIp", String.class, "0.0.0.0"),
	JUMP_PORT("jumpPort", Integer.class, 0), JUMP_PROTOCOL("jumpProtocol", String.class, ""),
	JUMP_USER("jumpUser", String.class, ""), LAST_ERROR_MESSAGE("lastErrorMessage", String.class, ""), 
	LOG_FILENAME("logFilename", String.class, ""), MODIFIED("modified", Date.class, null), 
	MODIFIED_BY("modifiedBy", String.class, ""), OPERATION("operation", String.class, ""), 
	PID("pid", Integer.class, 0), PPID("pPid", Integer.class, 0), 
	PORT("port", Integer.class, 0), PROTOCOL("protocol", String.class, ""), 
	SOSFTP_ID("sosftpId", Integer.class, 0), STATUS("status", String.class, ""), 
	TARGET_DIR("targetDir", String.class, ""), TARGET_FILENAME("targetFilename", String.class, ""), 
	TARGET_HOST("targetHost", String.class, ""), TARGET_HOST_IP("targetHostIP", String.class, ""), 
	TARGET_USER("targetUser", String.class, ""), TRANSFER_TIMESTAMP("transferTimestamp", Date.class, null), 
	TRANSFER_TIMESTAMP_ISO("transferTimestampIso", String.class, null), JADE_FILE_CREATED("created", Date.class, null), 
	JADE_FILE_CREATED_BY("createdBy", String.class, ""), JADE_FILE_SIZE("fileSize", Integer.class, 0), 
	JADE_FILE_ID("fileId", Long.class, 0), JADE_FILE_MANDATOR("mandator", String.class, ""), 
	JADE_FILE_MD5("md5", String.class, ""), JADE_FILE_MODIFIED("modified", Date.class, ""), 
	JADE_FILE_MODIFIED_BY("modifiedBy", String.class, ""), JADE_FILE_SOURCE_DIR("sourceDir", String.class, ""), 
	JADE_FILE_SOURCE_FILENAME("sourceFileName", String.class, ""), JADE_FILE_SOURCE_HOST("sourceHost", String.class, ""), 
	JADE_FILE_SOURCE_HOST_IP("sourceHostIp", String.class, ""), JADE_FILE_SOURCE_USER("sourceUser", String.class, "");

	private String name;
	private Class<?> type;
	private Object defaultValue;
	
	private Columns(String name, Class<?> type, Object defaultValue) {
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
