package com.sos.jade.backgroundservice.enums;

public enum TransferStatusValues {
	TRANSFER_UNDEFINED ("transferUndefined"), 
	WAITING_4_TRANSFER ("waiting4transfer"), 
	TRANSFERRING ("transferring"), 
	TRANSFER_IN_PROGRESS ("transferInProgress"), 
	TRANSFERRED ("transferred"), 
	TRANSFER_SKIPPED ("transfer_skipped"), 
	TRANSFER_HAS_ERRORS ("transfer_has_errors"), 
	TRANSFER_ABORTED ("transfer_aborted"), 
	COMPRESSED ("compressed"), 
	NOT_OVERWRITTEN ("notOverwritten"), 
	DELETED ("deleted"), 
	RENAMED ("renamed"), 
	IGNORED_DUE_TO_ZEROBYTE_CONSTRAINT ("IgnoredDueToZerobyteConstraint"), 
	SET_BACK ("setBack"), 
	POLLING ("polling");
	
	private String name;
	
	private TransferStatusValues (String name){
		this.name = name;
	}
	
	public String getName(){
        return name;
    }
}
