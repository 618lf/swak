package com.swak.jwt;

import com.swak.security.JwtAuthProvider;
import com.swak.security.jwt.JWTPayload;

/**
 * token 过期测试
 * 
 * @author lifeng
 */
public class TokenExpireTest {

	public static void main(String[] args) throws InterruptedException {
		JwtAuthProvider jwt = new JwtAuthProvider(null, "secret", "X-Token");
		jwt.setExpiresInSeconds(30);
		JWTPayload payload = new JWTPayload();
		payload.put("id", "1");
		payload.put("name", "李锋");
		String token = jwt.generateToken(payload);
		Thread.sleep(30001);
		payload = jwt.verifyToken(token);
	}
}
