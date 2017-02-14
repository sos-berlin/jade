package com.sos.DataExchange;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.ProxySelector;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.Properties;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.io.Util;
import org.apache.commons.net.util.TrustManagerUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import sos.configuration.SOSConfiguration;
import sos.util.SOSLogger;
import sos.util.SOSStandardLogger;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.JSHelper.Options.SOSOptionProxyProtocol;
import com.sos.VirtualFileSystem.FTPS.SOSVfsFtpSProxySelector;

public class JadeTestsFTPS extends JadeTestBase {

    private static final Logger LOGGER = Logger.getLogger(JadeTestsFTPS.class);
    private static final String BASE_PATH = "R://backup//sos//java//development//SOSDataExchange//examples//";
    private JADEOptions options;

    @Before
    public void setUp() throws Exception {
        options = new JADEOptions();
        options.settings.setValue(BASE_PATH + "jade_ftps_settings.ini");
    }

    @Test
    public void testLocal2ExplicitFtps() throws Exception {
        options.profile.setValue("local_2_explicit_ftps");
        this.execute(options);
    }

    @Test
    public void testLocal2ImplicitFtps() throws Exception {
        options.profile.setValue("local_2_implicit_ftps");
        this.execute(options);
    }

    @Test
    public void testLocal2ExplicitFtpsKeystore() throws Exception {
        options.profile.setValue("local_2_explicit_ftps_keystore");
        this.execute(options);
    }

    @Test
    public void testLocal2LocalExplicitFtps() throws Exception {
        options.profile.setValue("local_2_local_explicit_ftps");
        this.execute(options);
    }

    @Test
    public void testLocal2HomerExplicitFtpsSocksProxy() throws Exception {
        options.profile.setValue("local_2_homer_explicit_ftps_socks_proxy");
        this.execute(options);
    }

    @Test
    public void testLocal2HomerImplicitFtpsSocksProxy() throws Exception {
        options.profile.setValue("local_2_homer_implicit_ftps_socks_proxy");
        this.execute(options);
    }

    @Test
    public void testLocal2HomerExplicitFtpsHttpProxy() throws Exception {
        options.profile.setValue("local_2_homer_explicit_ftps_http_proxy");
        this.execute(options);
    }

    @Test
    public void testLocal2LocalFtpExplicitFtpsHttpProxy() throws Exception {
        options.profile.setValue("local_2_local_ftp_explicit_ftps_http_proxy");
        this.execute(options);
    }

    @Test
    public void testLocal2LocalImplicitFtps() throws Exception {
        options.profile.setValue("local_2_local_implicit_ftps");
        this.execute(options);
    }

    private void execute(JADEOptions options) throws Exception {
        try {
            objJadeEngine = new JadeEngine(options);
            objJadeEngine.execute();
        } catch (Exception ex) {
            throw ex;
        } finally {
            objJadeEngine.logout();
        }
    }

    @Test
    public void testLocalClientFTPS() throws Exception {
        LocalFTPSClient cl = new LocalFTPSClient();
        cl.execute();
    }

    public class LocalFTPSClient {

