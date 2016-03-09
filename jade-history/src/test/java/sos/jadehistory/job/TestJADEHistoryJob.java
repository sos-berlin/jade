package sos.jadehistory.job;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.UUID;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sos.connection.SOSConnection;
import sos.jadehistory.job.JADEHistoryJob;

public class TestJADEHistoryJob {

    public TestJADEHistoryJob() {
        // nothing to do
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        // nothing to do
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        // nothing to do
    }

    @Before
    public void setUp() throws Exception {
        // nothing to do
    }

    @After
    public void tearDown() throws Exception {
        // nothing to do
    }

    @Test
    public void testGUID() throws Exception {
        JADEHistoryJob jadeHistoryJob = new JADEHistoryJob();
        jadeHistoryJob.init();
        jadeHistoryJob.setConnection(SOSConnection.createInstance("SOSOracleConnection", "oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@localhost:1521:test", "test", "test"));
        String randomUUIDString = UUID.randomUUID().toString();
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("guid", randomUUIDString);
        String guid = jadeHistoryJob.getRecordValue(parameters, "mapping_guid");
        assertTrue("guid is not reduced", guid.length() == randomUUIDString.length());
    }

}
