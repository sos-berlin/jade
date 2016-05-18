package com.sos.jade.job;

import org.apache.log4j.Logger;

import com.sos.JSHelper.Basics.JSJobUtilitiesClass;
import com.sos.jade.db.JadeTransferDBLayer;
import com.sos.jade.db.JadeTransferDetailDBLayer;

public class JadeDeleteHistory extends JSJobUtilitiesClass<JadeDeleteHistoryOptions> {

    private static final Logger LOGGER = Logger.getLogger(JadeDeleteHistory.class);

    public JadeDeleteHistory() {
        super(new JadeDeleteHistoryOptions());
    }

    @Override
    public JadeDeleteHistoryOptions getOptions() {
        if (objOptions == null) {
            objOptions = new JadeDeleteHistoryOptions();
        }
        return objOptions;
    }

    public JadeDeleteHistory Execute() throws Exception {
        final String methodName = "JadeTransferDetailDBLayerTest::Execute";
        LOGGER.debug(String.format(Messages.getMsg("JSJ-I-110"), methodName));
        try {
            getOptions().checkMandatory();
            LOGGER.debug(getOptions().toString());
            JadeTransferDBLayer jadeTransferDBLayer = new JadeTransferDBLayer(objOptions.configuration_file.Value());
            jadeTransferDBLayer.setAge(objOptions.age_exceeding_days.value());
            jadeTransferDBLayer.getConnection().connect();
            jadeTransferDBLayer.getConnection().beginTransaction();
            jadeTransferDBLayer.deleteFromTo();
            jadeTransferDBLayer.getConnection().commit();
            JadeTransferDetailDBLayer jadeTransferDetailDBLayer = new JadeTransferDetailDBLayer(objOptions.configuration_file.Value());
            jadeTransferDetailDBLayer.setAge(objOptions.age_exceeding_days.value());
            jadeTransferDetailDBLayer.getConnection().connect();
            jadeTransferDetailDBLayer.getConnection().beginTransaction();
            jadeTransferDetailDBLayer.deleteFromTo();
            jadeTransferDetailDBLayer.getConnection().commit();
            LOGGER.debug(String.format(Messages.getMsg("JSJ-I-111"), methodName));
        } catch (Exception e) {
            LOGGER.debug(String.format(Messages.getMsg("JSJ-I-111"), methodName));
        }
        return this;
    }

    public void init() {
        doInitialize();
    }

    private void doInitialize() {
        // doInitialize
    }

}