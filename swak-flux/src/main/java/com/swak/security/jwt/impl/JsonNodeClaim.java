package com.swak.security.jwt.impl;

import java.util.Date;
import java.util.Map;

import com.swak.security.jwt.interfaces.Claim;

/**
 * The JsonNodeClaim retrieves a claim value from a JsonNode object.
 */
class JsonNodeClaim implements Claim {

    private final Object data;

    private JsonNodeClaim(Object node) {
        this.data = node;
    }

    @Override
    public Boolean asBoolean() {
        return !(data instanceof Boolean) ? null : (Boolean)data;
    }

    @Override
    public Integer asInt() {
    	return !(data instanceof Integer) ? null : (Integer)data;
    }

    @Override
    public Long asLong() {
    	return !(data instanceof Long) ? null : (Long)data;
    }

    @Override
    public Double asDouble() {
    	return !(data instanceof Double) ? null : (Double)data;
    }

    @Override
    public String asString() {
    	return !(data instanceof String) ? null : (String)data;
    }

    @Override
    public Date asDate() {
    	return !(data instanceof Long) ? null : new Date((Long)data * 1000);
    }
    
    @Override
    public boolean isNull() {
        return false;
    }

    /**
     * Helper method to extract a Claim from the given JsonNode tree.
     *
     * @param claimName the Claim to search for.
     * @param tree      the JsonNode tree to search the Claim in.
     * @return a valid non-null Claim.
     */
    static Claim extractClaim(String claimName, Map<String, Object> tree) {
    	Object node = tree.get(claimName);
        return claimFromNode(node);
    }

    /**
     * Helper method to create a Claim representation from the given JsonNode.
     *
     * @param node the JsonNode to convert into a Claim.
     * @return a valid Claim instance. If the node is null or missing, a NullClaim will be returned.
     */
    static Claim claimFromNode(Object node) {
        if (node == null) {
            return new NullClaim();
        }
        return new JsonNodeClaim(node);
    }
}
