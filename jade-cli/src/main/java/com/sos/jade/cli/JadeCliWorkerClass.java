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

/** @author KB */
public class JadeCliWorkerClass extends Thread {

    private static final Logger LOGGER = Logger.getLogger(JadeCliWorkerClass.class);
    private ISOSVFSHandler objVFS4Handler = null;
    private SOSFTPOptions objOptions = null;
    private SOSConnection2OptionsAlternate objConnectOptions;
    public ISOSVfsFileTransfer objDataClient = null;

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

    @Override
    public void run() {
        LOGGER.setLevel(Level.DEBUG);
        objConnectOptions = objOptions.getConnectionOptions().Source();
        Vector<String> objA = new Vector<String>();
        BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
        try {
            while (true) {
                LOGGER.debug("give command:\n");
                String strCommand;
                strCommand = bufferRead.readLine();
                if (strCommand == null || strCommand.isEmpty()) {
                    break;
                }
                String[] strCmdParams = strCommand.split(" ");
                switch (strCmdParams[0]) {
                case "open":
                    String strHost = strCmdParams[1];
                    objConnectOptions.protocol.Value(enuTransferTypes.ftp);
                    objConnectOptions.host.Value(strHost);
                    LOGGER.debug("try to connect to host " + strHost);
                    doConnectToServer();
                    LOGGER.info("connected");
                    break;
                case "user":
                    doAuthenticate();
                    break;
                case "quit":
                case "close":
                case "bye":
                    closeConnection();
                    LOGGER.info("bye, bye ...");
                    break;
                case "recv":
                case "get":
                    break;
                case "send":
                case "put":
                    break;
                case "chdir":
                    break;
                case "pwd":
                    break;
                default:
                    break;
                }
                objA.add(strCommand);
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void closeConnection() {
        try {
            if (objVFS4Handler != null) {
                objVFS4Handler.CloseConnection();
                objVFS4Handler.CloseSession();
            }
        } catch (Exception e) {
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
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void doAuthenticate() {
    }
    
}
