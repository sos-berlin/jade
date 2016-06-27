package com.sos.jade.job;

import java.io.File;

import org.apache.log4j.Logger;

import sos.scheduler.job.JobSchedulerJobAdapter;
import sos.spooler.Spooler;

import com.sos.JSHelper.Basics.IJSCommands;

// Super-Class for JobScheduler Java-API-Jobs
/** \class JadeDeleteHistoryJSAdapterClass - JobScheduler Adapter for
 * "Delete entries in Jade history table"
 *
 * \brief AdapterClass of JadeDeleteHistory for the SOSJobScheduler
 *
 * This Class JadeDeleteHistoryJSAdapterClass works as an adapter-class between
 * the SOS JobScheduler and the worker-class JadeDeleteHistory.
 *
 * 
 *
 * see \see C:\Dokumente und Einstellungen\Uwe Risse\Lokale
 * Einstellungen\Temp\scheduler_editor-7740628146985625965.html for more
 * details.
 *
 * \verbatim ; mechanicaly created by C:\Dokumente und Einstellungen\Uwe
 * Risse\Eigene
 * Dateien\sos-berlin.com\jobscheduler.1.3.9\scheduler_139\config\JOETemplates
 * \java\xsl\JSJobDoc2JSAdapterClass.xsl from http://www.sos-berlin.com at
 * 20111221162313 \endverbatim */
public class JadeDeleteHistoryJSAdapterClass extends JobSchedulerJobAdapter implements IJSCommands {

    private final String conClassName = "JadeDeleteHistoryJSAdapterClass";  //$NON-NLS-1$
    private static Logger logger1 = Logger.getLogger(JadeDeleteHistoryJSAdapterClass.class);

    public void init() {
        @SuppressWarnings("unused")
        final String conMethodName = conClassName + "::init"; //$NON-NLS-1$
        doInitialize();
    }

    private void doInitialize() {
    } // doInitialize

    @Override
    public boolean spooler_init() {
        @SuppressWarnings("unused")
        final String conMethodName = conClassName + "::spooler_init"; //$NON-NLS-1$
        return super.spooler_init();
    }

    @Override
    public boolean spooler_process() throws Exception {
        @SuppressWarnings("unused")
        final String conMethodName = conClassName + "::spooler_process"; //$NON-NLS-1$

        try {
            super.spooler_process();
            doProcessing();
        } catch (Exception e) {
            return false;
        } finally {
        } // finally
          // return value for classic and order driven processing
          // TODO create method in base-class for this functionality
        return spooler_task.job().order_queue() != null;

    } // spooler_process

    @Override
    public void spooler_exit() {
        @SuppressWarnings("unused")
        final String conMethodName = conClassName + "::spooler_exit"; //$NON-NLS-1$
        super.spooler_exit();
    }

    private void doProcessing() throws Exception {
        IJSCommands objJSCommands = this;
        @SuppressWarnings("unused")
        final String conMethodName = conClassName + "::doProcessing"; //$NON-NLS-1$

        JadeDeleteHistory objR = new JadeDeleteHistory();
        JadeDeleteHistoryOptions objO = objR.getOptions();
        objO.setCurrentNodeName(getCurrentNodeName());
        objO.setAllOptions(getSchedulerParameterAsProperties(getParameters()));
        objO.checkMandatory();
        objR.setJSJobUtilites(this);

        Object objSp = objJSCommands.getSpoolerObject();
        Spooler objSpooler = (Spooler) objSp;

        String configuration_file = "";

        if (objO.getItem("configuration_file") != null) {
            logger1.debug("configuration_file from param");
            configuration_file = objO.configuration_file.getValue();
        } else {
            logger1.debug("configuration_file from scheduler");
            File f = new File(objSpooler.configuration_directory(), "hibernate.cfg.xml");
            configuration_file = f.getAbsolutePath();

        }

        objO.configuration_file.setValue(configuration_file);

        objO.configuration_file.setValue(configuration_file);
        objR.Execute();
    } // doProcessing

}
