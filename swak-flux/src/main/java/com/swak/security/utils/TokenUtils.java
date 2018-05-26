package com.swak.security.utils;

import com.swak.reactivex.Principal;
import com.swak.security.jwt.JWT;
import com.swak.security.jwt.JWTVerifier;
import com.swak.security.jwt.algorithms.Algorithm;
import com.swak.security.jwt.interfaces.DecodedJWT;
import com.swak.utils.DateUtils;
import com.swak.utils.JsonMapper;
import com.swak.utils.StringUtils;

/**
 * 创建token Jwts 依赖 fastxml 需要研究是否有替代的方案
 * 
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

	public static void main(String[] args) {
		Principal principal = new Principal(1L, "lifeng");
		String token = TokenUtils.getToken(principal, "secret");
		principal = TokenUtils.getSubject(token, "secret");
		System.out.println(JsonMapper.toJson(principal));
	}
}