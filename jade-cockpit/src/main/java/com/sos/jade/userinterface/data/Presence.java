package com.sos.jade.userinterface.data;

import org.apache.log4j.Logger;

public class Presence {
	
	@SuppressWarnings("unused")
	private final Logger			logger				= Logger.getLogger(Presence.class);
	public final String		conSVNVersion		= "$Id$";

	
//	public static final Presence ONLINE = new Presence("Online");
	public static final Presence ONLINE = new Presence("Run");
	public static final Presence AWAY = new Presence("Away");
	public static final Presence DO_NOT_DISTURB = new Presence("Do Not Disturb");
	public static final Presence INVISIBLE = new Presence("Offline");
	public static final Presence Profile = new Presence("Offline");
	private final String value;

	private Presence(final String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
}
