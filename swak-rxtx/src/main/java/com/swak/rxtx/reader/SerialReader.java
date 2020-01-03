package com.swak.rxtx.reader;

/**
 * @author han xinjian
 **/
@FunctionalInterface
public interface SerialReader {


    /**
     * read a byte array
     *
     * @return data
     */
    byte[] readBytes();
}
