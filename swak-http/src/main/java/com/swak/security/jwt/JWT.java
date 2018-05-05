package com.swak.security.jwt;

import com.swak.security.jwt.algorithms.Algorithm;
import com.swak.security.jwt.exceptions.JWTDecodeException;
import com.swak.security.jwt.interfaces.DecodedJWT;
import com.swak.security.jwt.interfaces.Verification;

public abstract class JWT {

    /**
     * Decode a given Json Web Token.
     * <p>
     * Note that this method <b>doesn't verify the token's signature!</b> Use it only if you trust the token or you already verified it.
     *
     * @param token with jwt format as string.
     * @return a decoded JWT.
     * @throws JWTDecodeException if any part of the token contained an invalid jwt or JSON format of each of the jwt parts.
     */
    public static DecodedJWT decode(String token) throws JWTDecodeException {
        return new JWTDecoder(token);
    }

    /**
     * Returns a {@link JWTVerifier} builder with the algorithm to be used to validate token signature.
     *
     * @param algorithm that will be used to verify the token's signature.
     * @return {@link JWTVerifier} builder
     * @throws IllegalArgumentException if the provided algorithm is null.
     */
    public static Verification require(Algorithm algorithm) {
        return JWTVerifier.init(algorithm);
    }

    /**
     * Returns a Json Web Token builder used to create and sign tokens
     *
     * @return a token builder.
     */
    public static JWTCreator.Builder create() {
        return JWTCreator.init();
    }
}
