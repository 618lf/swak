package com.swak.security;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.util.ResourceUtils;

import com.swak.exception.BaseRuntimeException;
import com.swak.security.jwt.JWT;
import com.swak.security.jwt.JWTOptions;
import com.swak.security.jwt.JWTPayload;
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
	private final String tokenName;

	public JwtAuthProvider(String keyStorePath, String keyStorePass, String tokenName) {
		try {
			KeyStore keyStore = this.loadKeyStore(keyStorePath, keyStorePass);

			jwt = new JWT(keyStore, keyStorePass.toCharArray());
		} catch (Exception e) {
			throw new BaseRuntimeException(e);
		}

		// 设置tokenName
		this.tokenName = tokenName;

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
				Resource resource = this.getResource(path);
				ksPath = resource.getInputStream();
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

	// 获取资源
	private Resource getResource(String path) throws Exception {
		if (path.startsWith(ResourceLoader.CLASSPATH_URL_PREFIX)) {
			return new ClassPathResource(path.substring(ResourceLoader.CLASSPATH_URL_PREFIX.length()));
		} else {
			URL url = new URL(path);
			return (ResourceUtils.isFileURL(url) ? new FileUrlResource(url) : new UrlResource(url));
		}
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