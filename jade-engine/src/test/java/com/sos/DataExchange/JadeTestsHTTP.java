package com.sos.DataExchange;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpHost;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.TraceMethod;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.io.Files;
import com.sos.DataExchange.Options.JADEOptions;
import com.sos.VirtualFileSystem.HTTP.SOSVfsHTTPRequestEntity;

public class JadeTestsHTTP extends JadeTestBase {

    @Override
    @Before
    public void setUp() throws Exception {
        objOptions = new JADEOptions();
        objOptions.settings.setValue("src/test/resources/examples/jade_http_settings.ini");
    }

    @Test
    public void testHttp2LocalOneFile() throws Exception {
        objOptions.profile.setValue("http_2_local_one_file");
        this.execute(objOptions);
    }
    
    @Test
    @Ignore
    public void testHttpClientPutMethod() throws Exception {

        HostConfiguration hc = new HostConfiguration();
        hc.setHost(new HttpHost("localhost", 8080));

        MultiThreadedHttpConnectionManager cm = new MultiThreadedHttpConnectionManager();
        HttpClient httpClient = new HttpClient(cm);
        httpClient.setHostConfiguration(hc);

        File file = new File("D://yade_test.txt");
        String uri = "http://localhost:8080/yade_http/" + file.getName();
        
        PutMethod m = null;
        InputStream source = null;
        OutputStream target = null;
        try {
            m = new PutMethod(uri);
            SOSVfsHTTPRequestEntity re = new SOSVfsHTTPRequestEntity();
            m.setRequestEntity(re);
            target = re.getOutputStream();

            byte[] buffer = new byte[objOptions.bufferSize.value()];
            int bytesTransferred;
            source = Files.asByteSource(file).openStream();
            while ((bytesTransferred = source.read(buffer)) != -1) {

                target.write(buffer, 0, bytesTransferred);
            }
            source.close();
            source = null;

            target.flush();
            target.close();
            target = null;

            int statusCode = httpClient.executeMethod(m);
            System.out.println("statusCode=" + statusCode + ", statusText=" + m.getStatusText());

        } catch (Exception e) {
            throw e;
        } finally {
            if (source != null) {
                source.close();
            }
            if (target != null) {
                target.flush();
                target.close();
            }
            if (m != null) {
                m.releaseConnection();
            }
            cm.shutdown();
        }
    }

    @Test
    @Ignore
    public void testHttpClientTraceMethod() throws Exception {

        HostConfiguration hc = new HostConfiguration();
        hc.setHost(new HttpHost("localhost", 8080));

        MultiThreadedHttpConnectionManager cm = new MultiThreadedHttpConnectionManager();
        HttpClient httpClient = new HttpClient(cm);
        httpClient.setHostConfiguration(hc);

        String uri = "http://localhost:8080/yade_http";
        TraceMethod m = null;
        try {
            m = new TraceMethod(uri);
            int statusCode = httpClient.executeMethod(m);
            System.out.println("statusCode=" + statusCode + ", statusText=" + m.getStatusText());

        } catch (Exception e) {
            throw e;
        } finally {
            if (m != null) {
                m.releaseConnection();
            }
            cm.shutdown();
        }
    }
    
    @Test
    @Ignore
    public void testHttpClientDeleteMethod() throws Exception {

        HostConfiguration hc = new HostConfiguration();
        hc.setHost(new HttpHost("localhost", 8080));

        MultiThreadedHttpConnectionManager cm = new MultiThreadedHttpConnectionManager();
        HttpClient httpClient = new HttpClient(cm);
        httpClient.setHostConfiguration(hc);

        String uri = "http://localhost:8080/yade_http/yade_test.txt";
        DeleteMethod m = null;
        try {
            m = new DeleteMethod(uri);
            int statusCode = httpClient.executeMethod(m);
            System.out.println("statusCode=" + statusCode + ", statusText=" + m.getStatusText());

        } catch (Exception e) {
            throw e;
        } finally {
            if (m != null) {
                m.releaseConnection();
            }
            cm.shutdown();
        }
    }
    
    @Test
    public void testHttps2LocalTrustedCertificateOneFile() throws Exception {
        objOptions.profile.setValue("https_2_local_trusted_certificate_one_file");
        this.execute(objOptions);
    }

    @Test
    public void testHttps2LocalSelfSignedCertificateOneFile() throws Exception {
        objOptions.profile.setValue("https_2_local_self_signed_certificate_one_file");
        this.execute(objOptions);
    }

    @Test
    public void testHttp2LocalMultipleFiles() throws Exception {
        objOptions.profile.setValue("http_2_local_multiple_files");
        this.execute(objOptions);
    }

    @Test
    public void testHttp2LocalMultiThreaded() throws Exception {
        objOptions.profile.setValue("http_2_local_multithreaded");
        this.execute(objOptions);
    }

    @Test
    public void testHttp2LocalWithAuthentication() throws Exception {
        objOptions.profile.setValue("http_2_local_with_authentication");
        this.execute(objOptions);
    }

    @Test
    public void testHttp2Sftp() throws Exception {
        objOptions.profile.setValue("http_2_sftp");
        this.execute(objOptions);
    }

    @Test
    public void testHttp2localProxy() throws Exception {
        objOptions.profile.setValue("http_2_local_proxy");
        this.execute(objOptions);
    }

    @Test
    public void testHttp2localContentLength() throws Exception {
        objOptions.profile.setValue("http_2_local_content_length");
        this.execute(objOptions);
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
}
