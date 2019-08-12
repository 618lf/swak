package com.tmt.api.web;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.CompletableFuture;

import com.swak.utils.FileUtils;
import com.swak.utils.StringUtils;
import com.swak.vertx.annotation.GetMapping;
import com.swak.vertx.annotation.RestController;
import com.swak.vertx.annotation.VertxReferer;
import com.swak.vertx.transport.multipart.PlainFile;
import com.tmt.api.facade.GoodsServiceFacadeAsyncx;

/**
 * 测试大文本的zeroCopy
 * 
 * @author lifeng
 */
@RestController(path = "/api/test/bigtext")
public class ZeroCopyController {

	@VertxReferer
	private GoodsServiceFacadeAsyncx goodsService;

	/**
	 * 通过 zero copy 的方式输出文本
	 * 
	 * @return
	 */
	@GetMapping("/zerocopy")
	public CompletableFuture<PlainFile> zeroCopy() {
		File file = this.file();
		return CompletableFuture.completedFuture(PlainFile.of(file));
	}

	/**
	 * 不通过 zero copy 的方式输出文本
	 * 
	 * @return
	 */
	@GetMapping("/notzerocopy")
	public CompletableFuture<String> notzeroCopy() {
		File file = this.file();
		byte[] bytes = FileUtils.read(file);
		return CompletableFuture.completedFuture(StringUtils.newStringUtf8(bytes));
	}

	/**
	 * 不通过 zero copy 的方式输出文本
	 * 
	 * @return
	 */
	@GetMapping("/fromdb")
	public CompletableFuture<String> fromdb() {
		return goodsService.get().thenApply(res -> res.getRemarks());
	}

	private File file() {
		return new File("/home/lifeng/Desktop/bigtext.txt");
	}

	/**
	 * 占用大小，和数据库的长度差不多
	 * 
	 * @param args
	 * @throws UnsupportedEncodingException
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {
		File file = new File("/home/lifeng/Desktop/bigtext.txt");
		byte[] bytes = FileUtils.read(file);
		String text = new String(bytes, "utf-8");
		System.out.println(text.getBytes().length / 1024);
	}
}