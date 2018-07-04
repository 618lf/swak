package com.swak.security;

import com.swak.reactivex.transport.http.Principal;
import com.swak.security.jwt.JWT;
import com.swak.security.jwt.JWTVerifier;
import com.swak.security.jwt.algorithms.Algorithm;
import com.swak.security.jwt.interfaces.DecodedJWT;
import com.swak.utils.JsonMapper;
import com.swak.utils.StringUtils;
import com.swak.utils.time.DateUtils;

/**
 * @author lifeng
 */
public class TokenUtils {

	/**
	 * 得到 token
	 * 
	 * @param sub
	 * @param key
	 * @return
	 */
	public static String getToken(Principal principal, String key) {
		try {
			String subject = JsonMapper.toJson(principal);
			return JWT.create().withIssuer("tmt").withClaim("date", DateUtils.getTodayTime()).withSubject(subject)
					.sign(Algorithm.HMAC256(key));
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 得到用户实体
	 * 
	 * @param token
	 * @param key
	 * @return
	 */
	public static Principal getSubject(String token, String key) {
		try {
			JWTVerifier verifier = JWT.require(Algorithm.HMAC256(key)).withIssuer("tmt").build();
			DecodedJWT dj = verifier.verify(token);
			String _principal = dj.getSubject();
			if (StringUtils.hasText(_principal)) {
				return JsonMapper.fromJson(_principal, Principal.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}