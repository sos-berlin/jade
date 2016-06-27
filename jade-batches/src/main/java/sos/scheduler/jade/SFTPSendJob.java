package sos.scheduler.jade;

import com.sos.JSHelper.Basics.VersionInfo;
import com.sos.JSHelper.Options.SOSOptionJadeOperation;
import com.sos.JSHelper.Options.SOSOptionPortNumber;
import com.sos.JSHelper.Options.SOSOptionTransferType.enuTransferTypes;
import com.sos.VirtualFileSystem.Options.SOSConnection2Options;
import com.sos.i18n.annotation.I18NResourceBundle;
import org.apache.log4j.Logger;

@I18NResourceBundle(baseName = "com.sos.scheduler.messages", defaultLocale = "en")
public class SFTPSendJob extends Jade4JessyBaseClass {

    private static final Logger logger = Logger.getLogger(SFTPSendJob.class);

    @Override
    protected void setSpecialOptions() {
        objO.operation.setValue(SOSOptionJadeOperation.enuJadeOperations.send);
        objO.protocol.setValue(enuTransferTypes.sftp);
        objO.port.value(SOSOptionPortNumber.getStandardSFTPPort());
        SOSConnection2Options objConn = objO.getConnectionOptions();
        if (objConn != null) {
            objConn.getTarget().protocol.setValue(enuTransferTypes.sftp);
        }
    }

    @Override
    protected void showVersionInfo() {
        logger.debug(VersionInfo.VERSION_STRING);
    }

}