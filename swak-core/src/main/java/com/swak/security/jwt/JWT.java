/*
 * Copyright 2015 Red Hat, Inc.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  The Apache License v2.0 is available at
 *  http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 */
package com.swak.security.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JWT and JWS implementation draft-ietf-oauth-json-web-token-32.
 *
 * @author Paulo Lopes
 */
public final class JWT {

    private static final Random RND = new Random();

    @SuppressWarnings("serial")
    private static final Map<String, String> ALGORITHM_ALIAS = new HashMap<String, String>() {
        {
            put("HS256", "HMacSHA256");
            put("HS384", "HMacSHA384");
            put("HS512", "HMacSHA512");
            put("RS256", "SHA256withRSA");
            put("RS384", "SHA384withRSA");
            put("RS512", "SHA512withRSA");
            put("ES256", "SHA256withECDSA");
            put("ES384", "SHA384withECDSA");
            put("ES512", "SHA512withECDSA");
        }
    };

    private static final Charset UTF8 = StandardCharsets.UTF_8;
    private static final Logger log = LoggerFactory.getLogger(JWT.class);
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();

    private final Map<String, List<Crypto>> cryptoMap = new ConcurrentHashMap<>();

    public JWT() {
        // Spec requires "none" to always be available
        cryptoMap.put("none", Collections.singletonList(new CryptoNone()));
    }

