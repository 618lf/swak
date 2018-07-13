package com.swak.security.web.captcha;

import java.io.IOException;
import java.util.concurrent.CompletionStage;

import javax.imageio.ImageIO;

import com.swak.executor.Workers;
import com.swak.reactivex.transport.http.multipart.MimeType;
import com.swak.reactivex.transport.http.server.HttpServerRequest;
import com.swak.reactivex.transport.http.server.HttpServerResponse;
import com.swak.reactor.publisher.MonoFalse;
import com.swak.security.web.captcha.builder.ABuilder;
import com.swak.security.web.captcha.builder.BBuilder;
import com.swak.security.web.captcha.builder.Builder;
import com.swak.security.web.captcha.builder.CBuilder;
import com.swak.security.web.captcha.builder.DBuilder;
import com.swak.security.web.captcha.builder.EBuilder;
import com.swak.security.web.captcha.builder.FBuilder;
import com.swak.security.web.captcha.builder.GBuilder;
import com.swak.security.web.captcha.builder.HBuilder;
import com.swak.security.web.cookie.CookieProvider;
import com.swak.utils.IOUtils;
import com.swak.utils.Ints;
import com.swak.utils.StringUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import reactor.core.publisher.Mono;

/**
 * 验证码管理器
 * 
 * java 验证码的效率如下： 生成：20w每秒 存储：很慢5000/s
 * 
 * 所以對於圖片的處理最好是使用lua 來做。 如果堅持要用java来处理则，被列为耗时的api：
 * 
 * 总结下lua下的图片处理： 1. 验证码： 目前只有 lua-gd 这样一个lua 包，而且好久没更新. 2. 图片处理到可以使用 imageMAGICK
 * lua 或 im4java 这样的工具
 * 
 * : ImageIo 是一个耗时的操作
 * 
 * @author lifeng
 */
public class CaptchaManager {

	private static String VALIDATE_CODE = "captcha-etag";
	private static String IMAGE_TYPE = "JPEG";
	private static String TYPES = "ABCDEFGHJK";

	/**
	 * 创建验证码
	 * 
	 * @return
	 */
	public static Captcha build() {
		return getBuilder().build();
	}

	public static Builder getBuilder() {
		int num = Ints.random(TYPES.length());
		char type = TYPES.charAt(num);
		switch (type) {
		case 'A':
			return new ABuilder();
		case 'B':
			return new BBuilder();
		case 'C':
			return new CBuilder();
		case 'D':
			return new DBuilder();
		case 'E':
			return new EBuilder();
		case 'F':
			return new FBuilder();
		case 'G':
			return new GBuilder();
		case 'H':
			return new HBuilder();
		case 'J':
			return new HBuilder();
		case 'K':
			return new HBuilder();
		default:
			return new ABuilder();
		}
	}

	/**
	 * 将验证码输出
	 * 
	 * @param captcha
	 * @param response
	 */
	public static CompletionStage<Void> image(HttpServerRequest request, HttpServerResponse response) {
		Captcha captcha = build();
		CompletionStage<Void> task = Workers.future(() ->{
			// 直接申请一个直接内存， OUT 的时候会释放
			ByteBuf buf = Unpooled.directBuffer(1024);
			ByteBufOutputStream out = new ByteBufOutputStream(buf);
			try {
				ImageIO.write(captcha.getImage(), IMAGE_TYPE, out);
				response.buffer(buf);
				response.mime(MimeType.getMimeType(IMAGE_TYPE));
			} catch (IOException e) {
			} finally {
				IOUtils.closeQuietly(out);
			}
		});
		return task.thenCompose(v ->{
			return CookieProvider.setAttribute(request, response, VALIDATE_CODE, captcha.getResult());
		});
	}

	/**
	 * 将验证码输出
	 * 
	 * @param captcha
	 * @param response
	 */
	public static CompletionStage<Void> base64(HttpServerRequest request, HttpServerResponse response) {
		return image(request, response).thenAccept(v -> {
			response.base64();
		});
	}

	/**
	 * 校验验证码
	 * 
	 * @param captcha
	 * @param cCode
	 * @return
	 */
	public static Mono<Boolean> check(HttpServerRequest request, HttpServerResponse response, String captcha) {
		if (captcha == null || captcha.isEmpty() || captcha.length() <= 1) {
			return MonoFalse.instance();
		}
		CompletionStage<String> _captcha = CookieProvider.getAttribute(request, response, VALIDATE_CODE);
		if (_captcha != null) {
			return Mono.fromCompletionStage(_captcha).map(s -> {
				if (s == null || s.isEmpty() || s.length() <= 1) {
					return false;
				}
				return StringUtils.equalsIgnoreCase(captcha, s);
			});
		}
		return MonoFalse.instance();
	}
}