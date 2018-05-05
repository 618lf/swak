package com.swak.security.jwt.algorithms;

import org.apache.commons.codec.binary.Base64;

import com.swak.security.jwt.exceptions.SignatureGenerationException;
import com.swak.security.jwt.exceptions.SignatureVerificationException;
import com.swak.security.jwt.interfaces.DecodedJWT;

class NoneAlgorithm extends Algorithm {

    NoneAlgorithm() {
        super("none", "none");
    }

    @Override
    public void verify(DecodedJWT jwt) throws SignatureVerificationException {
        byte[] signatureBytes = Base64.decodeBase64(jwt.getSignature());
        if (signatureBytes.length > 0) {
            throw new SignatureVerificationException(this);
        }
    }

    @Override
    public byte[] sign(byte[] contentBytes) throws SignatureGenerationException {
        return new byte[0];
    }
}
