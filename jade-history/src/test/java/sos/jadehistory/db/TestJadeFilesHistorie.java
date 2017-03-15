package sos.jadehistory.db;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.sos.hibernate.classes.DbItem;

// oh 18.04.14 No runnable methods [SP]
@Ignore("Test set to Ignore for later examination")
// 11.08.15 runnable test methods added [SP]
public class TestJadeFilesHistorie {

    private String configurationFilename = "c:/temp/hibernate.cfg.xml";
    private static final Logger LOGGER = Logger.getLogger(TestJadeFilesHistorie.class);

    public TestJadeFilesHistorie() {
        // nothing to do
    }

    @Test
    public void testPartialSearchword() {
        JadeFilesDBLayer layer = new JadeFilesDBLayer(configurationFilename);
        try {
            layer.getSession().connect();
            layer.getSession().beginTransaction();
            Query query = layer.getSession().createQuery(" from JadeFilesDBItem where sourceFilename like '%Mass%'");
            List<DbItem> resultset = query.list();
            assertNotNull(resultset);
            for (DbItem item : resultset) {
                LOGGER.info("**** sourceFilename: " + ((JadeFilesDBItem) item).getSourceFilename() + "****");
            }
        } catch (Exception e) {
            LOGGER.error("Error occurred in testPartialSearchword(): ", e);
        }
    }

    @Test
    public void testCompleteSearchword() {
        JadeFilesDBLayer layer = new JadeFilesDBLayer(configurationFilename);
        try {
            layer.getSession().connect();
            layer.getSession().beginTransaction();
            Query query = layer.getSession().createQuery(" from JadeFilesDBItem where sourceFilename = 'Masstest00001.txt'");
            List<DbItem> resultset = query.list();
            assertNotNull(resultset);
            for (DbItem item : resultset) {
                LOGGER.info("**** sourceFilename: " + ((JadeFilesDBItem) item).getSourceFilename() + "****");
            }
        } catch (Exception e) {
            LOGGER.error("Error occurred in testCompleteSearchword(): ", e);
        }
    }

}
