package com.sos.DataExchange;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.ProxySelector;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.io.Util;
import org.apache.commons.net.util.TrustManagerUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.JSHelper.Options.SOSOptionProxyProtocol;
import com.sos.VirtualFileSystem.FTPS.SOSVfsFtpSProxySelector;


public class JadeTestsFTPS extends JadeTestBase{
	private final Logger	logger			= Logger.getLogger(JadeTestsFTPS.class);

	private JADEOptions options;
	
	public JadeTestsFTPS() {
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		options = new JADEOptions();
		options.settings.Value("src/test/resources/examples/jade_ftps_settings.ini");
	}

	@After
	public void tearDown() throws Exception{
			
	}

	@Test
	public void testLocal2ExplicitFtps() throws Exception {
		options.profile.Value("local_2_explicit_ftps");
		
		this.execute(options);
	}
	
	@Test
	public void testLocal2ImplicitFtps() throws Exception {
		options.profile.Value("local_2_implicit_ftps");
		
		this.execute(options);
	}
	
	@Test
	public void testLocal2ExplicitFtpsKeystore() throws Exception {
		options.profile.Value("local_2_explicit_ftps_keystore");
		
		this.execute(options);
	}
	
	@Test
	public void testLocal2LocalExplicitFtps() throws Exception {
		options.profile.Value("local_2_local_explicit_ftps");
		
		this.execute(options);
	}
	
	
	
	@Test
	public void testLocal2HomerExplicitFtpsSocksProxy() throws Exception {
		options.profile.Value("local_2_homer_explicit_ftps_socks_proxy");
		
		this.execute(options);
	}
	
	
	@Test
	public void testLocal2LocalImplicitFtps() throws Exception {
		options.profile.Value("local_2_local_implicit_ftps");
		
		this.execute(options);
	}
	
	@Test
	public void testLocal2ftp() throws Exception {
		options.profile.Value("local_2_ftp");
		
		this.execute(options);
	}
	
	/**
	 * 
	 * @param options
	 * @throws Exception
	 */
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
	
	
	
	@Test
	@Ignore
	public void testLocalClientFTPS() throws Exception{
		LocalFTPSClient cl = new LocalFTPSClient();
		cl.execute();
	}
		
	/**
	 * 
	 * @author Robert Ehrlich
	 *
	 */
	public class LocalFTPSClient{
		
