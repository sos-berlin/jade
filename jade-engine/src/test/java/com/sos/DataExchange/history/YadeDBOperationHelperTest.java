package com.sos.DataExchange.history;

import java.nio.file.Paths;
import java.sql.Connection;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sos.DataExchange.SOSDataExchangeEngine;
import com.sos.JSHelper.Exceptions.JobSchedulerException;
import com.sos.JSHelper.Options.SOSOptionAuthenticationMethod;
import com.sos.JSHelper.Options.SOSOptionTransferType.TransferTypes;
import com.sos.exception.SOSYadeSourceConnectionException;
import com.sos.hibernate.classes.ClassList;
import com.sos.hibernate.classes.SOSHibernateFactory;
import com.sos.jade.db.DBItemYadeFiles;
import com.sos.jade.db.DBItemYadeProtocols;
import com.sos.jade.db.DBItemYadeTransfers;
import com.sos.vfs.common.options.SOSBaseOptions;

public class YadeDBOperationHelperTest {

    private Logger LOGGER = LoggerFactory.getLogger(YadeDBOperationHelperTest.class);

    @Test
    public void testYadeDBOperationHelper() throws Exception {
        DBItemYadeTransfers transferDBItem = null;
        DBItemYadeProtocols sourceProtocolDBItem = null;
        DBItemYadeProtocols targetProtocolDBItem = null;
        DBItemYadeProtocols jumpProtocolDBItem = null;
        SOSBaseOptions jadeOptions = new SOSBaseOptions();
        jadeOptions.settings.setValue("C:/sp/jobschedulers/approvals/jobscheduler_1.12-SNAPSHOT/sp_4012/config/live/YADE-463/YADE-463.ini");
        jadeOptions.profile.setValue("homer");
        SOSDataExchangeEngine engine = new SOSDataExchangeEngine(jadeOptions);
        jadeOptions.setJobSchedulerId("sp_4012");
        jadeOptions.setJobChain("yade_job_chain");
        jadeOptions.setJob("YADEJob");
        jadeOptions.setJobChainNodeName("execute yade job");
        jadeOptions.setOrderId("dummyOrderIdForTesting");
        jadeOptions.setTaskId("32885");
        YadeHistory history = new YadeHistory(null);
        engine.setJobSchedulerEventHandler(history);
        history.buildFactory(Paths.get("C:/sp/jobschedulers/approvals/jobscheduler_1.12-SNAPSHOT/sp_4012/config/reporting.hibernate.cfg.xml"));
        try {
            engine.execute();
        } catch (Exception e) {
            throw e;
        } finally {
            engine.disconnect();
        }
    }

    @Test
    public void testYadeDBOperationHelperWithFixConfig() throws Exception {
        DBItemYadeTransfers transferDBItem = null;
        DBItemYadeProtocols sourceProtocolDBItem = null;
        DBItemYadeProtocols targetProtocolDBItem = null;
        DBItemYadeProtocols jumpProtocolDBItem = null;
        SOSHibernateFactory dbFactory = new SOSHibernateFactory(Paths.get(
                "C:/sp/jobschedulers/approvals/jobscheduler_1.12-SNAPSHOT/sp_4012/config/reporting.hibernate.cfg.xml"));
        dbFactory.setIdentifier("YADE");
        dbFactory.setAutoCommit(false);
        ClassList cl = new ClassList();
        cl.add(DBItemYadeFiles.class);
        cl.add(DBItemYadeProtocols.class);
        cl.add(DBItemYadeTransfers.class);
        dbFactory.addClassMapping(cl);
        dbFactory.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        dbFactory.build();
        SOSBaseOptions jadeOptions = new SOSBaseOptions();
        // jadeOptions.settings.setValue("C:/sp/testing/yade/local2local.xml");
        // jadeOptions.profile.setValue("local2local");
        jadeOptions.getSource().authMethod.setValue(SOSOptionAuthenticationMethod.enuAuthenticationMethods.password);
        jadeOptions.getSource().password.setValue("12345");
        jadeOptions.getSource().host.setValue("galadriel.sos");
        jadeOptions.getSource().directory.setValue("/home/test/tmp/test");
        jadeOptions.getSource().protocol.setValue(TransferTypes.sftp);
        jadeOptions.fileSpec.setValue(".*");
        jadeOptions.getTarget().directory.setValue("C:/sp/testing/yade/in/477");
        jadeOptions.getTarget().protocol.setValue(TransferTypes.local);

        SOSDataExchangeEngine engine = new SOSDataExchangeEngine(jadeOptions);
        jadeOptions.setJobSchedulerId("sp_4012");
        jadeOptions.setJobChain("yade_job_chain");
        jadeOptions.setJob("YADEJob");
        jadeOptions.setJobChainNodeName("execute yade job");
        jadeOptions.setOrderId("dummyOrderIdForTesting");
        jadeOptions.setTaskId("32885");
        // engine.setDBFactory(dbFactory);
        try {
            engine.execute();
        } catch (Exception e) {
            throw e;
        } finally {
            engine.disconnect();
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
