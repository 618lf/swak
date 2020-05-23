package com.swak.jwt;

import com.swak.security.JWTAuthOptions;
import com.swak.security.JwtAuthProvider;
import com.swak.security.jwt.JWTOptions;
import com.swak.security.jwt.JWTPayload;
import com.swak.security.options.KeyStoreOptions;

/**
 * 通过 Keystore 来做授权： 不支持创建token和验证token分开的模式
 * 
 * keytool -genseckey -keystore D:\\keystore.jceks -storetype jceks -storepass
 * secret -keyalg HMacSHA256 -keysize 2048 -alias HS256 -keypass secret
 * 
 * keytool -genseckey -keystore D:\\keystore.jceks -storetype jceks -storepass
 * secret -keyalg HMacSHA384 -keysize 2048 -alias HS384 -keypass secret
 * 
 * keytool -genseckey -keystore D:\\keystore.jceks -storetype jceks -storepass
 * secret -keyalg HMacSHA512 -keysize 2048 -alias HS512 -keypass secret
 * 
 * keytool -genkey -keystore D:\\keystore.jceks -storetype jceks -storepass
 * secret -keyalg RSA -keysize 2048 -alias RS256 -keypass secret -sigalg
 * SHA256withRSA -dname "CN=,OU=,O=,L=,ST=,C=" -validity 360
 * 
 * keytool -genkey -keystore D:\\keystore.jceks -storetype jceks -storepass
 * secret -keyalg RSA -keysize 2048 -alias RS384 -keypass secret -sigalg
 * SHA384withRSA -dname "CN=,OU=,O=,L=,ST=,C=" -validity 360
 * 
 * keytool -genkey -keystore D:\\keystore.jceks -storetype jceks -storepass
 * secret -keyalg RSA -keysize 2048 -alias RS512 -keypass secret -sigalg
 * SHA512withRSA -dname "CN=,OU=,O=,L=,ST=,C=" -validity 360
 * 
 * keytool -genkeypair -keystore D:\\keystore.jceks -storetype jceks -storepass
 * secret -keyalg EC -keysize 256 -alias ES256 -keypass secret -sigalg
 * SHA256withECDSA -dname "CN=,OU=,O=,L=,ST=,C=" -validity 360
 * 
 * keytool -genkeypair -keystore D:\\keystore.jceks -storetype jceks -storepass
 * secret -keyalg EC -keysize 384 -alias ES384 -keypass secret -sigalg
 * SHA384withECDSA -dname "CN=,OU=,O=,L=,ST=,C=" -validity 360
 * 
 * keytool -genkeypair -keystore D:\\keystore.jceks -storetype jceks -storepass
 * secret -keyalg EC -keysize 521 -alias ES512 -keypass secret -sigalg
 * SHA512withECDSA -dname "CN=,OU=,O=,L=,ST=,C=" -validity 360
 * 
 * @author lifeng
 * @date 2020年4月16日 下午9:37:17
 */
public class KeyStoreTest {

	/**
	 * 创建： Options
	 * 
	 * 第一步，使用如上的命令创建keystore文件
	 * 
	 * 第二步，将keystore文件放入classpath位置（src/main/resources）。或者文件系统
	 * 
	 * 第三步，配置JWTAuth：算法、keystore位置、密码。
	 * 
	 * 第四步，注意点keystore如果放在src/main/resources，则直接keystore.setPath("keystore.jceks")
	 * 
	 * @return
	 */
	private static JWTAuthOptions KeyStoreOptions() {

		// 选择一种模式
		JWTOptions useOptions = new JWTOptions();
		useOptions.setAlgorithm("RS256"); // 需要和keystore中的算法一致

		// 支持的所有模式
		JWTAuthOptions options = new JWTAuthOptions();
		KeyStoreOptions keystore = new KeyStoreOptions();
		keystore.setPath(""); // 设置路径
		keystore.setPassword("secret");// 设置密码
		options.setKeyStore(keystore);

		options.setJWTOptions(useOptions);
		return options;
	}

	public static void main(String[] args) {
		JWTAuthOptions config = KeyStoreOptions();
		JwtAuthProvider jwt = new JwtAuthProvider(config);
		JWTPayload payload = new JWTPayload();
		payload.put("id", "1");
		payload.put("name", "李锋");
		String token = jwt.generateToken(payload);
		System.out.println("生成的 token:" + token);
	}
}
