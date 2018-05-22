package com.tmt.coroutine.kotlin;

import java.util.concurrent.CountDownLatch;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.swak.kotlin.TasksKt;

public class KotlinTest {

	static OkHttpClient client = new OkHttpClient();

	public static Response task(int no) {
		try {
			System.out.println(no + "号任务，获取数据的线程 开始：" + Thread.currentThread().getName());
			Request request = new Request.Builder().url("http://127.0.0.1:8080/admin/hello/say/void").build();
			Call call = client.newCall(request);
			//return call.execute();
			return null;
		}catch (Exception e) {
			return null;
		}finally {
			System.out.println(no + "号任务，获取数据的线程 结束：" + Thread.currentThread().getName());
		}
	}

	public static void main(String[] args) throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		for (int i = 0; i < 10; i++) {
			final int no = i;
			TasksKt.taskAsync(10000, (t) ->{
				return task(no);
			});
		}

		latch.await();
	}
}