    public JWT(final KeyStore keyStore, final char[] keyStorePassword) {
        this();

        // load MACs
        for (String alg : Arrays.asList("HS256", "HS384", "HS512")) {
            try {
                Mac mac = getMac(keyStore, keyStorePassword, alg);
                if (mac != null) {
                    List<Crypto> l = cryptoMap.computeIfAbsent(alg, k -> new ArrayList<>());
                    l.add(new CryptoMac(mac));
                } else {
                    log.info(alg + " not available");
                }
            } catch (RuntimeException e) {
                log.warn(alg + " not supported", e);
            }
        }

        for (String alg : Arrays.asList("RS256", "RS384", "RS512", "ES256", "ES384", "ES512")) {
            try {
                X509Certificate certificate = getCertificate(keyStore, alg);
                PrivateKey privateKey = getPrivateKey(keyStore, keyStorePassword, alg);
                if (certificate != null && privateKey != null) {
                    List<Crypto> l = cryptoMap.computeIfAbsent(alg, k -> new ArrayList<>());
                    l.add(new CryptoSignature(ALGORITHM_ALIAS.get(alg), certificate, privateKey));
                } else {
                    log.info(alg + " not available");
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
                log.warn(alg + " not supported");
            }
        }
    }

    @Deprecated
    public JWT(String key, boolean keyPrivate) {
        // make sure the none is present
        this();

        if (keyPrivate) {
            addSecretKey("RS256", key);
        } else {
            addPublicKey("RS256", key);
        }
    }

    private static byte[] base64urlDecode(String str) {
        return DECODER.decode(str.getBytes(UTF8));
    }

    private static String base64urlEncode(String str) {
        return base64urlEncode(str.getBytes(UTF8));
    }

    private static String base64urlEncode(byte[] bytes) {
        return ENCODER.encodeToString(bytes);
    }

    /**
     * Adds a JSON Web Key (rfc7517) to the crypto map.
     *
     * @param jwk a JSON Web Key
     * @return self
     */
    public JWT addJWK(JWK jwk) {
        List<Crypto> current = cryptoMap.computeIfAbsent(jwk.getAlgorithm(), k -> new ArrayList<>());

        boolean replaced = false;

        for (int i = 0; i < current.size(); i++) {
            if (current.get(i).getId().equals(jwk.getId())) {
                // replace
                current.set(i, jwk);
                replaced = true;
            }
        }

        if (!replaced) {
            // non existent, add it!
            current.add(jwk);
        }

        return this;
    }

    /**
     * Adds a public key for a given JWS algorithm to the crypto map. This is an
     * alternative to using keystores since it is common to see these keys when
     * dealing with 3rd party services such as Google or Keycloak.
     *
     * @param algorithm the JWS algorithm, e.g.: RS256
     * @param key       the base64 DER format of the key (also known as PEM format,
     *                  without the header and footer).
     * @return self
     * @deprecated Replaced by {@link #addJWK(JWK)}
     */
    @Deprecated
    public JWT addPublicKey(String algorithm, String key) {
        return addJWK(new JWK(algorithm, key, null));
    }

    /**
     * Adds a key pair for a given JWS algorithm to the crypto map. This is an
     * alternative to using keystores since it is common to see these keys when
     * dealing with 3rd party services such as Google or Keycloak.
     *
     * @param algorithm  the JWS algorithm, e.g.: RS256
     * @param publicKey  the base64 DER format of the key (also known as PEM format,
     *                   without the header and footer).
     * @param privateKey the base64 DER format of the key (also known as PEM format,
     *                   without the header and footer).
     * @return self
     * @deprecated Replaced by {@link #addJWK(JWK)}
     */
    @Deprecated
    public JWT addKeyPair(String algorithm, String publicKey, String privateKey) {
        return addJWK(new JWK(algorithm, publicKey, privateKey));
    }

    /**
     * Adds a private key for a given JWS algorithm to the crypto map. This is an
     * alternative to using keystores since it is common to see these keys when
     * dealing with 3rd party services such as Google.
     *
     * @param algorithm the JWS algorithm, e.g.: RS256
     * @param key       the base64 DER format of the key (also known as PEM format,
     *                  without the header and footer).
     * @return self
     * @deprecated Replaced by {@link #addJWK(JWK)}
     */
    @Deprecated
    public JWT addSecretKey(String algorithm, String key) {
        return addJWK(new JWK(algorithm, null, key));
    }

    /**
     * Adds a certificate for a given JWS algorithm to the crypto map. This is an
     * alternative to using keystores since it is common to see these keys when
     * dealing with 3rd party services such as Google.
     *
     * @param algorithm the JWS algorithm, e.g.: RS256
     * @param cert      the base64 DER format of the key (also known as PEM format,
     *                  without the header and footer).
     * @return self
     */
    public JWT addCertificate(String algorithm, String cert) {
        return addJWK(new JWK(algorithm, true, cert, null));
    }

    /**
     * Adds a secret (password) for a given JWS algorithm to the crypto map. This is
     * an alternative to using keystores since it is common to see these keys when
     * dealing with 3rd party services such as Google.
     *
     * @param algorithm the JWS algorithm, e.g.: HS256
     * @param key       the base64 DER format of the key (also known as PEM format,
     *                  without the header and footer).
     * @return self
     */
    public JWT addSecret(String algorithm, String key) {
        return addJWK(new JWK(algorithm, key));
    }

    /**
     * Creates a new Message Authentication Code
     *
     * @param keyStore a valid JKS
     * @param alias    algorithm to use e.g.: HmacSHA256
     * @return Mac implementation
     */
    private Mac getMac(final KeyStore keyStore, final char[] keyStorePassword, final String alias) {
        try {
            final Key secretKey = keyStore.getKey(alias, keyStorePassword);

            // key store does not have the requested algorithm
            if (secretKey == null) {
                return null;
            }

            Mac mac = Mac.getInstance(secretKey.getAlgorithm());
            mac.init(secretKey);

            return mac;
        } catch (NoSuchAlgorithmException | InvalidKeyException | UnrecoverableKeyException | KeyStoreException e) {
            throw new RuntimeException(e);
        }
    }

    private X509Certificate getCertificate(final KeyStore keyStore, final String alias) {
        try {
            return (X509Certificate) keyStore.getCertificate(alias);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }
    }

    private PrivateKey getPrivateKey(final KeyStore keyStore, final char[] keyStorePassword, final String alias) {
        try {
            return (PrivateKey) keyStore.getKey(alias, keyStorePassword);
        } catch (NoSuchAlgorithmException | UnrecoverableKeyException | KeyStoreException e) {
            throw new RuntimeException(e);
        }
    }

    public JWTPayload decode(final String token) {
        String[] segments = token.split("\\.");
        if (segments.length != (isUnsecure() ? 2 : 3)) {
            throw new RuntimeException("Not enough or too many segments");
        }

        // All segment should be base64
        String headerSeg = segments[0];
        String payloadSeg = segments[1];
        String signatureSeg = isUnsecure() ? null : segments[2];

        if ("".equals(signatureSeg)) {
            throw new RuntimeException("Signature is required");
        }

        // base64 decode and parse JSON
        JWTHeader header = new JWTHeader(new String(base64urlDecode(headerSeg), UTF8));
        JWTPayload payload = new JWTPayload(new String(base64urlDecode(payloadSeg), UTF8));

        String alg = header.get("alg");

        List<Crypto> cryptos = cryptoMap.get(alg);

        if (cryptos == null || cryptos.size() == 0) {
            throw new RuntimeException("Algorithm not supported");
        }

        // if we only allow secure alg, then none is not a valid option
        if (!isUnsecure() && "none".equals(alg)) {
            throw new RuntimeException("Algorithm \"none\" not allowed");
        }

        // verify signature. `sign` will return base64 string.
        if (!isUnsecure()) {
			assert signatureSeg != null;
			byte[] payloadInput = base64urlDecode(signatureSeg);
            byte[] signingInput = (headerSeg + "." + payloadSeg).getBytes(UTF8);

            for (Crypto c : cryptos) {
                if (c.verify(payloadInput, signingInput)) {
                    return payload;
                }
            }

            throw new RuntimeException("Signature verification failed");
        }

        return payload;
    }

    public boolean isExpired(JWTPayload jwt, JWTOptions options) {

        if (jwt == null) {
            return false;
        }

        // All dates in JWT are of type NumericDate
        // a NumericDate is: numeric value representing the number of seconds from
        // 1970-01-01T00:00:00Z UTC until
        // the specified UTC date/time, ignoring leap seconds
        final long now = (System.currentTimeMillis() / 1000);

        if (jwt.containsKey("exp") && !options.isIgnoreExpiration()) {
            if (now - options.getLeeway() >= jwt.getLong("exp")) {
                throw new RuntimeException("Expired JWT token: exp <= now");
            }
        }

        if (jwt.containsKey("iat")) {
            Long iat = jwt.getLong("iat");
            // issue at must be in the past
            if (iat > now + options.getLeeway()) {
                throw new RuntimeException("Invalid JWT token: iat > now");
            }
        }

        if (jwt.containsKey("nbf")) {
            Long nbf = jwt.getLong("nbf");
            // not before must be after now
            if (nbf > now + options.getLeeway()) {
                throw new RuntimeException("Invalid JWT token: nbf > now");
            }
        }

        return false;
    }

    public String sign(JWTPayload payload, JWTOptions options) {
        final String algorithm = options.getAlgorithm();

        List<Crypto> cryptos = cryptoMap.get(algorithm);

        if (cryptos == null || cryptos.size() == 0) {
            throw new RuntimeException("Algorithm not supported");
        }

        // header, typ is fixed value.
        JWTHeader header = new JWTHeader().merge(options.getHeader()).put("typ", "JWT").put("alg", algorithm);

        // NumericDate is a number is seconds since 1st Jan 1970 in UTC
        long timestamp = System.currentTimeMillis() / 1000;

        if (!options.isNoTimestamp()) {
            payload.put("iat", payload.get("iat", timestamp));
        }

        if (options.getExpiresInSeconds() > 0) {
            payload.put("exp", timestamp + options.getExpiresInSeconds());
        }

        if (options.getAudience() != null && options.getAudience().size() >= 1) {
            if (options.getAudience().size() > 1) {
                payload.put("aud", options.getAudience());
            } else {
                payload.put("aud", options.getAudience().get(0));
            }
        }

        if (options.getIssuer() != null) {
            payload.put("iss", options.getIssuer());
        }

        if (options.getSubject() != null) {
            payload.put("sub", options.getSubject());
        }

        // create segments, all segment should be base64 string
        String headerSegment = base64urlEncode(header.encode());
        String payloadSegment = base64urlEncode(payload.encode());
        String signingInput = headerSegment + "." + payloadSegment;
        String signSegment = base64urlEncode(
                cryptos.get(RND.nextInt(cryptos.size())).sign(signingInput.getBytes(UTF8)));

        return headerSegment + "." + payloadSegment + "." + signSegment;
    }

    public boolean isUnsecure() {
        return cryptoMap.size() == 1;
    }

    public Collection<String> availableAlgorithms() {
        return cryptoMap.keySet();
    }
}
