package com.sos.DataExchange;

import com.sos.VirtualFileSystem.Interfaces.ISOSVfsFileTransfer;

public interface IJadeEngineClientHandler {

    public void onBeforeOperation(ISOSVfsFileTransfer sourceClient, ISOSVfsFileTransfer targetClient) throws Exception;
}
