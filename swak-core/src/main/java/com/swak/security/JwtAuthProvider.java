package com.swak.security;

import com.swak.exception.BaseRuntimeException;
import com.swak.security.jwt.JWT;
import com.swak.security.jwt.JWTOptions;
import com.swak.security.jwt.JWTPayload;
import com.swak.utils.IOUtils;
import com.swak.utils.StringUtils;
import org.springframework.core.io.*;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

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
        InputStream ksPath;
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

    public String getTokenName() {
        return tokenName;
    }
}