package com.swak.jwt;

import java.util.Map;

import com.swak.security.jwt.JWTEncode;
import com.swak.utils.Maps;

public class JsonTest {

	public static void main(String[] args) {
	  Map<String, Object> map = Maps.newHashMap();
	  Long exp = System.currentTimeMillis()/ 1000 + 30;
	  map.put("exp",exp);
      String json = JWTEncode.encode(map);
      System.out.println(json);
      map = JWTEncode.encode(json);
      System.out.println(map.get("exp").getClass());
	}
}
