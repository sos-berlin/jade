package com.sos.jade.backgroundservice.enums;

public enum TransferStatusValues {
	transferUndefined, 
	waiting4transfer, 
	transferring, 
	transferInProgress, 
	transferred, 
	transfer_skipped, 
	transfer_has_errors, 
	transfer_aborted, 
	compressed, 
	notOverwritten, 
	deleted, 
	renamed, 
	IgnoredDueToZerobyteConstraint, 
	setBack, 
	polling
}
