package sos.scheduler.jade;

import java.util.HashMap;

import org.apache.log4j.Logger;

import sos.scheduler.job.JobSchedulerJobAdapter;

import com.sos.DataExchange.JadeEngine;
import com.sos.DataExchange.SOSDataExchangeEngine;
import com.sos.VirtualFileSystem.Options.SOSFTPOptions;
import com.sos.i18n.annotation.I18NResourceBundle;

/** \file Jade4JessyBaseClass.java \class Jade4JessyBaseClass
 *
 * \brief AdapterClass of SOSDEx for the SOSJobScheduler
 *
 * This Class Jade4JessyBaseClass works as an adapter-class between the SOS
 * JobScheduler and the worker-class SOSDEx.
 *
 * 
 *
 * see \see
 * J:\E\java\development\com.sos.scheduler\src\sos\scheduler\jobdoc\SOSDEx.xml
 * for more details.
 *
 * \verbatim ; mechanicaly created by
 * C:\Users\KB\eclipse\sos.scheduler.xsl\JSJobDoc2JSAdapterClass.xsl from
 * http://www.sos-berlin.com at 20100930175652 \endverbatim */
@I18NResourceBundle(baseName = "com.sos.scheduler.messages", defaultLocale = "en")
abstract public class Jade4JessyBaseClass extends JobSchedulerJobAdapter {

    @SuppressWarnings("unused")
    private final String conClassName = this.getClass().getSimpleName();
    @SuppressWarnings("unused")
    private final Logger logger = Logger.getLogger(this.getClass());
    @SuppressWarnings("unused")
    private final String conSVNVersion = "$Id: Jade4JessyBaseClass.java 23709 2014-04-11 06:33:04Z sp $";
    protected SOSFTPOptions objO = null;
    protected SOSDataExchangeEngine objR = null;
    protected HashMap<String, String> hsmParameters = null;

    public void init() {
        @SuppressWarnings("unused") final String conMethodName = conClassName + "::init"; //$NON-NLS-1$
        doInitialize();
    }

    private void doInitialize() {
    } // doInitialize

    @Override
    public boolean spooler_init() {
        @SuppressWarnings("unused") final String conMethodName = conClassName + "::spooler_init"; //$NON-NLS-1$
        return super.spooler_init();
    }

    @Override
    public boolean spooler_process() throws Exception {
        @SuppressWarnings("unused") final String conMethodName = conClassName + "::spooler_process"; //$NON-NLS-1$
        try {
            super.spooler_process();
            doProcessing();
        } catch (Exception e) {
            logger.error(String.format("%1$s ended abnormal.", conClassName));
            logger.error(StackTrace2String(e));
            return signalFailure();
        } finally {
        } // finally
          // return value for classic and order driven processing
        return signalSuccess();
    } // spooler_process

    @Override
    public void spooler_exit() {
        @SuppressWarnings("unused") final String conMethodName = conClassName + "::spooler_exit"; //$NON-NLS-1$
        super.spooler_exit();
    }

    private void doProcessing() throws Exception {
        final String conMethodName = conClassName + "::doProcessing"; //$NON-NLS-1$
        showVersionInfo();
        objR = new JadeEngine();
        objO = objR.Options();
        objO.CurrentNodeName(getCurrentNodeName());
        hsmParameters = getSchedulerParameterAsProperties(getParameters());
        objO.setAllOptions2(objO.DeletePrefix(hsmParameters, "ftp_"));
        objO.CheckMandatory();
        setSpecialOptions();
        int intLogLevel = spooler_log.level();
        if (intLogLevel < 0) {
            objO.verbose.value(-1 * intLogLevel);
            logger.debug(objO.toString());
        }
        logger.info(String.format("%1$s with operation %2$s started.", conMethodName, objO.operation.Value()));
        objR.setJSJobUtilites(this);
        objR.Execute();
        objR.Logout();
        logger.info(String.format("%1$s with operation %2$s ended.", conMethodName, objO.operation.Value()));
    } // doProcessing

    abstract protected void setSpecialOptions();

    abstract protected void showVersionInfo();
}
