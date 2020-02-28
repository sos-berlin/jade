package com.sos.DataExchange;

import java.util.HashMap;
import java.util.Properties;

import com.sos.DataExchange.Options.JADEOptions;

public class JadeEngine extends SOSDataExchangeEngine {

    public JadeEngine() throws Exception {
    }

    public JadeEngine(final Properties properties) throws Exception {
        super(properties);
    }

    public JadeEngine(final JADEOptions options) throws Exception {
        super(options);
    }

    public JadeEngine(final HashMap<String, String> map) throws Exception {
        super(map);
    }

}
