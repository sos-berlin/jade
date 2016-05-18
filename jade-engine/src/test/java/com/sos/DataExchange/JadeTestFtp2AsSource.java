/**
 *
 */
package com.sos.DataExchange;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/** @author KB */
public class JadeTestFtp2AsSource extends JadeTestFtpAsSource {

    /**
	 *
	 */
    public JadeTestFtp2AsSource() {
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        objTestOptions.Source().loadClassName.Value("com.sos.VirtualFileSystem.FTP.SOSVfsFtp2");
    }

    @Override
    @Test
    public void testTransferUsingAbsolutFilePath() throws Exception {
        super.testTransferUsingAbsolutFilePath();
    }

    @Override
    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendWithPolling0Files() throws Exception {
        objTestOptions.pollingServer.value(true);
        objTestOptions.verbosityLevel.value(2);
        objTestOptions.pollMinfiles.value(1);
        objTestOptions.pollingServerDuration.Value("04:30");
        objTestOptions.forceFiles.setFalse();
        super.testSendWithPolling0Files();
    }

    @Override
    @Test
    public void jadeHomer2Local() throws Exception {
        super.jadeHomer2Local();
    }

    @Override
    @Test
    public void testUseProfileWithAsciiMode() throws Exception {
        final String conMethodName = CLASS_NAME + "::testUseProfileWithAsciiMode";
        objOptions.settings.Value(strSettingsFile);
        objOptions.profile.Value("copyWithAsciiMode");
        super.testUseProfileWithoutCreatingTestFiles();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testUseProfileWithOperationReceive() throws Exception {
        final String conMethodName = CLASS_NAME + "::testUseProfileWithOperationReceive";
        objOptions.settings.Value(strSettingsFile);
        objOptions.profile.Value("receive");
        super.testUseProfileWithoutCreatingTestFiles();
    }
}
