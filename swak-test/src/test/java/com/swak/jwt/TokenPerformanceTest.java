package com.swak.jwt;

import com.swak.security.JwtAuthProvider;
import com.swak.security.jwt.JWTPayload;
import com.swak.test.utils.MultiThreadTest;

/**
 * 性能测试： 300000/s的性能
 * 
 * @author lifeng
 */
public class TokenPerformanceTest {

	public static void main(String[] args) {
		JwtAuthProvider jwt = new JwtAuthProvider(null, "secret");
		JWTPayload payload = new JWTPayload();
		payload.put("id", "1");
		payload.put("name", "李锋");
		String token = jwt.generateToken(payload);
		payload = jwt.verifyToken(token);

		MultiThreadTest.run(() -> {
			for (int i = 0; i < 10000; i++) {
				JWTPayload payload2 = new JWTPayload();
				payload2.put("id", "1");
				payload2.put("name", "李锋");
				String token2 = jwt.generateToken(payload2);
				payload2 = jwt.verifyToken(token2);
			}
		}, 100, "jwt性能测试");
	}
}
