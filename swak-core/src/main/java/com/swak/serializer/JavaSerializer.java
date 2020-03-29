package com.swak.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.swak.exception.SerializeException;

/**
 * 标准的 Java 序列化
 * @author winterlau
 */
public class JavaSerializer implements Serializer {

    @Override
    public String name() {
        return "java";
    }

    @Override
    public byte[] serialize(Object obj) throws SerializeException {
        ObjectOutputStream oos = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new SerializeException(e);
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    @Override
    public Object deserialize(byte[] bits) throws SerializeException {
        if (bits == null || bits.length == 0) {
            return null;
        }
        ObjectInputStream ois = null;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(bits);
            ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {
            throw new SerializeException(e);
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

}
