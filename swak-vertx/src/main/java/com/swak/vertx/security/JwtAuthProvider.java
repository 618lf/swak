package com.swak.vertx.security;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import com.swak.exception.BaseRuntimeException;
import com.swak.utils.IOUtils;
import com.swak.utils.StringUtils;
import com.swak.vertx.config.VertxProperties;
import com.swak.vertx.security.jwt.JWT;
import com.swak.vertx.security.jwt.JWTOptions;
import com.swak.vertx.security.jwt.JWTPayload;

/**
 * jwt 的授权实现
 * 
 * @author lifeng
 */
public class JwtAuthProvider {

	private final JWT jwt;
	private final JWTOptions options;
	private final String tokenName;

	public JwtAuthProvider(VertxProperties properties) {
		try {
			KeyStore keyStore = this.loadKeyStore(properties.getKeyStorePath(), properties.getKeyStorePass());

			jwt = new JWT(keyStore, properties.getKeyStorePass().toCharArray());
		} catch (Exception e) {
			throw new BaseRuntimeException(e);
		}
		
		// 设置tokenName
		tokenName = properties.getJwtTokenName();
		
		// 默认的配置
		options = new JWTOptions();
	}

	private synchronized KeyStore loadKeyStore(String path, String pass)
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		InputStream ksPath = null;
		try {
			if (StringUtils.isBlank(path)) {
				ksPath = JwtAuthProvider.class.getResourceAsStream("keystore.jceks");
			} else {
				ksPath = new FileInputStream(path);
			}
		} catch (Exception e) {
			ksPath = JwtAuthProvider.class.getClassLoader().getResourceAsStream("keystore.jceks");
		}

		// 支持值这个类型
		KeyStore keyStore = KeyStore.getInstance("jceks");

		// 加载 keyStore
		keyStore.load(ksPath, pass.toCharArray());

		// 关闭资源
		IOUtils.closeQuietly(ksPath);

		// 返回加载好的 keyStore
		return keyStore;
	}

	/**
	 * 签名，生成 token
	 * 
	 * @param payload
	 * @return
	 */
	public String generateToken(JWTPayload payload) {
		return jwt.sign(payload, options);
	}

	/**
	 * 验证 token
	 * 
	 * @param token
	 * @return
	 */
	public JWTPayload verifyToken(String token) {
		if (StringUtils.isBlank(token)) {
			return null;
		}
		return jwt.decode(token);
	}

	/**
	 * 是否过期
	 * 
	 * @param token
	 * @return
	 */
	public Boolean isExpired(JWTPayload payload) {
		return jwt.isExpired(payload, options);
	}

	public JWTOptions getOptions() {
		return options;
	}
	public String getTokenName() {
		return tokenName;
	}
}