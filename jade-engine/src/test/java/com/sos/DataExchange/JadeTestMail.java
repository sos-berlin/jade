package com.sos.DataExchange;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.sos.JSHelper.Options.SOSOptionTransferType.enuTransferTypes;

/** @author KB */
public class JadeTestMail extends JadeTestBase {

    private final String strSettingsFile = "R:/backup/sos/java/development/SOSDataExchange/examples/jade_mail_settings.ini";

    /**
	 *
	 */
    public JadeTestMail() {
    }

    /** \brief setUp
     *
     * \details
     *
     * \return void
     *
     * @throws java.lang.Exception */
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        enuSourceTransferType = enuTransferTypes.local;
        enuTargetTransferType = enuTransferTypes.local;

        objTestOptions.sourceDir.setValue(strTestPathName);
        objTestOptions.targetDir.setValue(strTestPathName + "/SOSMDX/");

        objTestOptions.getSource().protocol.setValue(enuSourceTransferType);
        objTestOptions.getTarget().protocol.setValue(enuTargetTransferType);
    }

    @Test
    public void testMailWithNotification() throws Exception {
        // use file_notification_* params
        final String conMethodName = CLASS_NAME + "::testMailWithNotification";
        objOptions.settings.setValue(strSettingsFile);
        objOptions.profile.setValue("copy_files_with_notification");
        super.testUseProfile();
    }

    @Test
    public void testMailOnSuccess() throws Exception {
        // use mail_on_success_* params
        final String conMethodName = CLASS_NAME + "::testMailOnSuccess";
        objOptions.settings.setValue(strSettingsFile);
        objOptions.profile.setValue("copy_files_on_success");
        super.testUseProfile();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testMailOnError() throws Exception {
        // use mail_on_error_* params
        final String conMethodName = CLASS_NAME + "::testMailOnError";
        objOptions.settings.setValue(strSettingsFile);
        objOptions.profile.setValue("copy_files_on_error");
        super.testUseProfile();
    }

    @Test
    public void testMailOnErrorButNoErrorOccurs() throws Exception {
        // use mail_on_error_* params
        final String conMethodName = CLASS_NAME + "::testMailOnErrorButNoErrorOccurs";
        objOptions.settings.setValue(strSettingsFile);
        objOptions.profile.setValue("copy_files_without_error_and_mail_on_error");
        super.testUseProfile();
    }
}