        public void execute() throws Exception {
            SOSLogger l = new SOSStandardLogger(0);
            SOSConfiguration confFtps = new SOSConfiguration(BASE_PATH + "ftps/not_jade_ftps_setting.ini", "homer_ftps");
            Properties propertiesFtps = confFtps.getParameterAsProperties();
            SOSConfiguration confProxySocks5 = new SOSConfiguration(BASE_PATH + "ftps/not_jade_ftps_setting.ini", "homer_proxy_socks5");
            Properties propertiesProxySocks5 = confProxySocks5.getParameterAsProperties();
            SOSConfiguration confProxyHttp = new SOSConfiguration(BASE_PATH + "ftps/not_jade_ftps_setting.ini", "homer_proxy_http");
            Properties propertiesProxyHttp = confProxyHttp.getParameterAsProperties();
            FTPSClient cl = null;
            String host = propertiesFtps.getProperty("host");
            int port = Integer.parseInt(propertiesFtps.getProperty("port_explicit"));
            String user = propertiesFtps.getProperty("user");
            String pass = propertiesFtps.getProperty("password");
            String source = propertiesFtps.getProperty("source_file");
            String target = propertiesFtps.getProperty("target_file");
            File keyStore = new File(propertiesFtps.getProperty("key_store_file"));
            String keyStorePass = propertiesFtps.getProperty("key_store_password");
            boolean implicit = false;
            boolean useHttpProxy = true;
            boolean useSystemProperties = false;
            boolean useProxySelector = false;
            boolean useProxy = true;
            boolean useKeyStore = false;
            SOSOptionProxyProtocol.Protocol proxyProtocol = SOSOptionProxyProtocol.Protocol.socks5;
            String proxyHost = propertiesProxySocks5.getProperty("host");
            int proxyPort = Integer.parseInt(propertiesProxySocks5.getProperty("port"));
            String proxyUser = propertiesProxySocks5.getProperty("user");
            String proxyPassword = propertiesProxySocks5.getProperty("password");
            try {
                if (implicit) {
                    port = Integer.parseInt(propertiesFtps.getProperty("port_implicit"));
                }
                if (useHttpProxy) {
                    proxyProtocol = SOSOptionProxyProtocol.Protocol.http;
                    proxyPort = Integer.parseInt(propertiesProxyHttp.getProperty("port"));
                    proxyUser = propertiesProxyHttp.getProperty("user");
                    proxyPassword = propertiesProxyHttp.getProperty("password");
                }
                if (useSystemProperties) {
                    setSystemProperties(useHttpProxy, propertiesProxyHttp, propertiesProxySocks5);
                }
                if (useProxySelector) {
                    SOSVfsFtpSProxySelector ps = new SOSVfsFtpSProxySelector(proxyProtocol, proxyHost, proxyPort, proxyUser, proxyPassword);
                    ProxySelector.setDefault(ps);
                }
                cl = new FTPSClient("SSL", implicit);
                if (useKeyStore) {
                    setKeyManager(cl, keyStore, keyStorePass);
                }
                if (useProxy) {
                    Proxy proxy =
                            (useHttpProxy) ? getHTTPProxy(proxyHost, proxyPort, proxyUser, proxyPassword) : getSocksProxy(proxyHost, proxyPort,
                                    proxyUser, proxyPassword);
                    cl.setProxy(proxy);
                }
                LOGGER.info("do Connect");
                cl.connect(host, port);
                cl.setControlKeepAliveTimeout(180);
                cl.setBufferSize(1000);
                int reply = cl.getReplyCode();
                if (FTPReply.isPositiveCompletion(reply)) {
                    LOGGER.info("do Login");
                    if (cl.login(user, pass)) {
                        cl.setFileType(FTP.BINARY_FILE_TYPE);
                        // Set protection buffer size
                        cl.execPBSZ(0);
                        // Set data channel protection to private
                        cl.execPROT("P");
                        LOGGER.info("do enterLocalPassiveMode");
                        // Enter local passive mode
                        cl.enterLocalPassiveMode();
                        LOGGER.info("do deleteFile");
                        cl.deleteFile("/home/sos/jade/to_homer/.in.re_unitest.pdf.");
                        cl.deleteFile(target);
                        cl.cwd("/home/sos/jade/to_homer");
                        LOGGER.info("cwd replay = " + cl.getReplyCode() + " = " + cl.getReplyString());
                    }
                }
                LOGGER.info("END");
            } catch (Exception ex) {
                throw ex;
            } finally {
                if (cl != null) {
                    try {
                        cl.logout();
                    } catch (Exception ex) {
                    }
                    try {
                        cl.disconnect();
                    } catch (Exception ex) {
                    }
                    if (useSystemProperties) {
                        clearSystemProperties(useHttpProxy);
                    }
                }
                LOGGER.info("Finally");
            }
        }

