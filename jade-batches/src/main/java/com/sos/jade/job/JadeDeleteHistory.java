package com.sos.jade.job;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sos.JSHelper.Basics.JSJobUtilitiesClass;
import com.sos.jade.db.JadeTransferDBLayer;
import com.sos.jade.db.JadeTransferDetailDBLayer;

public class JadeDeleteHistory extends JSJobUtilitiesClass<JadeDeleteHistoryOptions> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JadeDeleteHistory.class);

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
            JadeTransferDBLayer jadeTransferDBLayer = new JadeTransferDBLayer(objOptions.configuration_file.getValue());
            jadeTransferDBLayer.setAge(objOptions.age_exceeding_days.value());
            jadeTransferDBLayer.getSession().connect();
            jadeTransferDBLayer.getSession().beginTransaction();
            jadeTransferDBLayer.deleteFromTo();
            jadeTransferDBLayer.getSession().commit();
            JadeTransferDetailDBLayer jadeTransferDetailDBLayer = new JadeTransferDetailDBLayer(objOptions.configuration_file.getValue());
            jadeTransferDetailDBLayer.setAge(objOptions.age_exceeding_days.value());
            jadeTransferDetailDBLayer.getSession().connect();
            jadeTransferDetailDBLayer.getSession().beginTransaction();
            jadeTransferDetailDBLayer.deleteFromTo();
            jadeTransferDetailDBLayer.getSession().commit();
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