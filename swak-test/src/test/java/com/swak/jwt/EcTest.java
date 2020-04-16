package com.swak.jwt;

import com.swak.security.JWTAuthOptions;
import com.swak.security.JwtAuthProvider;
import com.swak.security.jwt.JWTOptions;
import com.swak.security.jwt.JWTPayload;
import com.swak.security.options.PubSecKeyOptions;

/**
 * 也适合创建token和验证token不在同一个地方
 * 
 * openssl ecparam -name secp256r1 -genkey -out private.pem
 * 
 * openssl pkcs8 -topk8 -nocrypt -in private.pem -out private_key.pem
 * 
 * openssl ec -in private.pem -pubout -out public.pem
 * 
 * @author lifeng
 * @date 2020年4月16日 下午1:39:54
 */
public class EcTest {

	/**
	 * 创建： Options
	 * 
	 * @return
	 */
	private static JWTAuthOptions KeyStoreOptions() {

		// 默认的算法
		JWTOptions useOptions = new JWTOptions();
		useOptions.setAlgorithm("ES256");
		JWTAuthOptions options = new JWTAuthOptions();
		PubSecKeyOptions pubOptions = new PubSecKeyOptions();
		pubOptions = new PubSecKeyOptions();
		pubOptions.setAlgorithm("ES256");
		pubOptions.setSecretKey("MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQgcSaiSL8JPU0ObqTf\r\n"
				+ "0Z7PJQjWoJP5ZR58RsodhZtftkChRANCAARavVU0gIQGdwuFU/wNmY9AsbLg7NNU\r\n"
				+ "HQR6Na6iIW90ebq+YwMcXGWa7/aL6wmtt1zPSLczWB6/dj06Pn5o6SMk");//
		pubOptions.setPublicKey("MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEWr1VNICEBncLhVP8DZmPQLGy4OzT\r\n"
				+ "VB0EejWuoiFvdHm6vmMDHFxlmu/2i+sJrbdcz0i3M1gev3Y9Oj5+aOkjJA==");
		useOptions.setAlgorithm("ES256");
		options.addPubSecKey(pubOptions);

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