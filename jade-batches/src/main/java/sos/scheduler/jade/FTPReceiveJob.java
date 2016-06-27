package sos.scheduler.jade;

import com.sos.JSHelper.Basics.VersionInfo;
import com.sos.JSHelper.Options.SOSOptionAuthenticationMethod.enuAuthenticationMethods;
import com.sos.JSHelper.Options.SOSOptionJadeOperation;
import com.sos.JSHelper.Options.SOSOptionPortNumber;
import com.sos.JSHelper.Options.SOSOptionTransferType.enuTransferTypes;
import com.sos.i18n.annotation.I18NResourceBundle;
import org.apache.log4j.Logger;

/** \file FTPReceiveJob.java \class FTPReceiveJob
 *
 * \brief AdapterClass of SOSDEx for the SOSJobScheduler
 *
 * This Class FTPReceiveJob works as an adapter-class between the SOS
 * JobScheduler and the worker-class SOSDEx.
 *
 * 
 *
 * see \see
 * J:\E\java\development\com.sos.scheduler\src\sos\scheduler\jobdoc\SOSDEx.xml
 * for more details. */
@I18NResourceBundle(baseName = "com_sos_scheduler_messages", defaultLocale = "en")
public class FTPReceiveJob extends Jade4JessyBaseClass {

    @SuppressWarnings("unused")
    private final String conClassName = "FTPReceiveJob";
    private final String conSVNVersion = "$Id: FTPReceiveJob.java 22360 2014-02-05 09:19:04Z oh $";
    private final Logger logger = Logger.getLogger(this.getClass());

    @Override
    protected void setSpecialOptions() {
        objO.protocol.setValue(enuTransferTypes.ftp);
        objO.port.value(SOSOptionPortNumber.getStandardFTPPort());
        objO.operation.setValue(SOSOptionJadeOperation.enuJadeOperations.receive);
        objO.sshAuthMethod.setValue(enuAuthenticationMethods.password);
    }

    @Override
    protected void showVersionInfo() {
        logger.debug(VersionInfo.VERSION_STRING);
        logger.debug(conSVNVersion);
    }
}