		/**
		 * 
		 * @throws Exception
		 */
		public void execute() throws Exception{
			FTPSClient cl = null;
			
			String host = "homer.sos";
			int port = 21;
			String user = "sos";
			String pass = "sos";
			String source = "R:/nobackup/junittests/testdata/JADE_Target_Dir/JADE.Manual.pdf";
			String target = "/home/sos/jade/to_homer/re_unitest.pdf";
			target = "re_unitest.pdf";
			
			File keyStore = new File("D:/_temp/ftps/keys/1/keystore.jks");
			String keyStorePass = "password";
			
			
			////-Djava.net.preferIPv4Stack=true
			//System.setProperty("java.net.preferIPv4Stack", "true");
			
			boolean implicit = false;
			boolean useHttpProxy = false;
				
			boolean useSystemProperties = false;
			boolean useProxySelector = true;
			boolean useProxy = false;
			
			boolean useKeyStore = true;
			
			SOSOptionProxyProtocol.Protocol proxyProtocol = SOSOptionProxyProtocol.Protocol.socks5;
			String proxyHost = "homer.sos";
			int proxyPort = 1080;
			String proxyUser = "sos";
			String proxyPassword = "sos";
			
			try{
				if(implicit == true){
					port = 990;
				}
				if(useHttpProxy){
					proxyProtocol = SOSOptionProxyProtocol.Protocol.http;
					proxyPort = 3128;
					proxyUser = "proxy_user";
					proxyPassword = "12345";
				}
				
				if(useSystemProperties){
					setSystemProperties(useHttpProxy);
				}
				
				if(useProxySelector){
					SOSVfsFtpSProxySelector ps = new SOSVfsFtpSProxySelector(proxyProtocol,
						proxyHost,
						proxyPort,
						proxyUser,
						proxyPassword);
					//ps.select(new URI("http://"+proxyHost));
					ProxySelector.setDefault(ps);
				}
				
				cl = new FTPSClient("SSL",implicit);
				//all|valid|none
				//setTrustManager(cl,"none");
				if(useKeyStore){
					setKeyManager(cl, keyStore, keyStorePass);
				}
				
				if(useProxy){
					Proxy p = (useHttpProxy) ? getHTTPProxy(proxyHost, proxyPort) : getSocksProxy(proxyHost, proxyPort);
					cl.setProxy(p);
				}
				
				logger.info("do Connect");
		    	cl.connect(host, port);
				cl.setControlKeepAliveTimeout(180);
				cl.setBufferSize(1000);
				
				int reply = cl.getReplyCode();
			    if (FTPReply.isPositiveCompletion(reply)) {
			    	logger.info("do Login");
			    	if (cl.login(user, pass)) {
			    		cl.setFileType(FTP.BINARY_FILE_TYPE);
			            
			    		// Set protection buffer size
			            cl.execPBSZ(0);
			            // Set data channel protection to private
			            cl.execPROT("P");
			            //cl.execPROT(FTPSClient.DEFAULT_PROT);
			            logger.info("do enterLocalPassiveMode");
			            // Enter local passive mode
			            cl.enterLocalPassiveMode();
			            
			            logger.info("do deleteFile");
			            
			            cl.deleteFile("/home/sos/jade/to_homer/.in.re_unitest.pdf.");
			            cl.deleteFile(target);
			            
			            cl.cwd("/home/sos/jade/to_homer");
			            logger.info("cwd replay = "+cl.getReplyCode()+" = "+cl.getReplyString());
								            
			            storeFileAsStream(cl, source, target);
			            //storeFile(cl, source, target);
			            logger.info("copied to "+target);
			    	}
			    }
			    
			    logger.info("END");
			}
			catch(Exception ex){
				throw ex;
			}
			finally{
				if(cl != null){
					try{cl.logout();}catch(Exception ex){}
					try{cl.disconnect();}catch(Exception ex){}
				
					if(useSystemProperties){
						clearSystemProperties(useHttpProxy);
					}
				}
			}
		}
		
		/**
		 * 
		 * @param storeType
		 * @param storePath
		 * @param storePass
		 * @return
		 * @throws KeyStoreException
		 * @throws IOException
		 * @throws GeneralSecurityException
		 */
		private KeyStore loadStore(String storeType, File storePath, String storePass)
		        throws KeyStoreException,  IOException, GeneralSecurityException {
		        KeyStore ks = KeyStore.getInstance(storeType);
		        FileInputStream stream = null;
		        try {
		            stream = new FileInputStream(storePath);
		            ks.load(stream, storePass.toCharArray());
		        } finally {
		        	Util.closeQuietly(stream);
		        }
		        return ks;
		    }
		
		/**
		 * 
		 * @param cl
		 * @param trustmgr
		 */
		private void setTrustManager(FTPSClient cl, String trustmgr){
			if ("all".equals(trustmgr)) {
	            cl.setTrustManager(TrustManagerUtils.getAcceptAllTrustManager());
	        } else if ("valid".equals(trustmgr)) {
	            cl.setTrustManager(TrustManagerUtils.getValidateServerCertificateTrustManager());
	        } else if ("none".equals(trustmgr)) {
	            cl.setTrustManager(null);
	        }
		}
		
