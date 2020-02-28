package com.sos.DataExchange;

public interface IJadeEngineClientHandler {

    public void onBeforeOperation(SOSDataExchangeEngine engine) throws Exception;

    public void onEnd(SOSDataExchangeEngine engine);
}
