package com.swak.rxtx.parse;

import com.swak.rxtx.HexUtil;

/**
 * @author han xinjian
 **/
public class HexStringSerialDataParser implements SerialDataParser<String> {
    @Override
    public String parse(byte[] bytes) {
        return HexUtil.bytesToHexString(bytes);
    }
}
