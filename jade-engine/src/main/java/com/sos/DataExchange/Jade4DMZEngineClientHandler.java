package com.sos.DataExchange;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sos.DataExchange.Jade4DMZ.Operation;
import com.sos.DataExchange.Options.JADEOptions;
import com.sos.vfs.sftp.SOSSFTP;
import com.sos.vfs.common.interfaces.ISOSTransferHandler;

import sos.util.SOSString;

public class Jade4DMZEngineClientHandler implements IJadeEngineClientHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Jade4DMZEngineClientHandler.class);
    private static final boolean isDebugEnabled = LOGGER.isDebugEnabled();

    private Operation operation;
    private boolean copyFromInternetWithFileList = false;
    private String clientFileListName = null;
    private String jumpFileListName = null;
    private String jumpWorkingDir = null;

    public Jade4DMZEngineClientHandler(JADEOptions options, Operation dmzOperation, String jumpDir, String jumpUuid, String jumpSubDir) {
        operation = dmzOperation;
        jumpWorkingDir = jumpSubDir;
        String fileListName = options.fileListName.getValue();
        if (operation.equals(Operation.copyFromInternet) && !SOSString.isEmpty(fileListName)) {
            copyFromInternetWithFileList = true;
            clientFileListName = fileListName;
            jumpFileListName = new StringBuilder(jumpDir).append(jumpUuid).append(".filelist").toString();
        }
    }

    @Override
    public void onBeforeOperation(SOSDataExchangeEngine engine) throws Exception {
        if (copyFromInternetWithFileList) {
            copyFileListToJump(engine.getSourceClient());
        }
    }

    @Override
    public void onEnd(SOSDataExchangeEngine engine) {
        // removeDirOnDMZ
        try {
            if (engine == null || operation.equals(Operation.remove)) {
                return;
            }
            String command = getRemoveDirCommand(engine.getOptions(), jumpWorkingDir);
            if (operation.equals(Operation.copyToInternet)) {
                if (engine.getTargetClient() != null) {
                    engine.executeTransferCommands("target remove dir", engine.getTargetClient(), command, null);
                } else {
                    LOGGER.warn(String.format("[skip][%s]targetClient or targetClient.Handler is null", command));
                }
            } else {
                if (engine.getSourceClient() != null) {
                    engine.executeTransferCommands("source remove temp files", engine.getSourceClient(), command, null);
                } else {
                    LOGGER.warn(String.format("[skip][%s]sourceClient or sourceClient.Handler is null", command));
                }
            }
        } catch (Exception ex) {
            LOGGER.warn(String.format("%s", ex.toString()));
        }
    }

    private String getRemoveDirCommand(JADEOptions options, String dir) {
        if (options.jumpPlatform.isWindows()) {
            dir = dir.replace('/', '\\');
            return "rmdir \"" + dir + "\" /s /q;del /F /Q " + dir + "* 2>nul";
        } else {
            return "rm -f -R " + dir + "*";
        }
    }

    private void copyFileListToJump(ISOSTransferHandler sourceClient) throws Exception {
        if (sourceClient instanceof SOSSFTP) {
            if (isDebugEnabled) {
                LOGGER.debug(String.format("[source][copyFileListToJump][%s]%s", clientFileListName, jumpFileListName));
            }
            SOSSFTP h = (SOSSFTP) sourceClient;
            File f = new File(clientFileListName);
            if (!f.exists()) {
                throw new Exception(String.format("[source][copyFileListToJump][%s]not found local FileList file", f.getCanonicalPath()));
            }
            h.putFile(f.getCanonicalPath(), jumpFileListName);
        }
    }

    public boolean isCopyFromInternetWithFileList() {
        return copyFromInternetWithFileList;
    }

    public String getJumpFileListName() {
        return jumpFileListName;
    }
}
