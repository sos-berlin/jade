package com.sos.DataExchange.history;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sos.jade.db.history.YadeEngineTransferResult;
import com.sos.yade.commons.result.YadeTransferResult;
import com.sos.yade.commons.result.YadeTransferResultEntry;
import com.sos.yade.commons.result.YadeTransferResultProtocol;
import com.sos.yade.commons.result.YadeTransferResultSerializer;

public class YadeTransferResultSerializerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(YadeTransferResultSerializerTest.class);

    @Ignore
    @Test
    public void testSerialize() throws Exception {
        YadeTransferResult tr = getTransfer(new YadeTransferResult());
        YadeTransferResultSerializer<YadeTransferResult> serializer = new YadeTransferResultSerializer<YadeTransferResult>();

        LOGGER.info("serialize ...");
        String serialized = serializer.serialize(tr);

        LOGGER.info("deserialize ...");
        YadeTransferResult r = serializer.deserialize(serialized);

        LOGGER.info(r.getSource().getHost());
        LOGGER.info(r.getEntries().get(1).getTarget());
    }

    @Ignore
    @Test
    public void testEngineSerialize() throws Exception {
        YadeEngineTransferResult tr = (YadeEngineTransferResult) getTransfer(new YadeEngineTransferResult());
        tr.setMandator("SOS");
        YadeTransferResultSerializer<YadeEngineTransferResult> serializer = new YadeTransferResultSerializer<YadeEngineTransferResult>();

        LOGGER.info("serialize ...");
        String serialized = serializer.serialize(tr);

        LOGGER.info("deserialize ...");
        YadeEngineTransferResult r = serializer.deserialize(serialized);

        LOGGER.info(r.getMandator());
        LOGGER.info(r.getSource().getHost());
        LOGGER.info(r.getEntries().get(1).getTarget());
    }

    @Ignore
    @Test
    public void testSerializeBase64() throws Exception {
        YadeTransferResult tr = getTransfer(new YadeTransferResult());
        YadeTransferResultSerializer<YadeTransferResult> serializer = new YadeTransferResultSerializer<>();

        LOGGER.info("serialize ...");
        String serialized = serializer.serializeBase64(tr);

        LOGGER.info("deserialize ...");
        YadeTransferResult r = serializer.deserialize(serialized);

        LOGGER.info(r.getSource().getHost());
        LOGGER.info(r.getEntries().get(1).getTarget());
    }

    private YadeTransferResult getTransfer(YadeTransferResult tr) {
        tr.setSource(getProtocol("source"));
        tr.setTarget(getProtocol("target"));

        tr.setSettings("settings.xml");
        tr.setProfile("my_profile");
        tr.setOperation("operation");

        tr.setStart(Instant.now());
        tr.setEnd(Instant.now());

        tr.setEntries(generateEntries());
        return tr;
    }

    private YadeTransferResultProtocol getProtocol(String prefix) {
        YadeTransferResultProtocol p = new YadeTransferResultProtocol();
        p.setHost(prefix + "_host");
        p.setPort(123);
        p.setProtocol("sftp");
        p.setAccount(prefix + "_user");
        return p;
    }

    private List<YadeTransferResultEntry> generateEntries() {
        List<YadeTransferResultEntry> l = new ArrayList<YadeTransferResultEntry>();
        for (int i = 1; i <= 10; i++) {
            YadeTransferResultEntry e = new YadeTransferResultEntry();
            e.setSource("/tmp/source/ロバート /роберт_" + i + ".txt");
            e.setTarget("/tmp/target/ロバート /роберт_" + i + ".txt");
            e.setSize(i);
            e.setModificationDate(new Date().getTime());
            e.setState("transferred");
            l.add(e);
        }
        return l;
    }
}
