package com.tmt.rx.my.v6;

/**
 * 测试 AsynJob
 * @author lifeng
 */
public class TestAsynJob {

	public static void main(String[] args) {

		/**
		 * 一个具体的任务
		 */
		AsynJob<String> one = new AsynJob<String>() {
			@Override
			public String call(CallBack<String> callBack) {
				String result = "123";
				System.out.println("第一获得值是String");
				callBack.onResult(result);
				return result;
			}
		};
		
		/**
		 * 第二个具体的任务是第一个具体的任务执行完成之后处理
		 */
		AsynJob<Integer> two = one.map(new Func<String, AsynJob<Integer>>() {
			@Override
			public AsynJob<Integer> call(String t) {
				return new AsynJob<Integer>() {
					@Override
					public Integer call(CallBack<Integer> callBack) {
						Integer result = Integer.parseInt(t);
						System.out.println("第二获得数据是int");
						callBack.onResult(result);
						return result;
					}
				};
			}
		});
		
		/**
		 * 第二个具体的任务是第一个具体的任务执行完成之后处理
		 */
		AsynJob<Long> three = two.map(new Func<Integer, AsynJob<Long>>(){
			@Override
			public AsynJob<Long> call(Integer t) {
				return new AsynJob<Long>() {
					@Override
					public Long call(CallBack<Long> callBack) {
						Long result = t.longValue();
						System.out.println("第三获得数据是long");
						callBack.onResult(result);
						return result;
					}
				};
			}
		});
		
		three.call(new CallBack<Long>() {
			@Override
			public void onResult(Long result) {
				System.out.println("获得的结果：" + result);
			}
			@Override
			public void onError() {
				System.out.println("执行错误");
			}
		});
	}
}