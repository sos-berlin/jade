package com.sos.DataExchange;


import org.junit.Before;
import org.junit.Test;

import com.sos.JSHelper.Options.SOSOptionAuthenticationMethod.enuAuthenticationMethods;
import com.sos.JSHelper.Options.SOSOptionPortNumber;
import com.sos.JSHelper.Options.SOSOptionTransferType.enuTransferTypes;


public class JadeTestsHTTP extends JadeTestBase {

	protected final String			WEB_URI					= "http://www.sos-berlin.com";
	protected final String			WEB_USER				= "";
	protected final String			WEB_PASS				= "";
	protected final String			REMOTE_BASE_PATH		= "timecard/timecard_dialog.php";

	public JadeTestsHTTP() {
		enuSourceTransferType = enuTransferTypes.http;
		enuTargetTransferType = enuTransferTypes.local;
	}

	/**
	 * \brief setUp
	 *
	 * \details
	 *
	 * \return void
	 *
	 * @throws java.lang.Exception
	 */
	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();

		objTestOptions.TargetDir.Value("C:/temp");

		objTestOptions.Target().protocol.Value(enuTargetTransferType);
		//objTestOptions.Target().user.Value("test");
		//objTestOptions.Target().password.Value("12345");
		objTestOptions.Target().host.Value("localhost");

		objTestOptions.Source().protocol.Value(enuSourceTransferType);

		objTestOptions.SourceDir.Value(REMOTE_BASE_PATH);
		objTestOptions.Source().host.Value(WEB_URI);
		objTestOptions.Source().port.value(SOSOptionPortNumber.conPort4http);
		objTestOptions.Source().user.Value(WEB_USER);
		objTestOptions.Source().password.Value(WEB_PASS);
		//objTestOptions.Source().auth_method.Value(enuAuthenticationMethods.url);
	}




	@Test
	public void testDownloadFilePath() throws Exception {
		objTestOptions.SourceDir.Value("Test");
		objTestOptions.Source().host.Value("http://www.sos-berlin.com");
		objTestOptions.Source().port.value(80);
		objTestOptions.Source().user.Value("");
		objTestOptions.Source().password.Value("");
		objTestOptions.Source().auth_method.Value(enuAuthenticationMethods.url);
		//objTestOptions.file_spec.Value(".*");
		objTestOptions.file_path.Value("timecard/timecard_dialog.php");
		objTestOptions.TargetDir.Value("C:/temp");
		//super.testSendFileSpec2();
		super.httpDownloadFile();
	}


}
