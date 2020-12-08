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
		useOptions.setExpiresInMinutes(7*24);
		
		// 支持的所有模式
		JWTAuthOptions options = new JWTAuthOptions();
		KeyStoreOptions keystore = new KeyStoreOptions();
		keystore.setPath(""); // 设置路径
		keystore.setPassword("secret");// 设置密码
		options.setKeyStore(keystore);

		options.setJWTOptions(useOptions);
		return options;
	}

	//eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJuYW1lIjoi5p2O6ZSLIiwiaWQiOiIxIn0.R_3ZTMTL_gt942HYn0YZqasxggc2hrFe30Qb9wIr0UKcf8YtnBfcSXagbeahoy__sBzyDwAdNuQwy5Z6z_QOkpdqzk8w5k-dq6YD4pP4QAQnpZkUXRLPseuRY_PK28nRsTBozvsvk78vL2wQQVPZTgggrwu7WQsnkafIzQwSjgJMHiHufNauElytoOxNjX7JbrCUaVyWeN28D-O6jj-dWF72lpbx-AgSXQOoiQJzC6m-iJ3DuO-XiwEtokpzJ_DuyvAl23G9Ctzot_vbXRhZy2JnTdmT7UqN53oFUZiQYf8S3fs8-vByCfGzE_DP8dj6cXM_UTUuPZ3beawnwQfgFg
	//eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJuYW1lIjoi5p2O6ZSLIiwiaWQiOiIxIn0.R_3ZTMTL_gt942HYn0YZqasxggc2hrFe30Qb9wIr0UKcf8YtnBfcSXagbeahoy__sBzyDwAdNuQwy5Z6z_QOkpdqzk8w5k-dq6YD4pP4QAQnpZkUXRLPseuRY_PK28nRsTBozvsvk78vL2wQQVPZTgggrwu7WQsnkafIzQwSjgJMHiHufNauElytoOxNjX7JbrCUaVyWeN28D-O6jj-dWF72lpbx-AgSXQOoiQJzC6m-iJ3DuO-XiwEtokpzJ_DuyvAl23G9Ctzot_vbXRhZy2JnTdmT7UqN53oFUZiQYf8S3fs8-vByCfGzE_DP8dj6cXM_UTUuPZ3beawnwQfgFg
	//eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJuYW1lIjoi5p2O6ZSLIiwiaWQiOiIxIiwiZXhwIjoxNjA3MzE3OTYxfQ.Kgbl8fmEjsVV8eVA7orM8AtfZhScbTYRqSoFw3lnkPguNiUUCLjHDK-LJvE13HwIYfZGGM3ooFOp18qZrNiJFmOHCQ4BWVIxCm1Ab5qc-dq9Bf_oeC1QRtFw7LcvTcBD4rukbQAejq-gu0VMjDg23nI9SVfBIxAylteNifNBP8D6l7bXQObqsH1hLFwuWiahAGO1kbxdSuU_M4-A4z28HSzege0ov_aHi1c3KnwoyEizScZ6mDnVeRc7s2PZTl8AXwymUueBwm1_0Mo6rYjWpdzSP2iF4lRmnjfQ01zxr6DGIcjjmXZTpV5a39O5l8RMVBJQjKJO_0mYU20tgD4F8w
	//eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJuYW1lIjoi5p2O6ZSLIiwiaWQiOiIxIiwiZXhwIjoxNjA3MzE3OTgxfQ.jB6Ag7AnaJDY4suG9cWEXgIATMYyq6_l2Nz6RNXUJHr0KTXthVcYdOr87DnSCltBH9xto8QVqnFMyv9wRz-A4r2jZpk6yB37Mqq2dr-2uCyX49mkz9FcTdFmGbQ049WREmJv0BAwz912PhC-RoCrOfRuv3nf-7g6Hyyv4hJp1VfAo_ZX8TLYY_y_7ad9y7BNYISJkk_JvgRb_uhiMcH1bFDpn7Xaf9QOvYsWlOr52jQClhLeTtHkmAx8lVvXkLQy23pCy_9CiqXsliX6fpl0xSMi0Wn9SkPiylA474rhIyunPG8QDwcy4RS31CBQKvw7Aq4CVi3bgmygYB1fhsqJow
	public static void main(String[] args) {
		JWTAuthOptions config = KeyStoreOptions();
		JwtAuthProvider jwt = new JwtAuthProvider(config);
		JWTPayload payload = new JWTPayload();
		payload.put("id", "1");
		payload.put("name", "李锋");
		String token = jwt.generateToken(payload);
		System.out.println(token);
		
		JWTPayload me = jwt.verifyToken(token);
		System.out.println(me.getLong("exp"));
	}
}
