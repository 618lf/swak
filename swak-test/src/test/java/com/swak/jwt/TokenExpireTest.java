package com.swak.jwt;

import com.swak.security.JwtAuthProvider;
import com.swak.security.jwt.JWTPayload;

/**
 * token 过期测试： 执行这个测试用例需要删除resources下的keystore.jceks
 * 
 * @author lifeng
 */
public class TokenExpireTest {

	/**
	 * 测试是通过的
	 * 
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {

		// 默认路径下的keystore.jceks 的密码, 使用这个密码需要删除src/test/resources 下的keystore.jceks
		String pw = "secret";

		// src/test/resources 下的keystore.jceks
		pw = "13599794-749a-4290-852a-96f0d44613af";

		JwtAuthProvider jwt = new JwtAuthProvider(null, pw);
		jwt.setExpiresInSeconds(30);
		JWTPayload payload = new JWTPayload();
		payload.put("id", "1");
		payload.put("name", "李锋");
		String token = jwt.generateToken(payload);
		System.out.println("生成的 token:" + token);
		Thread.sleep(30001);
		payload = jwt.verifyToken(token);
	}
}
