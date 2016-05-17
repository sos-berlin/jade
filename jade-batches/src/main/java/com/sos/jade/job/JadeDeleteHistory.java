package com.sos.jade.job;

import java.io.File;

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
            File configurationFile = new File(objOptions.configuration_file.Value());
            JadeTransferDBLayer jadeTransferDBLayer = new JadeTransferDBLayer(configurationFile);
            jadeTransferDBLayer.setAge(objOptions.age_exceeding_days.value());
            jadeTransferDBLayer.beginTransaction();
            jadeTransferDBLayer.deleteFromTo();
            jadeTransferDBLayer.commit();
            JadeTransferDetailDBLayer jadeTransferDetailDBLayer = new JadeTransferDetailDBLayer(configurationFile);
            jadeTransferDetailDBLayer.setAge(objOptions.age_exceeding_days.value());
            jadeTransferDetailDBLayer.beginTransaction();
            jadeTransferDetailDBLayer.deleteFromTo();
            jadeTransferDetailDBLayer.commit();
        } catch (Exception e) {
            LOGGER.error(String.format(Messages.getMsg("JSJ-I-107"), methodName), e);
        } finally {
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