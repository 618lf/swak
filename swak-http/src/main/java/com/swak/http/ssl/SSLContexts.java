/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package com.swak.http.ssl;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

/**
 * Builder for {@link javax.net.ssl.SSLContext} instances.
 * <p>
 * Please note: the default Oracle JSSE implementation of
 * {@link SSLContext#init(KeyManager[], TrustManager[], SecureRandom)} accepts
 * multiple key and trust managers, however only only first matching type is
 * ever used. See for example: <a href=
 * "http://docs.oracle.com/javase/7/docs/api/javax/net/ssl/SSLContext.html#init%28javax.net.ssl.KeyManager[],%20javax.net.ssl.TrustManager[],%20java.security.SecureRandom%29">
 * SSLContext.html#init </a>
 *
 * @since 4.4
 */
public class SSLContexts {

	static final String TLS = "TLS";

	private String protocol;
	private final Set<KeyManager> keymanagers;
	private final Set<TrustManager> trustmanagers;
	private SecureRandom secureRandom;

	public static SSLContexts create() {
		return new SSLContexts();
	}

	public SSLContexts() {
		super();
		this.keymanagers = new LinkedHashSet<KeyManager>();
		this.trustmanagers = new LinkedHashSet<TrustManager>();
	}

	public SSLContexts useProtocol(final String protocol) {
		this.protocol = protocol;
		return this;
	}

	public SSLContexts setSecureRandom(final SecureRandom secureRandom) {
		this.secureRandom = secureRandom;
		return this;
	}
    
    public SSLContexts loadKeyMaterial(
            final KeyStore keystore,
            final char[] keyPassword)
            throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
        final KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(
                KeyManagerFactory.getDefaultAlgorithm());
        kmfactory.init(keystore, keyPassword);
        final KeyManager[] kms =  kmfactory.getKeyManagers();
        if (kms != null) {
            for (final KeyManager km : kms) {
                keymanagers.add(km);
            }
        }
        return this;
    }

	protected void initSSLContext(final SSLContext sslcontext, final Collection<KeyManager> keyManagers,
			final Collection<TrustManager> trustManagers, final SecureRandom secureRandom)
			throws KeyManagementException {
		sslcontext.init(!keyManagers.isEmpty() ? keyManagers.toArray(new KeyManager[keyManagers.size()]) : null,
				!trustManagers.isEmpty() ? trustManagers.toArray(new TrustManager[trustManagers.size()]) : null,
				secureRandom);
	}

	public SSLContext build() throws NoSuchAlgorithmException, KeyManagementException {
		final SSLContext sslcontext = SSLContext.getInstance(this.protocol != null ? this.protocol : TLS);
		initSSLContext(sslcontext, keymanagers, trustmanagers, secureRandom);
		return sslcontext;
	}

	/**
	 * 工厂对象
	 * 
	 * @return
	 */
	public static SSLContexts me() {
		return new SSLContexts();
	}
}
