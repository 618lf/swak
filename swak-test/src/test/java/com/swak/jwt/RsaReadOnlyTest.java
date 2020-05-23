package com.swak.jwt;

import com.swak.security.JWTAuthOptions;
import com.swak.security.JwtAuthProvider;
import com.swak.security.jwt.JWTOptions;
import com.swak.security.options.PubSecKeyOptions;

/**
 * 只解密，适用于生成token 和使用token不在同一个地方
 * 
 * @see RsaTest
 * @author lifeng
 * @date 2020年4月16日 下午9:51:05
 */
public class RsaReadOnlyTest {

	/**
	 * 创建： Options
	 * 
	 * @return
	 */
	private static JWTAuthOptions KeyStoreOptions() {
		JWTOptions useOptions = new JWTOptions();
		useOptions.setAlgorithm("RS256");
		JWTAuthOptions options = new JWTAuthOptions();
		PubSecKeyOptions pubOptions = new PubSecKeyOptions();
		pubOptions = new PubSecKeyOptions();
		pubOptions.setAlgorithm("RS256");
		pubOptions.setPublicKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA54A5a6uiCXjSp9Fsouri\r\n"
				+ "wxadAcwYesaP4on2kc7ClOvViYzrw3UrgERTpIr+0DGc8eSZUFAnkkGAggS/Pi4e\r\n"
				+ "OYALneoY5b7iEHP9nCqry5ZPmuwmFklwqLEqq77NoOyo7EIcdLgnbYJJ1D8l0PO0\r\n"
				+ "SWo0sxa+lsaegFW0v42SBFN1wwpQhqxp1kMWAEnmJg/fQMAJUO4EKGUHOPDlaYaV\r\n"
				+ "XxTksZpU5ZJPr55wLnJAl1FFu0eji/9NBlq4CA7KcjgfUc1/lvIStaatbjyiexuG\r\n"
				+ "IXj1I4vy3f1nKF+gxERY8RDp+gXpV3M7UuUzKRldB1/yEXCy0JMsNgrVIh0yu5WN\r\n" + "LQIDAQAB");
		useOptions.setAlgorithm("RS256");
		options.addPubSecKey(pubOptions);
		options.setJWTOptions(useOptions);
		return options;
	}

	public static void main(String[] args) {
		String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJuYW1lIjoi5p2O6ZSLIiwiaWQiOiIxIn0.OqnCoOO2W6tmkMXuHPDKHYd4xMVOkbhITkGnj_GZptdeSCXm5iXPc9Dx_ZvvRUyisH_RV8gKowH47ZALsRu_KFiVnfPWY3tgaNzC-a6WIZtaGz5II0bRj-rbmfd7TQNm81OkbffVi8Vjnva6-mV6UpgEBb27lZEfRIQKNpMuTF1ooAy80ZrkW_c7i0Yj_BztiZZOR69VIDdh0K80jafC_W1dU9vNOjkaS1p6OU8-lAUb9VfCXH4cnNCdtYdEieVnwUKRGIEQDw3f5Jkak4cp72NzsDA-c00lDhK7iHC_w7HNuj2CYHno1eLNL9pflmefUFdLo450tK3Hozu1UtBMIw";
		JWTAuthOptions config = KeyStoreOptions();
		JwtAuthProvider jwt = new JwtAuthProvider(config);
		System.out.println("生成的 token:" + jwt.verifyToken(token));
	}
}