		private void setKeyManager(FTPSClient cl, File keyStore, String keyStorePass) throws Exception{
			//KeyManager keyManager = org.apache.commons.net.util.KeyManagerUtils.createClientKeyManager(keyStore, keyStorePass);
			//cl.setKeyManager(keyManager);
			// cl.setTrustManager(TrustManagerUtils.getDefaultTrustManager(keyStore).getValidateServerCertificateTrustManager());
		
			KeyStore ks = loadStore("JKS", keyStore, keyStorePass);
			cl.setTrustManager(TrustManagerUtils.getDefaultTrustManager(ks));
		}
		
		/**
		 * 
		 * @return
		 * @throws NoSuchAlgorithmException
		 * @throws KeyManagementException
		 */
		private SSLContext getSSLContext() throws NoSuchAlgorithmException, KeyManagementException{
			SSLContext sslContext = SSLContext.getInstance("SSL");

			// set up a TrustManager that trusts everything
			sslContext.init(null, new TrustManager[] { new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					System.out.println("getAcceptedIssuers =============");
					return null;
				}

				public void checkClientTrusted(X509Certificate[] certs,
						String authType) {
					System.out.println("checkClientTrusted =============");
				}

				public void checkServerTrusted(X509Certificate[] certs,
						String authType) {
					System.out.println("checkServerTrusted =============");
				}
			} }, new SecureRandom());

			
			return sslContext;
		}
		
		/**
		 * 
		 * @param proxyHost
		 * @param proxyPort
		 * @return
		 */
		private Proxy getHTTPProxy(String proxyHost,int proxyPort){
			Authenticator.setDefault(new Authenticator(){
				  protected  PasswordAuthentication  getPasswordAuthentication(){
					   PasswordAuthentication p = new PasswordAuthentication("proxy_user","12345".toCharArray());
					   return p;
				  }
				});
			
			return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost,proxyPort));
		}
		
		/**
		 * 
		 * @param proxyHost
		 * @param proxyPort
		 * @return
		 */
		private Proxy getSocksProxy(String proxyHost,int proxyPort){
			Authenticator.setDefault(new Authenticator(){
				  protected  PasswordAuthentication  getPasswordAuthentication(){
					   PasswordAuthentication p = new PasswordAuthentication("sos","sos".toCharArray());
					   return p;
				  }
				});
			
			return new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyHost,proxyPort));
		}
		
		/**
		 * 
		 * @param useHttpProxy
		 */
		private void setSystemProperties(boolean useHttpProxy){
			if(useHttpProxy){
				System.getProperties().put("https.proxyHost", "homer.sos");
				System.getProperties().put("https.proxyPort", "3128");
				System.getProperties().put("https.proxyUser", "proxy_user");
				System.getProperties().put("https.proxyPassword", "12345");
				
				System.getProperties().put("http.proxySet", "true");
				System.getProperties().put("http.proxyHost", "homer.sos");
				System.getProperties().put("http.proxyPort", "3128");
				System.getProperties().put("http.proxyUser", "proxy_user");
				System.getProperties().put("http.proxyPassword", "12345");
				System.getProperties().put("http.nonProxyHosts", "localhost|RE-DELL|127.0.0.1");
				
				System.getProperties().put("ftp.proxySet", "true");
				System.getProperties().put("ftp.proxyHost", "homer.sos");
				System.getProperties().put("ftp.proxyPort", "3128");
				System.getProperties().put("ftp.proxyUser", "proxy_user");
				System.getProperties().put("ftp.proxyPassword", "12345");
				System.getProperties().put("ftp.nonProxyHosts", "localhost|RE-DELL|127.0.0.1");
							
				System.getProperties().put("ftps.proxySet", "true");
				System.getProperties().put("ftps.proxyHost", "homer.sos");
				System.getProperties().put("ftps.proxyPort", "3128");
				System.getProperties().put("ftps.proxyUser", "proxy_user");
				System.getProperties().put("ftps.proxyPassword", "12345");
				System.getProperties().put("ftps.nonProxyHosts", "localhost|RE-DELL|127.0.0.1");
				
				Authenticator.setDefault(new Authenticator(){
					  protected  PasswordAuthentication  getPasswordAuthentication(){
						   PasswordAuthentication p = new PasswordAuthentication("proxy_user","12345".toCharArray());
						   return p;
					  }
					});

			}
			else{
				System.getProperties().put( "socksProxyHost" ,"homer.sos");
				System.getProperties().put( "socksProxyPort", "1080");
				System.getProperties().put( "socksProxyUser", "sos");
				System.getProperties().put( "socksProxyPassword" ,"sos");
				
				Authenticator.setDefault(new Authenticator(){
					  protected  PasswordAuthentication  getPasswordAuthentication(){
						   PasswordAuthentication p = new PasswordAuthentication("sos","sos".toCharArray());
						   return p;
					  }
					});
			}
		}
		
		/**
		 * 
		 * @param useHttpProxy
		 */
		private void clearSystemProperties(boolean useHttpProxy){
			if(useHttpProxy){
				
				System.clearProperty("https.proxyHost");
				System.clearProperty("https.proxyPort");
				System.clearProperty("https.proxyUser");
				System.clearProperty("https.proxyPassword");
				
				System.clearProperty("http.proxySet");
				System.clearProperty("http.proxyHost");
				System.clearProperty("http.proxyPort");
				System.clearProperty("http.proxyUser");
				System.clearProperty("http.proxyPassword");
				System.clearProperty("http.nonProxyHosts");			
				
				System.clearProperty("ftp.proxySet");
				System.clearProperty("ftp.proxyHost");
				System.clearProperty("ftp.proxyPort");
				System.clearProperty("ftp.proxyUser");
				System.clearProperty("ftp.proxyPassword");
				System.clearProperty("ftp.nonProxyHosts");
				
				System.clearProperty("ftps.proxySet");
				System.clearProperty("ftps.proxyHost");
				System.clearProperty("ftps.proxyPort");
				System.clearProperty("ftps.proxyUser");
				System.clearProperty("ftps.proxyPassword");
				System.clearProperty("ftps.nonProxyHosts");
			}
			else{
				System.clearProperty("socksProxyHost");
				System.clearProperty("socksProxyPort");
				System.clearProperty("socksProxyUser");
				System.clearProperty("socksProxyPassword");
			}
		}
		
		/**
		 * 
		 * @param cl
		 * @param source
		 * @param target
		 * @throws Exception
		 */
		private void storeFile(FTPSClient cl,String source, String target) throws Exception{
			InputStream input = null;
			try{
				input = new FileInputStream(source);
	        	cl.storeFile(target, input);
			}
			catch(Exception ex){
				throw ex;
			}
			finally{
				if(input != null){
					input.close();
				}
			}
		}
		
		/**
		 * bei proftp
		 * 
		 * TLSOptions                 NoCertRequest NoSessionReuseRequired
		 * 
		 * ohne NoSessionReuseRequired wird socket write error ausgelöst
		 * 
		 * @param cl
		 * @param source
		 * @param target
		 * @throws Exception
		 */
		private void storeFileAsStream(FTPSClient cl,String source, String target) throws Exception{
			String method = "storeFileAsStream";
			
			InputStream input = null;
			OutputStream os = null;
			
			try{
				
				input = new FileInputStream(source);
				os = cl.storeFileStream(target); //cl.storeFileStream(target);
				logger.info(method+": os = "+os+" = "+target);
				
				logger.info("replay = "+cl.getReplyCode()+" = "+cl.getReplyString());
				if(os == null){
					throw new Exception("null for "+target);
				}
				
				byte[] buf = new byte[1024];
	            int len;
	            while ((len = input.read(buf)) > 0) {
	                 os.write(buf, 0, len);
	                 //os.flush();
	            }
	            //cl.completePendingCommand();

			}
			catch(Exception ex){
				throw ex;
			}
			finally{
				if(input != null){
					input.close();
				}
				if(os != null){
					os.flush();
					os.close();
				}
			}
		}
	}
}
