package com.swak.jwt;

import com.swak.security.JWTAuthOptions;
import com.swak.security.JwtAuthProvider;
import com.swak.security.jwt.JWTPayload;
import com.swak.security.options.KeyStoreOptions;

/**
 * 自定义的授权方式
 * 
 * @author lifeng
 * @date 2020年4月16日 下午1:39:54
 */
public class CustomAuthTest {

	/**
	 * 创建： Options
	 * 
	 * @return
	 */
	private static JWTAuthOptions KeyStoreOptions() {
		JWTAuthOptions options = new JWTAuthOptions();

		// keystore： 只是密码的一种存储方式
		KeyStoreOptions keystore = new KeyStoreOptions();
		keystore.setPassword("13599794-749a-4290-852a-96f0d44613af");
		options.setKeyStore(keystore);

		// 如果直接在项目中写死，则可以使用JWK这样的方式
		
		return options;
	}

	public static void main() {
		JWTAuthOptions config = KeyStoreOptions();
		JwtAuthProvider jwt = new JwtAuthProvider(config);
		JWTPayload payload = new JWTPayload();
		payload.put("id", "1");
		payload.put("name", "李锋");
		String token = jwt.generateToken(payload);
		System.out.println("生成的 token:" + token);
	}
}