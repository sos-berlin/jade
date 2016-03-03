package com.sos.jade.db;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/** @author Uwe Risse */
public class JadeTransferDetailDBLayerTest {

    private static final Logger LOGGER = Logger.getLogger(JadeTransferDetailDBLayerTest.class);
    private JadeTransferDetailDBLayer jadeTransferDetailDBLayer;
    private String configurationFilename = "c:/temp/hibernate.cfg.xml";

    @Before
    public void setUp() throws Exception {
        jadeTransferDetailDBLayer = new JadeTransferDetailDBLayer(configurationFilename);
    }

    private JadeTransferDetailDBItem getNewTransferDetailDBItem() {
        JadeTransferDetailDBItem transferDetailItem = new JadeTransferDetailDBItem();
        transferDetailItem.setPid("2");
        transferDetailItem.setTargetFilename("myTargetFilename");
        transferDetailItem.setSourceFilename("mySourceFilename");
        transferDetailItem.setStartTime(new Date());
        transferDetailItem.setEndTime(new Date());
        transferDetailItem.setStatus(1);
        transferDetailItem.setFileSize(new Long(1));
        transferDetailItem.setCommandType(1);
        transferDetailItem.setCommand("myCommand");
        transferDetailItem.setMd5("myMd5");
        transferDetailItem.setLastErrorMessage("myLastErrorMessage");
        transferDetailItem.setModifiedBy("myModifiedBy");
        transferDetailItem.setCreatedBy("myCreatedBy");
        transferDetailItem.setCreated(new Date());
        transferDetailItem.setModified(new Date());
        return transferDetailItem;
    }

    private JadeTransferDBItem getNewTransferDBItem() {
        JadeTransferDBItem transferItem = new JadeTransferDBItem();
        transferItem.setMandator("myMandator");
        transferItem.setSourceHost("mySourceHost");
        transferItem.setSourceHostIp("mySourceHostIp");
        transferItem.setSourceUser("mySourceUser");
        transferItem.setSourceDir("mySourceDir");
        transferItem.setFileSize(new Long(1));
        transferItem.setProtocolType(1);
        transferItem.setPort(4711);
        transferItem.setTargetHost("myTargetHost");
        transferItem.setTargetHostIp("myTargetHostIp");
        transferItem.setTargetUser("myTargetUser");
        transferItem.setTargetDir("myTargetDir");
        transferItem.setTargetDir("myTargetDir");
        transferItem.setStartTime(new Date());
        transferItem.setEndTime(new Date());
        transferItem.setFilesCount(2);
        transferItem.setStatus(1);
        transferItem.setProfileName("myProfileName");
        transferItem.setProfile("myProfile");
        transferItem.setLog("myLog");
        transferItem.setLastErrorMessage("myLastErrorMessage");
        transferItem.setCommandType(3);
        transferItem.setCommand("myCommand");
        transferItem.setModifiedBy("myModifiedBy");
        transferItem.setModified(new Date());
        transferItem.setCreatedBy("myCreatedBy");
        transferItem.setCreated(new Date());
        return transferItem;
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testDeleteFromTo() throws ParseException {
        try {
            jadeTransferDetailDBLayer.getConnection().connect();
            jadeTransferDetailDBLayer.getConnection().beginTransaction();
            jadeTransferDetailDBLayer.setDateFormat("yyyy-MM-dd hh:mm");
            jadeTransferDetailDBLayer.setCreatedFrom("2011-01-01 00:00");
            jadeTransferDetailDBLayer.setCreatedTo("2011-10-01 00:00");
            jadeTransferDetailDBLayer.deleteFromTo();
            jadeTransferDetailDBLayer.getConnection().connect();
            jadeTransferDetailDBLayer.getConnection().beginTransaction();
            List transferList = jadeTransferDetailDBLayer.getTransferList(0);
            assertEquals(0, transferList.size());
            JadeTransferDBLayer d = new JadeTransferDBLayer(configurationFilename);
            d.getConnection().connect();
            d.getConnection().beginTransaction();
            JadeTransferDetailDBItem jadeTransferDetailDBItem = this.getNewTransferDetailDBItem();
            jadeTransferDetailDBItem.setStatus(47);
            JadeTransferDBItem jadeTransferDBItem = this.getNewTransferDBItem();
            d.getConnection().save(jadeTransferDBItem);
            jadeTransferDetailDBItem.setJadeTransferDBItem(jadeTransferDBItem);
            d.getConnection().save(jadeTransferDetailDBItem);
            d.getConnection().delete(jadeTransferDetailDBItem);
            d.getConnection().save(jadeTransferDetailDBItem);
            d.getConnection().delete(jadeTransferDetailDBItem);
            d.getConnection().commit();
            d.getConnection().connect();
            d.getConnection().beginTransaction();
            Query query = d.getConnection().createQuery("  from JadeTransferDetailDBItem where status = :status");
            query.setParameter("status", 47);
            transferList = query.list();
            assertEquals(0, transferList.size());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testFilesSelectFromTo() throws Exception {
        JadeTransferDetailDBLayer jadeTransferDetailDBLayer = new JadeTransferDetailDBLayer(configurationFilename);
        jadeTransferDetailDBLayer.setDateFormat("dd.MM.yyyy hh:mm");
        jadeTransferDetailDBLayer.setCreatedFrom("07.09.2001 00:00");
        jadeTransferDetailDBLayer.setCreatedTo("07.09.2021 00:00");
        try {
            List<JadeTransferDetailDBItem> resultList = jadeTransferDetailDBLayer.getTransferListDetail(0);
            for (int i = 0; i < resultList.size(); i++) {
                JadeTransferDetailDBItem transfer = (JadeTransferDetailDBItem) resultList.get(i);
                if (transfer != null) {
                    if (i == 0) {
                        if (transfer.getCommand() != null) {
                            assertEquals("myCommand", transfer.getCommand());
                        }
                    }
                    LOGGER.info("History: " + transfer.getTransferId());
                }
            }
        } catch (ParseException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
