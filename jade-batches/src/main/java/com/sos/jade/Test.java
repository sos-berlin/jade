package com.sos.jade;

import java.io.File;
import com.sos.hibernate.layer.SOSHibernateDBLayer;

 

public class Test {

	@SuppressWarnings("unused")
	private final String	conClassName	= "Test";

	public Test() {
		//
	}

	//Warum geht diese Methode nicht? Wenn dies Klasse in com.sos.jade aufgerufen wird,funktioniert es.

	public static void main(String[] args) {
		SOSHibernateDBLayer l =   new SOSHibernateDBLayer();
		l.setConfigurationFile(new File("c:/temp/hibernate.cfg.xml"));
		l.initSession();
	}
}
