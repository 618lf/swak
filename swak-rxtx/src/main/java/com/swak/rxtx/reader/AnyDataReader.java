package com.swak.rxtx.reader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import com.swak.rxtx.SerialContext;

/**
 * @author han xinjian
 **/
public class AnyDataReader implements SerialReader {
    @Override
    public byte[] readBytes() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        try {
            int read = SerialContext.getSerialPort().getInputStream().read();
            while (read != -1) {
                byteBuffer.put(((byte) read));
                read = SerialContext.getSerialPort().getInputStream().read();
            }
            return Arrays.copyOf(byteBuffer.array(), byteBuffer.position());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
