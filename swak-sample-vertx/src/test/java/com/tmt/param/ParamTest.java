package com.tmt.param;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import com.swak.http.builder.RequestBuilder;
import com.swak.utils.JsonMapper;
import com.swak.utils.Maps;
import com.tmt.AppRunnerTest;

/**
 * 参数的测试
 * 
 * @author lifeng
 */
public class ParamTest extends AppRunnerTest {

	@Test
	public void testString() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		RequestBuilder.get().setUrl("http://127.0.0.1:8080/api/param/string").text().future().thenAccept(res -> {
			System.out.println(res);
			latch.countDown();
		});
		latch.await();
	}

	@Test
	public void testStringId() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		RequestBuilder.get().setUrl("http://127.0.0.1:8080/api/param/string/123").text().future().thenAccept(res -> {
			System.out.println(res);
			latch.countDown();
		});
		latch.await();
	}

	@Test
	public void string_get_param() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		RequestBuilder.post().setUrl("http://127.0.0.1:8080/api/param/post_param").text().addFormParam("p1", "123")
				.addFormParam("p2", "1234").addFormParam("p3", "1").addFormParam("p3", "2").addFormParam("p3", "3")
				.future().thenAccept(res -> {
					System.out.println(res);
					latch.countDown();
				});
		latch.await();
	}

	@Test
	public void post_param_obj() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		RequestBuilder.post().setUrl("http://127.0.0.1:8080/api/param/post_param_obj").text().addFormParam("p1", "123")
				.addFormParam("p2", "1234").addFormParam("p3", "1").addFormParam("p3", "2").addFormParam("p3", "3")
				.future().thenAccept(res -> {
					System.out.println(res);
					latch.countDown();
				});
		latch.await();
	}

	@Test
	public void post_param_obj2() throws InterruptedException {
		Map<String, Object> p4 = Maps.newHashMap();
		p4.put("a", "a");
		p4.put("b", "b");
		p4.put("c", "c");
		CountDownLatch latch = new CountDownLatch(1);
		RequestBuilder.post().setUrl("http://127.0.0.1:8080/api/param/post_param_obj").text()
				.addFormParam("param[p1]", "123").addFormParam("param[p2]", "1234").addFormParam("param[p3]", "1")
				.addFormParam("param[p3]", "2").addFormParam("param[p3]", "3")
				.addFormParam("param[p4]", JsonMapper.toJson(p4)).future().thenAccept(res -> {
					System.out.println(res);
					latch.countDown();
				});
		latch.await();
	}

	@Test
	public void post_param_obj3() throws InterruptedException {
		Map<String, Object> p4 = Maps.newHashMap();
		p4.put("a", "a");
		p4.put("b", "b");
		p4.put("c", "c");
		CountDownLatch latch = new CountDownLatch(1);
		RequestBuilder.post().setUrl("http://127.0.0.1:8080/api/param/post_param_obj").text()
				.addFormParam("param[p1]", "123").addFormParam("param[p2]", "1234").addFormParam("param[p3][0]", "1")
				.addFormParam("param[p3][1]", "2").addFormParam("param[p3][2]", "3")
				.addFormParam("param[p4]", JsonMapper.toJson(p4)).future().thenAccept(res -> {
					System.out.println(res);
					latch.countDown();
				});
		latch.await();
	}

	@Test
	public void post_param_anno() throws InterruptedException {
		Map<String, Object> p4 = Maps.newHashMap();
		p4.put("a", "a");
		p4.put("b", "b");
		p4.put("c", "c");
		CountDownLatch latch = new CountDownLatch(1);
		RequestBuilder.post().setUrl("http://127.0.0.1:8080/api/param/post_param_anno").text()
				.addFormParam("json", JsonMapper.toJson(p4)).addHeader("name", "lifeng").future().thenAccept(res -> {
					System.out.println(res);
					latch.countDown();
				});
		latch.await();
	}

	@Test
	public void json() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		RequestBuilder.get().setUrl("http://127.0.0.1:8080/api/param/json").text().addHeader("name", "lifeng").future()
				.thenAccept(res -> {
					System.out.println(res);
					latch.countDown();
				});
		latch.await();
	}

	@Test
	public void xml() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		RequestBuilder.get().setUrl("http://127.0.0.1:8080/api/param/xml").text().addHeader("name", "lifeng").future()
				.thenAccept(res -> {
					System.out.println(res);
					latch.countDown();
				});
		latch.await();
	}

	@Test
	public void zerocopy() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		RequestBuilder.get().setUrl("http://127.0.0.1:8080/api/param/zerocopy").text().addHeader("name", "lifeng")
				.future().thenAccept(res -> {
					System.out.println(res.toString().length());
					latch.countDown();
				});
		latch.await();
	}

	@Test
	public void html() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		RequestBuilder.get().setUrl("http://127.0.0.1:8080/api/param/html").text().addHeader("name", "lifeng").future()
				.thenAccept(res -> {
					System.out.println("html = " + res);
					latch.countDown();
				});
		latch.await();
	}
}