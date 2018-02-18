package com.swak.jwt.impl;

import static com.swak.jwt.impl.JsonNodeClaim.extractClaim;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.swak.jwt.interfaces.Claim;
import com.swak.jwt.interfaces.Header;

/**
 * The BasicHeader class implements the Header interface.
 */
class BasicHeader implements Header {
    private final String algorithm;
    private final String type;
    private final String contentType;
    private final String keyId;
    private final Map<String, Object> tree;

    BasicHeader(String algorithm, String type, String contentType, String keyId, Map<String, Object> tree) {
        this.algorithm = algorithm;
        this.type = type;
        this.contentType = contentType;
        this.keyId = keyId;
        this.tree = Collections.unmodifiableMap(tree == null ? new HashMap<String, Object>() : tree);
    }

    Map<String, Object> getTree() {
        return tree;
    }

    @Override
    public String getAlgorithm() {
        return algorithm;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public String getKeyId() {
        return keyId;
    }

    @Override
    public Claim getHeaderClaim(String name) {
        return extractClaim(name, tree);
    }
}
