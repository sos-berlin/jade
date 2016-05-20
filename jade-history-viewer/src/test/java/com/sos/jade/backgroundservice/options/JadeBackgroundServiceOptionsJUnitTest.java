package com.sos.jade.backgroundservice.options;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.sos.JSHelper.Basics.JSToolBox;
import com.sos.JSHelper.Listener.JSListenerClass;

public class JadeBackgroundServiceOptionsJUnitTest extends JSToolBox {

    protected JadeBackgroundServiceOptions objOptions = null;
    private static final int BOL_LOG_DEBUG_INFORMATION = 9;
    private static final String CONFIG_FILENAME = "/WEB-INF/classes/hibernate.cfg.xml";

    @Before
    public void setUp() throws Exception {
        System.setProperty("JADE_BS_HIBERNATE_CONFIG", CONFIG_FILENAME);
        objOptions = new JadeBackgroundServiceOptions();
        JSListenerClass.bolLogDebugInformation = true;
        JSListenerClass.intMaxDebugLevel = BOL_LOG_DEBUG_INFORMATION;
    }

    @Test
    public void testHibernateConfigurationFileName2() {
        assertEquals("", objOptions.hibernateConfigurationFileName.getValue(), CONFIG_FILENAME);
    }

    @Test
    public void testHibernateConfigurationFileNameAlias() {
        assertEquals("", objOptions.hibernateConf.getValue(), CONFIG_FILENAME);
    }

    @Test
    public void testHibernateConfigurationFileName() {
        objOptions.hibernateConfigurationFileName.setValue("++hibernate.cfg.xml++");
        assertEquals("", objOptions.hibernateConfigurationFileName.getValue(), "++hibernate.cfg.xml++");
    }
}