package sos.scheduler.jade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sos.JSHelper.Basics.VersionInfo;
import com.sos.JSHelper.Options.SOSOptionJadeOperation;
import com.sos.JSHelper.Options.SOSOptionPortNumber;
import com.sos.JSHelper.Options.SOSOptionTransferType.TransferTypes;
import com.sos.VirtualFileSystem.Options.SOSTransferOptions;
import com.sos.i18n.annotation.I18NResourceBundle;

@I18NResourceBundle(baseName = "com.sos.scheduler.messages", defaultLocale = "en")
public class SFTPSendJob extends Jade4JessyBaseClass {

    private static final Logger LOGGER = LoggerFactory.getLogger(SFTPSendJob.class);

    @Override
    protected void setSpecialOptions() {
        objO.operation.setValue(SOSOptionJadeOperation.enuJadeOperations.send);
        objO.protocol.setValue(TransferTypes.sftp);
        //objO.port.value(SOSOptionPortNumber.getStandardSFTPPort());
        SOSTransferOptions objConn = objO.getTransferOptions();
        if (objConn != null) {
            objConn.getTarget().protocol.setValue(TransferTypes.sftp);
        }
    }

    @Override
    protected void showVersionInfo() {
        LOGGER.debug(VersionInfo.VERSION_STRING);
    }

}