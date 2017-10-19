package com.sos.DataExchange.helpers;

import java.nio.file.Paths;
import java.sql.Connection;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sos.scheduler.job.JobSchedulerJobAdapter;

import com.sos.DataExchange.JadeEngine;
import com.sos.DataExchange.Options.JADEOptions;
import com.sos.JSHelper.Exceptions.JobSchedulerException;
import com.sos.exception.SOSYadeSourceConnectionException;
import com.sos.hibernate.classes.ClassList;
import com.sos.hibernate.classes.SOSHibernateFactory;
import com.sos.jade.db.DBItemYadeFiles;
import com.sos.jade.db.DBItemYadeProtocols;
import com.sos.jade.db.DBItemYadeTransfers;

public class YadeDBOperationHelperTest {
    private Logger LOGGER = LoggerFactory.getLogger(YadeDBOperationHelperTest.class);

    @Test
    public void testYadeDBOperationHelper() throws Exception {
        DBItemYadeTransfers transferDBItem = null;
        DBItemYadeProtocols sourceProtocolDBItem = null;
        DBItemYadeProtocols targetProtocolDBItem = null;
        DBItemYadeProtocols jumpProtocolDBItem = null;
        SOSHibernateFactory dbFactory = new SOSHibernateFactory(
                Paths.get("C:/sp/jobschedulers/approvals/jobscheduler_1.12-SNAPSHOT/sp_4012/config/reporting.hibernate.cfg.xml"));
        dbFactory.setIdentifier("YADE");
        dbFactory.setAutoCommit(false);
        ClassList cl = new ClassList();
        cl.add(DBItemYadeFiles.class);
        cl.add(DBItemYadeProtocols.class);
        cl.add(DBItemYadeTransfers.class);
        dbFactory.addClassMapping(cl);
        dbFactory.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        dbFactory.build();
        JADEOptions jadeOptions = new JADEOptions();
        jadeOptions.settings.setValue("C:/sp/testing/yade/local2local.xml");
        jadeOptions.profile.setValue("local2local");
        JadeEngine engine = new JadeEngine(jadeOptions);
        engine.setJobSchedulerEventHandler(new JobSchedulerJobAdapter());
        jadeOptions.setJobSchedulerId("sp_4012");
        jadeOptions.setJobChain("yade_job_chain");
        jadeOptions.setJob("YADEJob");
        jadeOptions.setJobChainNodeName("execute yade job");
        jadeOptions.setOrderId("dummyOrderIdForTesting");
        jadeOptions.setTaskId("32885");
        engine.setDBFactory(dbFactory);
        try {
            engine.execute();
        } catch (Exception e) {
            throw e;
        } finally {
            engine.logout();
        }
    }

    @Test
    public void testExceptionMessageHandling() {
        String test = null;
        try {
            try {
                try {
                    try {
                        LOGGER.info("" + Integer.parseInt(test));
                    } catch (Exception e) {
                        LOGGER.info("*** original Exception message and cause");
                        LOGGER.info("*** Message: " + e.getMessage());
                        LOGGER.info("*** Cause: " + e.getCause());
                        throw new JobSchedulerException(e);
                    }
                } catch (JobSchedulerException e) {
                    LOGGER.info("*** JobSchedulerException message and cause");
                    LOGGER.info("*** Message: " + e.getMessage());
                    LOGGER.info("*** Cause: " + e.getCause());
                    LOGGER.info("*** JobSchedulerException nested Exception message and cause");
                    LOGGER.info("*** Message: " + e.getNestedException().getMessage());
                    LOGGER.info("*** Cause: " + e.getNestedException().getCause());
                    throw new SOSYadeSourceConnectionException(e.getNestedException());
                }
            } catch (SOSYadeSourceConnectionException e) {
                LOGGER.info("*** SOSYadeSourceConnectionException message and cause");
                LOGGER.info("*** Message: " + e.getMessage());
                LOGGER.info("*** Cause: " + e.getCause());
                throw new JobSchedulerException(e);
            }
        } catch (JobSchedulerException e) {
            LOGGER.info("*** outer JobSchedulerException message and cause");
            LOGGER.info("*** Message: " + e.getMessage());
            LOGGER.info("*** Cause: " + e.getCause());
            LOGGER.info("*** outer JobSchedulerException nested Exception message and cause");
            LOGGER.info("*** Message: " + e.getNestedException().getMessage());
            LOGGER.info("*** Cause: " + e.getNestedException().getCause());
        }
    }
}
