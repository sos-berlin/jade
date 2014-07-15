package com.sos.jade.cli;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.sos.JSHelper.Exceptions.JobSchedulerException;
import com.sos.JSHelper.Options.SOSOptionTransferType.enuTransferTypes;
import com.sos.VirtualFileSystem.Factory.VFSFactory;
import com.sos.VirtualFileSystem.Interfaces.ISOSVFSHandler;
import com.sos.VirtualFileSystem.Interfaces.ISOSVfsFileTransfer;
import com.sos.VirtualFileSystem.Options.SOSConnection2OptionsAlternate;
import com.sos.VirtualFileSystem.Options.SOSFTPOptions;

/**
 * 
 */
/**
 * @author KB
 *
 * see http://www.nsftools.com/tips/MSFTP.htm
 */
public class JadeCliWorkerClass extends Thread {
	@SuppressWarnings("unused") private final String		conClassName	= this.getClass().getSimpleName();
	@SuppressWarnings("unused") private static final String	conSVNVersion	= "$Id$";
	@SuppressWarnings("unused") private final Logger		logger			= Logger.getLogger(this.getClass());
	private ISOSVFSHandler									objVFS4Handler	= null;
	private SOSFTPOptions									objOptions		= null;
	private SOSConnection2OptionsAlternate					objConnectOptions;
	public ISOSVfsFileTransfer								objDataClient	= null;

	/**
	 * 
	 */
	public JadeCliWorkerClass() {
	}

	public void Options(final SOSFTPOptions pobjOptions) {
		this.objOptions = pobjOptions;
	}

	public SOSFTPOptions Options() {
		if (objOptions == null) {
			objOptions = new SOSFTPOptions();
		}
		return objOptions;
	}

	public void setCommands(Vector<String> objA) {
		// TODO Auto-generated method stub
	}

