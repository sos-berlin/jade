package sos.scheduler.jade;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sos.DataExchange.JadeEngine;
import com.sos.DataExchange.SOSDataExchangeEngine;
import com.sos.VirtualFileSystem.Options.SOSBaseOptions;
import com.sos.i18n.annotation.I18NResourceBundle;

import sos.scheduler.job.JobSchedulerJobAdapter;

@I18NResourceBundle(baseName = "com.sos.scheduler.messages", defaultLocale = "en")
abstract public class Jade4JessyBaseClass extends JobSchedulerJobAdapter {

    private static final Logger LOGGER = LogManager.getLogger(Jade4JessyBaseClass.class);

    private final String conClassName = this.getClass().getSimpleName();
    @SuppressWarnings("unused")
    private final String conSVNVersion = "$Id: Jade4JessyBaseClass.java 23709 2014-04-11 06:33:04Z sp $";
    protected SOSBaseOptions objO = null;
    protected SOSDataExchangeEngine objR = null;
    protected HashMap<String, String> hsmParameters = null;

    public void init() {
        @SuppressWarnings("unused")
        // $NON-NLS-0$
        final String conMethodName = conClassName + "::init";
        doInitialize();
    }

    private void doInitialize() {
    } // doInitialize

    @Override
    public boolean spooler_init() {
        @SuppressWarnings("unused")
        // $NON-NLS-0$
        final String conMethodName = conClassName + "::spooler_init";
        return super.spooler_init();
    }

    @Override
    public boolean spooler_process() throws Exception {
        @SuppressWarnings("unused")
        // $NON-NLS-0$
        final String conMethodName = conClassName + "::spooler_process";
        try {
            super.spooler_process();
            doProcessing();
            return getSpoolerProcess().isOrderJob();
        } catch (Exception e) {
            LOGGER.error(e.toString(), e);
            return false;
        }
    } // spooler_process

    @Override
    public void spooler_exit() {
        @SuppressWarnings("unused")
        // $NON-NLS-0$
        final String conMethodName = conClassName + "::spooler_exit";
        super.spooler_exit();
    }

    private void doProcessing() throws Exception {
        final String conMethodName = conClassName + "::doProcessing"; //$NON-NLS-1$
        showVersionInfo();
        objR = new JadeEngine();
        objO = objR.getOptions();
        objO.setCurrentNodeName(getCurrentNodeName(getSpoolerProcess().getOrder(), true));
        hsmParameters = getSchedulerParameterAsProperties(getSpoolerProcess().getOrder());
        objO.setAllOptions2(objO.deletePrefix(hsmParameters, "ftp_"));
        objO.checkMandatory();
        setSpecialOptions();
        int intLogLevel = spooler_log.level();
        if (intLogLevel < 0) {
            objO.verbose.value(-1 * intLogLevel);
            LOGGER.debug(objO.toString());
        }
        LOGGER.info(String.format("%1$s with operation %2$s started.", conMethodName, objO.operation.getValue()));
        objR.setJSJobUtilites(this);
        objR.execute();
        objR.logout();
        LOGGER.info(String.format("%1$s with operation %2$s ended.", conMethodName, objO.operation.getValue()));
    } // doProcessing

    abstract protected void setSpecialOptions();

    abstract protected void showVersionInfo();
}
