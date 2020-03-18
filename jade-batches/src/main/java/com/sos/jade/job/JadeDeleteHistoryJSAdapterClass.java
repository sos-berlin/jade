package com.sos.jade.job;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sos.scheduler.job.JobSchedulerJobAdapter;
import sos.spooler.Spooler;

import com.sos.JSHelper.Basics.IJSCommands;

public class JadeDeleteHistoryJSAdapterClass extends JobSchedulerJobAdapter implements IJSCommands {

    private static final Logger LOGGER1 = LoggerFactory.getLogger(JadeDeleteHistoryJSAdapterClass.class);

    @Override
    public boolean spooler_process() throws Exception {

        try {
            super.spooler_process();
            doProcessing();
            return getSpoolerProcess().getSuccess();
        } catch (Exception e) {
            return false;
        }

    }

    private void doProcessing() throws Exception {
        IJSCommands objJSCommands = this;

        JadeDeleteHistory objR = new JadeDeleteHistory();
        JadeDeleteHistoryOptions objO = objR.getOptions();
        objO.setCurrentNodeName(getCurrentNodeName(getSpoolerProcess().getOrder(), true));
        objO.setAllOptions(getSchedulerParameterAsProperties(getSpoolerProcess().getOrder()));
        objO.checkMandatory();
        objR.setJSJobUtilites(this);

        Object objSp = objJSCommands.getSpoolerObject();
        Spooler objSpooler = (Spooler) objSp;

        String configuration_file = "";

        if (objO.getItem("configuration_file") != null) {
            LOGGER1.debug("configuration_file from param");
            configuration_file = objO.configuration_file.getValue();
        } else {
            LOGGER1.debug("configuration_file from scheduler");
            File f = new File(objSpooler.configuration_directory(), "hibernate.cfg.xml");
            configuration_file = f.getAbsolutePath();

        }

        objO.configuration_file.setValue(configuration_file);

        objO.configuration_file.setValue(configuration_file);
        objR.Execute();
    } // doProcessing

}
