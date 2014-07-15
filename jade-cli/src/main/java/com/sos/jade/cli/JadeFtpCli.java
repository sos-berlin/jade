/**
 * 
 */
package com.sos.jade.cli;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.sos.VirtualFileSystem.Options.SOSFTPOptions;

/**
 * @author KB
 *
 */
public class JadeFtpCli {
	@SuppressWarnings("unused") private final String		conClassName	= this.getClass().getSimpleName();
	@SuppressWarnings("unused") private static final String	conSVNVersion	= "$Id$";
	@SuppressWarnings("unused") private final Logger		logger			= Logger.getLogger(this.getClass());

	/**
	 * 
	 */
	public JadeFtpCli() {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BasicConfigurator.configure();
		JadeCliWorkerClass objWorker = new JadeCliWorkerClass();
		SOSFTPOptions objOptions = objWorker.Options();
		objOptions.CommandLineArgs(args);
		objWorker.run();
	}
}