	@Override public void run() {
		logger.setLevel(Level.DEBUG);
		objConnectOptions = objOptions.getConnectionOptions().Source();
		Vector<String> objA = new Vector<String>();
		BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
		try {
			while (true) {
				System.out.println("give command:\n");
				String strCommand;
				strCommand = bufferRead.readLine();
				if (strCommand == null || strCommand.length() <= 0) {
					break;
				}
				String[] strCmdParams = strCommand.split(" ");
				switch (strCmdParams[0]) {
					case "open":
						/*
						 * Connects to the specified FTP server.

						Syntax: open computer [port]

						Parameter(s):
						computer - Specifies the remote computer to connect to. Computer can be specified by IP address or computer name (a DNS or HOSTS file must be available). If auto-login is on (default), FTP also attempts to automatically log the user in to the FTP server (see Ftp command-line options to disable auto-login).
						port - Specifies a port number to use to contact an FTP server. 
						 */
						String strHost = strCmdParams[1];
						objConnectOptions.protocol.Value(enuTransferTypes.ftp);
						objConnectOptions.host.Value(strHost);
						logger.debug("try to connect to host " + strHost);
						doConnectToServer();
						logger.info("connected");
						break;
					case "user":
						doAuthenticate();
						break;
					case "quit":
					case "close":
					case "bye":
						closeConnection();
						System.out.println("bye, bye ...");
						break;
					case "recv":
					case "get":
						/*
						 * Copies a remote file to the local computer using the current file transfer type. See also mget, which can copy multiple files.

						Syntax: get remote-file [local-file]

						Parameter(s):
						remote-file
						Specifies the remote file to copy.

						local-file
						Specifies the name to use on the local computer. If not specified, the file is given the remote-file name. 
						 */
						break;
					case "send":
					case "put":
						/*
						 * Copies a local file to the remote computer using the current file transfer type. See also mput, which can copy multiple files.

						Syntax: put local-file [remote-file]

						Parameter(s):
						local-file - Specifies the local file to copy.
						remote-file - Specifies the name to use on the remote computer. If not specified, the file is given the local-file name. 
						 */
						break;
					case "chdir":
						break;
					case "pwd":
						/*
						 * Displays the current directory on the remote computer.

						Syntax: pwd 
						 */
						break;
					default:
						break;
				}
				objA.add(strCommand);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean isConnected() {
		boolean flgR = true;
		if (objVFS4Handler != null) {
			flgR = objVFS4Handler.isConnected();
		}
		return flgR;
	}

	private void closeConnection() {
		try {
			if (objVFS4Handler != null) {
				objVFS4Handler.CloseConnection();
				objVFS4Handler.CloseSession();
			}
		}
		catch (Exception e) {
			throw new JobSchedulerException(e);
		}
	}

	private void doConnectToServer() {
		String strDataType = "ftp";
		try {
			objConnectOptions = objOptions.getConnectionOptions().Source();
			VFSFactory.setConnectionOptions(objConnectOptions);
			objVFS4Handler = VFSFactory.getHandler(strDataType);
			objDataClient = (ISOSVfsFileTransfer) objVFS4Handler;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void doAuthenticate() {
	}

	private void doConnect(final ISOSVFSHandler objVFS4Handler, final SOSConnection2OptionsAlternate objConnectOptions) {
		try {
			objVFS4Handler.Connect(objConnectOptions);
		}
		catch (Exception e) { // Problem to connect, try alternate host
			// TODO respect alternate data-source type? alternate port etc. ?
			JobSchedulerException.LastErrorMessage = "";
			try {
				objVFS4Handler.Connect(objConnectOptions.Alternatives());
				objConnectOptions.setAlternateOptionsUsed("true");
			}
			catch (Exception e1) {
				throw new JobSchedulerException(e);
			}
			// TODO get an instance of .Alternatives for Authentication ...
		}
	}

	private void doAuthenticate(final ISOSVFSHandler objVFS4Handler, final SOSConnection2OptionsAlternate objConnectOptions, final boolean pflgIsDataSource)
			throws Exception {
		try {
			objVFS4Handler.Authenticate(objConnectOptions);
		}
		catch (Exception e) { // SOSFTP-113: Problem to login, try alternate User
			// TODO respect alternate authentication, eg password and/or public key
			JobSchedulerException.LastErrorMessage = "";
			try {
				objVFS4Handler.Authenticate(objConnectOptions.Alternatives());
			}
			catch (RuntimeException e1) {
				throw e1;
			}
			objConnectOptions.setAlternateOptionsUsed("true");
		}
		ISOSVfsFileTransfer objDataClient = (ISOSVfsFileTransfer) objVFS4Handler;
		if (objOptions.passive_mode.value() || objConnectOptions.passive_mode.isTrue()) {
			objDataClient.passive();
		}
		//objConnectOptions.transfer_mode is not used?
		if (objConnectOptions.transfer_mode.isDirty() && objConnectOptions.transfer_mode.IsNotEmpty()) {
			objDataClient.TransferMode(objConnectOptions.transfer_mode);
		}
		else {
			objDataClient.TransferMode(objOptions.transfer_mode);
		}
		objDataClient.ControlEncoding(objOptions.ControlEncoding.Value());
		// TODO pre-commands for source and target separately
		if (objOptions.PreFtpCommands.IsNotEmpty() && pflgIsDataSource == false) {
			// TODO Command separator as option
			for (String strCmd : objOptions.PreFtpCommands.split()) {
				strCmd = objOptions.replaceVars(strCmd);
				objDataClient.getHandler().ExecuteCommand(strCmd);
			}
		}
		if (objConnectOptions.PreFtpCommands.IsNotEmpty()) {
			// TODO Command separator as option
			for (String strCmd : objConnectOptions.PreFtpCommands.split()) {
				strCmd = objConnectOptions.replaceVars(strCmd);
				objDataClient.getHandler().ExecuteCommand(strCmd);
			}
		}
	}
}
