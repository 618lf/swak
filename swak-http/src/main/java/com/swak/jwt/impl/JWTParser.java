package com.swak.jwt.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.swak.common.utils.JsonMapper;
import com.swak.jwt.exceptions.JWTDecodeException;
import com.swak.jwt.interfaces.Header;
import com.swak.jwt.interfaces.JWTPartsParser;
import com.swak.jwt.interfaces.Payload;

@SuppressWarnings("unchecked")
public class JWTParser implements JWTPartsParser {
	
	@Override
    public Payload parsePayload(String json) throws JWTDecodeException {
    	if (json == null) {
            throw exceptionForInvalidJson(null);
        }
    	Map<String, Object> tree = JsonMapper.fromJson(json, Map.class);
    	String issuer = getString(tree, PublicClaims.ISSUER);
        String subject = getString(tree, PublicClaims.SUBJECT);
        List<String> audience = getStringOrArray(tree, PublicClaims.AUDIENCE);
        Date expiresAt = getDateFromSeconds(tree, PublicClaims.EXPIRES_AT);
        Date notBefore = getDateFromSeconds(tree, PublicClaims.NOT_BEFORE);
        Date issuedAt = getDateFromSeconds(tree, PublicClaims.ISSUED_AT);
        String jwtId = getString(tree, PublicClaims.JWT_ID);
        return new PayloadImpl(issuer, subject, audience, expiresAt, notBefore, issuedAt, jwtId, tree);
    }

    @Override
    public Header parseHeader(String json) throws JWTDecodeException {
    	if (json == null) {
            throw exceptionForInvalidJson(null);
        }
    	Map<String, Object> tree = JsonMapper.fromJson(json, Map.class);
    	String algorithm = getString(tree, PublicClaims.ALGORITHM);
        String type = getString(tree, PublicClaims.TYPE);
        String contentType = getString(tree, PublicClaims.CONTENT_TYPE);
        String keyId = getString(tree, PublicClaims.KEY_ID);
        return new BasicHeader(algorithm, type, contentType, keyId, tree);
    }
    
    private JWTDecodeException exceptionForInvalidJson(String json) {
        return new JWTDecodeException(String.format("The string '%s' doesn't have a valid JSON format.", json));
    }
    
    private String getString(Map<String, Object> tree, String claimName) {
        Object node = tree.get(claimName);
        if (node == null) {
            return null;
        }
        return String.valueOf(node);
    }
    
    private Date getDateFromSeconds(Map<String, Object> tree, String claimName) {
    	Object node = tree.get(claimName);
        if (node == null) {
            return null;
        }
        final long ms = (Long) node * 1000;
        return new Date(ms);
    }
    
    @SuppressWarnings("rawtypes")
	private List<String> getStringOrArray(Map<String, Object> tree, String claimName) throws JWTDecodeException {
    	Object node = tree.get(claimName);
        if (node == null) {
            return null;
        }
        
        return (List) node;
    }
}
