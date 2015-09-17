package com.sos.jade.backgroundservice.enums;

public enum TransferStatusValues {
	transferUndefined ("transfer undefined"), 
	waiting4transfer ("waiting for transfer"), 
	transferring ("transferring"), 
	transferInProgress ("transfer in progress"), 
	transferred ("transferred"), 
	transfer_skipped ("skipped"), 
	transfer_has_errors ("has errors"), 
	transfer_aborted ("aborted"), 
	compressed ("compressed"), 
	notOverwritten ("not overwritten"), 
	deleted ("deleted"), 
	renamed ("renamed"), 
	IgnoredDueToZerobyteConstraint ("ignored due to zero byte constraint"), 
	setBack ("setback"), 
	polling ("polling");
	
	public String name;
	
	private TransferStatusValues (String name){
		this.name = name;
	}
}
