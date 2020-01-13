package com.sos.DataExchange;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sos.DataExchange.Jade4DMZ.Operation;
import com.sos.DataExchange.Options.JADEOptions;
import com.sos.VirtualFileSystem.Interfaces.ISOSVfsFileTransfer;
import com.sos.VirtualFileSystem.SFTP.SOSVfsSFtpJCraft;

import sos.util.SOSString;

public class Jade4DMZEngineClientHandler implements IJadeEngineClientHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Jade4DMZEngineClientHandler.class);
    private static final boolean isDebugEnabled = LOGGER.isDebugEnabled();
    private boolean copyFromInternetWithFileList = false;
    private String clientFileListName = null;
    private String jumpFileListName = null;

    public Jade4DMZEngineClientHandler(JADEOptions options, Operation operation, String jumpDir, String jumpUuid) {
        if (operation.equals(Operation.copyFromInternet) && !SOSString.isEmpty(options.fileListName.getValue())) {
            copyFromInternetWithFileList = true;
            clientFileListName = options.fileListName.getValue();
            jumpFileListName = new StringBuilder(jumpDir).append(jumpUuid).append(".filelist").toString();
        }
    }

    @Override
    public void onBeforeOperation(ISOSVfsFileTransfer sourceClient, ISOSVfsFileTransfer targetClient) throws Exception {
        if (copyFromInternetWithFileList) {
            copyFileListToJump(sourceClient);
        }
    }

    private void copyFileListToJump(ISOSVfsFileTransfer sourceClient) throws Exception {
        if (sourceClient instanceof SOSVfsSFtpJCraft) {
            if (isDebugEnabled) {
                LOGGER.debug(String.format("[source][copyFileListToJump][%s]%s", clientFileListName, jumpFileListName));
            }
            SOSVfsSFtpJCraft h = (SOSVfsSFtpJCraft) sourceClient;
            File f = new File(clientFileListName);
            if (!f.exists()) {
                throw new Exception(String.format("[source][copyFileListToJump][%s]not found local FileList file", f
                        .getCanonicalPath()));
            }
            h.put(f.getCanonicalPath(), jumpFileListName);
        }
    }

    public boolean isCopyFromInternetWithFileList() {
        return copyFromInternetWithFileList;
    }

    public String getJumpFileListName() {
        return jumpFileListName;
    }
}
