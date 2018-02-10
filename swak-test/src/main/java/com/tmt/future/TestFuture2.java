package com.tmt.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

public class TestFuture2 {

//	/**
//	 * 进行变换 supplyAsync -- thenApplyAsync
//	 */
	@Test
	public void thenApply() {
		String result = CompletableFuture.supplyAsync(() -> "hello").thenApplyAsync(s -> s + " world").join();
		System.out.println(result);
	}
//
//	/**
//	 * 进行消耗 supplyAsync -- thenAcceptAsync
//	 */
//	@Test
//	public void thenAccept() {
//		CompletableFuture.supplyAsync(() -> "hello").thenAcceptAsync(s -> System.out.println(s + " world2"));
//	}
//
//	/**
//	 * 对上一步的计算结果不关心，执行下一个操作。
//	 * supplyAsync -- thenRunAsync
//	 */
//	@Test
//	public void thenRun() {
//		CompletableFuture.supplyAsync(() -> {
//			try {
//				Thread.sleep(2000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			return "hello";
//		}).thenRunAsync(() -> System.out.println("hello world"));
//	}
//	
//	/**
//	 * 结合两个CompletionStage的结果，进行转化后返回
//	 */
//	@Test
//    public void thenCombine() {
//        String result = CompletableFuture.supplyAsync(() -> {
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            return "hello1";
//        }).thenCombine(CompletableFuture.supplyAsync(() -> {
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            return "world1";
//        }), (s1, s2) -> s1 + " " + s2).join();
//        System.out.println(result);
//    }
//	
//	/**
//	 * 结合两个CompletionStage的结果，进行消耗
//	 */
//	@Test
//    public void thenAcceptBoth() {
//        CompletableFuture.supplyAsync(() -> {
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            return "hello2";
//        }).thenAcceptBoth(CompletableFuture.supplyAsync(() -> {
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            return "world2";
//        }), (s1, s2) -> System.out.println(s1 + " " + s2));
//        while (true){}
//    }
//	
//	@Test
//    public void runAfterBoth(){
//        CompletableFuture.supplyAsync(() -> {
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            return "s1";
//        }).runAfterBothAsync(CompletableFuture.supplyAsync(() -> {
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            return "s2";
//        }), () -> System.out.println("hello world"));
//        while (true){}
//    }
//	
//	/**
//	 * 两个CompletionStage，谁计算的快，我就用那个CompletionStage的结果进行下一步的转化操作。
//	 * apply 有返回值
//	 */
//	@Test
//    public void applyToEither() {
//        String result = CompletableFuture.supplyAsync(() -> {
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            return "s1";
//        }).applyToEither(CompletableFuture.supplyAsync(() -> {
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            return "hello world";
//        }), s -> s).join();
//        System.out.println(result);
//    }
//	
//	/**
//	 * 两个CompletionStage，谁计算的快，我就用那个CompletionStage的结果进行下一步的消耗操作。
//	 * accept 无返回值
//	 */
//	@Test
//    public void acceptEither() {
//        CompletableFuture.supplyAsync(() -> {
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            return "s1";
//        }).acceptEither(CompletableFuture.supplyAsync(() -> {
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            return "hello world";
//        }), System.out::println);
//        while (true){}
//    }
//	
//	/**
//	 * 两个CompletionStage，任何一个完成了都会执行下一步的操作（Runnable）。
//	 * run 是不关心计算结果
//	 */
//	@Test
//    public void runAfterEither() {
//        CompletableFuture.supplyAsync(() -> {
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            return "s1";
//        }).runAfterEither(CompletableFuture.supplyAsync(() -> {
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            return "s2";
//        }), () -> System.out.println("hello world"));
//        while (true) {
//        }
//    }
	
//	/**
//	 * exceptionally 捕获异常
//	 */
//	@Test
//    public void exceptionally() {
//        String result = CompletableFuture.supplyAsync(() -> {
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            if (1 == 1) {
//                throw new RuntimeException("测试一下异常情况");
//            }
//            return "s1";
//        }).exceptionally(e -> {
//            System.out.println(e.getMessage());
//            return "hello world";
//        }).join();
//        System.out.println(result);
//    }
	
//	/**
//	 * 当运行完成时，对结果的记录。
//	 */
//	@Test
//    public void whenComplete() {
//        String result = CompletableFuture.supplyAsync(() -> {
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            if (1 == 1) {
//                throw new RuntimeException("测试一下异常情况");
//            }
//            return "s1";
//        }).whenComplete((s, t) -> {
//            System.out.println(s); //获取不到
//            System.out.println(t.getMessage());
//        }).exceptionally(e -> {
//            //System.out.println(e.getMessage());
//            return "hello world";
//        }).join();
//        System.out.println(result);
//    }
	
//	/**
//	 * 
//	 */
//	@Test
//    public void handle() {
//        String result = CompletableFuture.supplyAsync(() -> {
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            //出现异常
//            if (1 == 1) {
//                throw new RuntimeException("测试一下异常情况");
//            }
//            return "s1";
//        }).handle((s, t) -> {
//            if (t != null) {
//                return "hello world";
//            }
//            return s;
//        }).join();
//        System.out.println(result);
//    }
	
	public static CompletableFuture<String> calculate(String input) {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            System.out.println(input);
            return input + "---" + input.length();
        }, executor);
        return future;
    }
	
	@Test
	public void testThenCompose() throws InterruptedException, ExecutionException {
		ExecutorService executor = Executors.newFixedThreadPool(5);
		CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> {
            return "zero";
        }, executor);
		
		CompletableFuture<CompletableFuture<String>> f4 = f1.thenApply(TestFuture2::calculate);
		System.out.println("f4.get:"+f4.get().get());

		// 类似flatMap
        CompletableFuture<String> f5 = f1.thenCompose(TestFuture2::calculate);
        System.out.println("f5.get:"+f5.get());

        System.out.println(f1.join());
	}
}
