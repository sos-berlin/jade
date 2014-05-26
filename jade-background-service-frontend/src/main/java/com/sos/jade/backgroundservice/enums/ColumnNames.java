package com.sos.jade.backgroundservice.enums;

public enum ColumnNames {
	GUID ("guid"), SOS_FTP_ID ("sosftpId"), OPERATION ("operation"), TRANSFER_TIMESTAMP ("transferTimestamp"), 
	PID ("pid"), PPID ("ppid"), TARGET_HOST ("targetHost"), TARGET_HOST_IP ("targetHostIp"), 
	TARGET_USER ("targetUser"), TARGET_DIR ("targetDir"), TARGET_FILENAME ("targetFilename"), 
	PROTOCOL ("protocol"), PORT ("port"), STATUS ("status"), LAST_ERROR_MESSAGE ("lastErrorMessage"), 
	LOG_FILENAME ("logFilename"), JUMP_HOST ("jumpHost"), JUMP_HOST_IP ("jumpHostIp"), JUMP_USER ("jumpUser"), 
	JUMP_PROTOCOL ("jumpProtocol"), JUMP_PORT ("jumpPort"), CREATED ("created"), CREATED_BY ("createdBy"),
	MODIFIED ("modified"), MODIFIED_BY ("modifiedBy");

    private final String value;

    private ColumnNames(String value){
    	this.value = value;
    }
    
	public String getValue() {
		return value;
	}
   
	public static int getSize(){
		return values().length;
	}
    
}
