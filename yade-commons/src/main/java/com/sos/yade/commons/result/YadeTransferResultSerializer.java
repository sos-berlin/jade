package com.sos.yade.commons.result;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class YadeTransferResultSerializer<T extends Serializable> {

    public String serialize(final T t) throws Exception {
        if (t == null) {
            return null;
        }
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); GZIPOutputStream gos = new GZIPOutputStream(baos); ObjectOutputStream oos =
                new ObjectOutputStream(gos)) {
            oos.writeObject(t);
            oos.flush();
            gos.finish();
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        }
    }

    @SuppressWarnings("unchecked")
    public T deserialize(String t) throws Exception {
        if (t == null) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new GZIPInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(t))))) {
            return (T) ois.readObject();
        }
    }

    public String serializeBase64(final T t) throws Exception {
        if (t == null) {
            return null;
        }
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(t);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        }
    }

    @SuppressWarnings("unchecked")
    public T deserializeBase64(final String t) throws Exception {
        if (t == null) {
            return null;
        }
        try (final ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(t)))) {
            return (T) ois.readObject();
        }
    }

}
