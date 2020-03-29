package com.swak.security.jwt;

import com.swak.utils.Maps;

import java.util.Map;

/**
 * JWT 的数据部分
 *
 * @author lifeng
 */
public class JWTPayload extends JWTObject {

    public JWTPayload() {
        this.map = Maps.newHashMap();
    }

    public JWTPayload(Map<String, Object> data) {
        this.map = data;
    }

    public JWTPayload(String json) {
        this.map = JWTEncode.encode(json);
    }
}
