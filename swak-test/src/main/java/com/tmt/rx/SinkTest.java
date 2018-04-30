package com.tmt.rx;

import com.swak.reactivex.observable.ObservableCreate;
import com.swak.reactivex.observable.ObservableCreate.ObservableSink;

import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SinkTest {
	ObservableSink<String> sink;
	
	public String get() {
		return ObservableCreate.create(new Consumer<ObservableCreate.ObservableSink<String>>() {
			@Override
			public void accept(ObservableSink<String> t) throws Exception {
				setSink(t);
				System.out.println("执行这部分代码 : " + Thread.currentThread().getName());
				Thread.sleep(2000L);
				//getSink().success("haole");
			}
		}).subscribeOn(Schedulers.io()).blockingFirst();
	}
	
	public void setSink(ObservableSink<String> sink) {
		this.sink = sink;
	}
	
	public ObservableSink<String> getSink() {
		return sink;
	}
	
	public static void main(String[] args) throws InterruptedException {
		SinkTest test = new SinkTest();
		System.out.println(test.get());
		System.out.println("我获得了数据");
	}
}
