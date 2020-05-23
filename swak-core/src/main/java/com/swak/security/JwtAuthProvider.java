package com.swak.security;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.util.ResourceUtils;

import com.swak.exception.BaseRuntimeException;
import com.swak.security.jwt.JWK;
import com.swak.security.jwt.JWT;
import com.swak.security.jwt.JWTOptions;
import com.swak.security.jwt.JWTPayload;
import com.swak.security.options.KeyStoreOptions;
import com.swak.security.options.PubSecKeyOptions;
import com.swak.security.options.SecretOptions;
import com.swak.utils.IOUtils;
import com.swak.utils.StringUtils;

/**
 * jwt 的授权实现
 *
 * @author lifeng
 */
public class JwtAuthProvider {

	private final JWT jwt;
	private final JWTOptions options;

	/**
	 * 通过配置化的方式来存储密码
	 * 
	 * @param config
	 */
	public JwtAuthProvider(JWTAuthOptions config) {
		this.options = config.getJWTOptions();

		final KeyStoreOptions keyStore = config.getKeyStore();

		try {
			if (keyStore != null) {
				KeyStore ks = this.loadKeyStore(keyStore.getType(), keyStore.getPath(), keyStore.getPassword());
				this.jwt = new JWT(ks, keyStore.getPassword().toCharArray());
			} else {
				this.jwt = new JWT();

				// the better way: use JWK
				final List<PubSecKeyOptions> keys = config.getPubSecKeys();

				if (keys != null) {
					for (PubSecKeyOptions pubSecKey : config.getPubSecKeys()) {
						if (pubSecKey.isSymmetric()) {
							jwt.addJWK(new JWK(pubSecKey.getAlgorithm(), pubSecKey.getPublicKey()));
						} else {
							jwt.addJWK(new JWK(pubSecKey.getAlgorithm(), pubSecKey.isCertificate(),
									pubSecKey.getPublicKey(), pubSecKey.getSecretKey()));
						}
					}
				}

				// the better way: use JWK
				final List<SecretOptions> secrets = config.getSecrets();

				if (secrets != null) {
					for (SecretOptions secret : secrets) {
						this.jwt.addSecret(secret.getType(), secret.getSecret());
					}
				}

				final List<JWK> jwks = config.getJwks();

				if (jwks != null) {
					for (JWK jwk : jwks) {
						this.jwt.addJWK(jwk);
					}
				}
			}
		} catch (Exception e) {
			throw new BaseRuntimeException(e);
		}
	}

	/**
	 * 默认的通过： keyStore 的方式来存储密码
	 * 
	 * @param keyStorePath
	 * @param keyStorePass
	 */
	public JwtAuthProvider(String keyStorePath, String keyStorePass, String keyStoreAlgorithm) {
		try {
			KeyStore keyStore = this.loadKeyStore("jceks", keyStorePath, keyStorePass);

			jwt = new JWT(keyStore, keyStorePass.toCharArray());
		} catch (Exception e) {
			throw new BaseRuntimeException(e);
		}

		// 默认的配置
		options = new JWTOptions().setAlgorithm(keyStoreAlgorithm);
	}

	private synchronized KeyStore loadKeyStore(String type, String path, String pass)
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		InputStream ksPath;
		try {
			// 加载默认位置的 keystore
			if (StringUtils.isBlank(path)) {

				// classes 目录下
				ksPath = JwtAuthProvider.class.getClassLoader().getResourceAsStream("keystore.jceks");

				// 当前目录下
				if (ksPath == null) {
					ksPath = JwtAuthProvider.class.getResourceAsStream("keystore.jceks");
				}
			}
			// 自定义的的keystore
			else {
				ksPath = this.getResource(path).getInputStream();
			}
		} catch (Exception e) {
			throw new BaseRuntimeException(e);
		}

		// 支持值这个类型
		KeyStore keyStore = KeyStore.getInstance(type);

		// 加载 keyStore
		keyStore.load(ksPath, pass.toCharArray());

		// 关闭资源
		IOUtils.closeQuietly(ksPath);

		// 返回加载好的 keyStore
		return keyStore;
	}

	/**
	 * 获取资源
	 *
	 * @param path 路径
	 * @return 资源
	 * @author lifeng
	 * @date 2020/3/29 13:27
	 */
	private Resource getResource(String path) {
		if (path.startsWith(ResourceLoader.CLASSPATH_URL_PREFIX)) {
			return new ClassPathResource(path.substring(ResourceLoader.CLASSPATH_URL_PREFIX.length()));
		} else {
			try {
				URL url = new URL(path);
				return (ResourceUtils.isFileURL(url) ? new FileUrlResource(url) : new UrlResource(url));
			} catch (Exception e) {
				return new ClassPathResource(path);
			}
		}
	}

	/**
	 * token 失效的时间设置
	 *
	 * @param expiresInSeconds 表达式
	 * @return JwtAuthProvider
	 */
	public JwtAuthProvider setExpiresInSeconds(int expiresInSeconds) {
		options.setExpiresInSeconds(expiresInSeconds);
		options.setIgnoreExpiration(false);
		return this;
	}

	/**
	 * 签名，生成 token
	 *
	 * @param payload 数据
	 * @return 生成 token
	 */
	public String generateToken(JWTPayload payload) {
		return jwt.sign(payload, options);
	}

	/**
	 * 验证 token
	 *
	 * @param token 生成 token
	 * @return JWTPayload
	 */
	public JWTPayload verifyToken(String token) {
		if (StringUtils.isBlank(token)) {
			return null;
		}

		// 解密token
		JWTPayload payload = jwt.decode(token);

		// 验证失败会抛出异常
		if (!options.isIgnoreExpiration()) {
			jwt.isExpired(payload, options);
		}
		return payload;
	}

}