        private KeyStore loadStore(String storeType, File storePath, String storePass) throws KeyStoreException, IOException,
                GeneralSecurityException {
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

        private void setKeyManager(FTPSClient cl, File keyStore, String keyStorePass) throws Exception {
            KeyStore ks = loadStore("JKS", keyStore, keyStorePass);
            cl.setTrustManager(TrustManagerUtils.getDefaultTrustManager(ks));
        }

        private Proxy getHTTPProxy(String proxyHost, int proxyPort, String proxyUser, String proxyPassword) {
            Authenticator.setDefault(new Authenticator() {

                protected PasswordAuthentication getPasswordAuthentication() {
                    PasswordAuthentication p = new PasswordAuthentication(proxyUser, proxyPassword.toCharArray());
                    return p;
                }
            });
            return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
        }

        private Proxy getSocksProxy(String proxyHost, int proxyPort, String proxyUser, String proxyPassword) {
            Authenticator.setDefault(new Authenticator() {

                protected PasswordAuthentication getPasswordAuthentication() {
                    PasswordAuthentication p = new PasswordAuthentication(proxyUser, proxyPassword.toCharArray());
                    return p;
                }
            });
            return new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyHost, proxyPort));
        }

        private void setSystemProperties(boolean useHttpProxy, Properties http, Properties socks5) {
            if (useHttpProxy) {
                System.getProperties().put("https.proxyHost", http.getProperty("host"));
                System.getProperties().put("https.proxyPort", http.getProperty("port"));
                System.getProperties().put("https.proxyUser", http.getProperty("user"));
                System.getProperties().put("https.proxyPassword", http.getProperty("password"));
                System.getProperties().put("http.proxySet", "true");
                System.getProperties().put("http.proxyHost", http.getProperty("host"));
                System.getProperties().put("http.proxyPort", http.getProperty("port"));
                System.getProperties().put("http.proxyUser", http.getProperty("user"));
                System.getProperties().put("http.proxyPassword", http.getProperty("password"));
                System.getProperties().put("http.nonProxyHosts", "localhost|127.0.0.1");
                System.getProperties().put("ftp.proxySet", "true");
                System.getProperties().put("ftp.proxyHost", http.getProperty("host"));
                System.getProperties().put("ftp.proxyPort", http.getProperty("port"));
                System.getProperties().put("ftp.proxyUser", http.getProperty("user"));
                System.getProperties().put("ftp.proxyPassword", http.getProperty("password"));
                System.getProperties().put("ftp.nonProxyHosts", "localhost|127.0.0.1");
                System.getProperties().put("ftps.proxySet", "true");
                System.getProperties().put("ftps.proxyHost", http.getProperty("host"));
                System.getProperties().put("ftps.proxyPort", http.getProperty("port"));
                System.getProperties().put("ftps.proxyUser", http.getProperty("user"));
                System.getProperties().put("ftps.proxyPassword", http.getProperty("password"));
                System.getProperties().put("ftps.nonProxyHosts", "localhost|127.0.0.1");
                Authenticator.setDefault(new Authenticator() {

                    protected PasswordAuthentication getPasswordAuthentication() {
                        PasswordAuthentication p = new PasswordAuthentication(http.getProperty("user"), http.getProperty("password").toCharArray());
                        return p;
                    }
                });
            } else {
                System.getProperties().put("socksProxyHost", socks5.getProperty("host"));
                System.getProperties().put("socksProxyPort", socks5.getProperty("port"));
                System.getProperties().put("socksProxyUser", socks5.getProperty("user"));
                System.getProperties().put("socksProxyPassword", socks5.getProperty("password"));
                Authenticator.setDefault(new Authenticator() {

                    protected PasswordAuthentication getPasswordAuthentication() {
                        PasswordAuthentication p =
                                new PasswordAuthentication(socks5.getProperty("user"), socks5.getProperty("password").toCharArray());
                        return p;
                    }
                });
            }
        }

        private void clearSystemProperties(boolean useHttpProxy) {
            if (useHttpProxy) {
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
            } else {
                System.clearProperty("socksProxyHost");
                System.clearProperty("socksProxyPort");
                System.clearProperty("socksProxyUser");
                System.clearProperty("socksProxyPassword");
            }
        }
    }

}