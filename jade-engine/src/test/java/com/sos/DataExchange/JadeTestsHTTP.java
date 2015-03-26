package com.sos.DataExchange;


import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.sos.DataExchange.Options.JADEOptions;


public class JadeTestsHTTP extends JadeTestBase {
	
	public JadeTestsHTTP() {
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
		objOptions = new JADEOptions();
		objOptions.settings.Value("src/test/resources/examples/jade_http_settings.ini");
	}

	@Override
	@After
	public void tearDown() throws Exception{
			
	}

	@Test
	public void testHttp2LocalOneFile() throws Exception {
		objOptions.profile.Value("http_2_local_one_file");
		
		this.execute(objOptions);
	}

	@Test
	public void testHttps2LocalTrustedCertificateOneFile() throws Exception {
		objOptions.profile.Value("https_2_local_trusted_certificate_one_file");
		
		this.execute(objOptions);
	}
	
	@Test
	public void testHttps2LocalSelfSignedCertificateOneFile() throws Exception {
		objOptions.profile.Value("https_2_local_self_signed_certificate_one_file");
		
		this.execute(objOptions);
	}
	
	
	
	@Test
	public void testHttp2LocalMultipleFiles() throws Exception {
		objOptions.profile.Value("http_2_local_multiple_files");
		
		this.execute(objOptions);
	}

	@Test
	public void testHttp2LocalMultiThreaded() throws Exception {
		objOptions.profile.Value("http_2_local_multithreaded");
		
		this.execute(objOptions);
	}
	
	@Test
	public void testHttp2LocalWithAuthentication() throws Exception {
		objOptions.profile.Value("http_2_local_with_authentication");
		
		this.execute(objOptions);
	}
	
	@Test
	public void testHttp2Sftp() throws Exception {
		objOptions.profile.Value("http_2_sftp");
		
		this.execute(objOptions);
	}
	
	@Test
	public void testHttp2localProxy() throws Exception {
		objOptions.profile.Value("http_2_local_proxy");
		
		this.execute(objOptions);
	}
	
	@Test
	public void testHttp2localContentLength() throws Exception {
		objOptions.profile.Value("http_2_local_content_length");
		
		this.execute(objOptions);
	}
	
	private void execute(JADEOptions options) throws Exception{
		try{
			objJadeEngine = new JadeEngine(options);
			objJadeEngine.Execute();
		}
		catch(Exception ex){
			throw ex;
		}
		finally{
			objJadeEngine.Logout();	
		}
	}
}
