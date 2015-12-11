package sos.jadehistory.db;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.sos.hibernate.classes.DbItem;

// oh 18.04.14 No runnable methods [SP]
@Ignore("Test set to Ignore for later examination")
public class TestJadeFilesHistorie {

    private String configurationFilename = "c:/temp/hibernate.cfg.xml";
    private static final Logger LOGGER = Logger.getLogger(TestJadeFilesHistorie.class);
    private File configurationFile;

    public TestJadeFilesHistorie() {
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
        configurationFile = new File(configurationFilename);
    }

    @After
    public void tearDown() throws Exception {
        // nothing to do
    }

    @Test
    public void testPartialSearchword() {
        JadeFilesDBLayer layer = new JadeFilesDBLayer(configurationFile);
        Session session = layer.getSession();
        Transaction transaction = session.beginTransaction();
        Query query = layer.createQuery(" from JadeFilesDBItem where sourceFilename like '%Mass%'");
        List<DbItem> resultset = query.list();
        assertNotNull(resultset);
        for (DbItem item : resultset) {
            LOGGER.info("**** sourceFilename: " + ((JadeFilesDBItem) item).getSourceFilename() + "****");
        }
        transaction.commit();
    }

    @Test
    public void testCompleteSearchword() {
        JadeFilesDBLayer layer = new JadeFilesDBLayer(configurationFile);
        Session session = layer.getSession();
        Transaction transaction = session.beginTransaction();
        Query query = layer.createQuery(" from JadeFilesDBItem where sourceFilename = 'Masstest00001.txt'");
        List<DbItem> resultset = query.list();
        assertNotNull(resultset);
        for (DbItem item : resultset) {
            LOGGER.info("**** sourceFilename: " + ((JadeFilesDBItem) item).getSourceFilename() + "****");
        }
        transaction.commit();
    }

}
