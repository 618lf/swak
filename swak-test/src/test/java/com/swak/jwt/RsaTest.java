package com.swak.jwt;

import com.swak.security.JWTAuthOptions;
import com.swak.security.JwtAuthProvider;
import com.swak.security.jwt.JWTOptions;
import com.swak.security.jwt.JWTPayload;
import com.swak.security.options.PubSecKeyOptions;

/**
 * openssl genrsa -out private.pem 2048
 * 
 * 
 * openssl pkcs8 -topk8 -inform PEM -in private.pem -out private_key.pem
 * -nocrypt
 * 
 * 
 * openssl rsa -in private.pem -outform PEM -pubout -out public.pem
 * 
 * 其中private_key.pem 是私钥； 其中public.pem 是公钥
 * 
 * @author lifeng
 * @date 2020年4月16日 下午9:47:27
 */
public class RsaTest {

	/**
	 * 创建： Options
	 * 
	 * @return
	 */
	private static JWTAuthOptions KeyStoreOptions() {

		// 默认的算法
		JWTOptions useOptions = new JWTOptions();
		useOptions.setAlgorithm("RS256");
		JWTAuthOptions options = new JWTAuthOptions();
		PubSecKeyOptions pubOptions = new PubSecKeyOptions();
		pubOptions = new PubSecKeyOptions();
		pubOptions.setAlgorithm("RS256");

		// public.pem 是公钥(给别人的，解密用)
		pubOptions.setPublicKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA54A5a6uiCXjSp9Fsouri\r\n"
				+ "wxadAcwYesaP4on2kc7ClOvViYzrw3UrgERTpIr+0DGc8eSZUFAnkkGAggS/Pi4e\r\n"
				+ "OYALneoY5b7iEHP9nCqry5ZPmuwmFklwqLEqq77NoOyo7EIcdLgnbYJJ1D8l0PO0\r\n"
				+ "SWo0sxa+lsaegFW0v42SBFN1wwpQhqxp1kMWAEnmJg/fQMAJUO4EKGUHOPDlaYaV\r\n"
				+ "XxTksZpU5ZJPr55wLnJAl1FFu0eji/9NBlq4CA7KcjgfUc1/lvIStaatbjyiexuG\r\n"
				+ "IXj1I4vy3f1nKF+gxERY8RDp+gXpV3M7UuUzKRldB1/yEXCy0JMsNgrVIh0yu5WN\r\n" + "LQIDAQAB");
		pubOptions.setSymmetric(false);

		// private_key.pem 是私钥（自己留着，加密用）
		pubOptions.setSecretKey("MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDngDlrq6IJeNKn\r\n"
				+ "0Wyi6uLDFp0BzBh6xo/iifaRzsKU69WJjOvDdSuARFOkiv7QMZzx5JlQUCeSQYCC\r\n"
				+ "BL8+Lh45gAud6hjlvuIQc/2cKqvLlk+a7CYWSXCosSqrvs2g7KjsQhx0uCdtgknU\r\n"
				+ "PyXQ87RJajSzFr6Wxp6AVbS/jZIEU3XDClCGrGnWQxYASeYmD99AwAlQ7gQoZQc4\r\n"
				+ "8OVphpVfFOSxmlTlkk+vnnAuckCXUUW7R6OL/00GWrgIDspyOB9RzX+W8hK1pq1u\r\n"
				+ "PKJ7G4YhePUji/Ld/WcoX6DERFjxEOn6BelXcztS5TMpGV0HX/IRcLLQkyw2CtUi\r\n"
				+ "HTK7lY0tAgMBAAECggEAEVVVd01bjZABXdVdZ/lkTy0jRl4suwyg+1pPHu23PgVG\r\n"
				+ "cM0fXfwo7KqxBJHvsFD2qlnubMgDnnzPDW0jkJlNZArFU+aSRKl9LbQxmf7L7Ctt\r\n"
				+ "/HCQDX0kNU1Gn6NirBjsMPxmOwsK0LU/p+eugi2tIw4TshES5iEq/vrr1KfZ9f7R\r\n"
				+ "TFy2fgXCkXK3H0kGEQq3V/DJzDuLE8xWdW7mKREyEqrKn/596Z3pPkwL+g+EwLqG\r\n"
				+ "A3QD4/tWxw2ZOGJ8oaVVHplBzfzs0wgoxQ5wIWlETzbNP+QdLUSkO8bZRCPHam77\r\n"
				+ "pIKIAs3hwEFhGRBJM6nmvbeqdneV6vMQxRDXwGPF9QKBgQD5HzQ9WpIGjw701aSb\r\n"
				+ "WGzeRtlILwQWXsIJkkkdXCUgKE+Bc84zho493BeVNPTEFCbFExHMRxbl6TzRR2Qg\r\n"
				+ "unwWj/nBmNaWaNZVnzNRNRG1IuGOOVBmiGj40va2tdBniqKh+grre1RU6nZ2IZd1\r\n"
				+ "LpnEi8BJLh99bhXt6PUFNEfnHwKBgQDt5HmIQaLjCFLUAa8vq9d3k8wOjV3wiOx8\r\n"
				+ "R8p+9p2nuNa+wmiKv+fM81XnwC5n5GWtXkiNWArZCIrERNRYo1Z4gnj/MVpO91wQ\r\n"
				+ "yyo6K4PbggC1tVAZih/15xCxI6G4Ndbg/Wo+bePrJfXyEECFt4lGHUZknoldk3lg\r\n"
				+ "4LI67Jc+MwKBgQCXpJPzCO+8iFaNWIMTgLb8ctoOo3f0egfZNa/F7HI46fDDlw+c\r\n"
				+ "s7pgE1pTm+GmfbG/4sTBuUBKATjhF3jupzWjsk5Qt01gTz6w7fCZ7RjzTuFH5sF3\r\n"
				+ "OG/Vju1Y+MF8VZp7yK3e+7qw2ev0Kqx8SzqUtpD8utO2UU2N/XEwkst19wKBgQCL\r\n"
				+ "FjMMPDvs+1faBmYTvCfNDQeYVoTe9l81xo+ZLb09a696dpXC0bLIPFk0l0NKkNn1\r\n"
				+ "xBMtOJWXEQx49jkk3dLXwIjXw1Ympy8XzoEQ45JDS734f1qmpABNuFwlBU0vM6M/\r\n"
				+ "hkqQ15UTTe1/Kote4tEKwmD+wUZVBL1Jq/Xox50hywKBgQD0a80XicDIiivkRfAb\r\n"
				+ "c51dR3MnDrolEEuv2hKuF0bodAzJ+mcaHM8L71UzLxg6KdjaV+gdUwBFihHA6vsq\r\n"
				+ "+wUSrdDX7qEgvaHXtaQFIEpIdscIg80n0rsOAEiqTveNxjTT+D8r8sT1qTIy+kJn\r\n"
				+ "MMdvkdJwurCN4YAAlLDFgI/+BA==");
		useOptions.setAlgorithm("RS256");
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
