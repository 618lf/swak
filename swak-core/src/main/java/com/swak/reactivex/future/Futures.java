package com.swak.reactivex.future;

import com.swak.utils.Lists;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * 快捷处理异步结果
 *
 * @author: lifeng
 * @date: 2020/3/29 12:16
 */
public class Futures {

    /**
     * 顺序执行任务
     *
     * @param <T>     任意
     * @param futures 任务池
     * @return 结果
     */
    public static <T> CompletableFuture<Void> order(List<Supplier<CompletableFuture<T>>> futures) {
        return new Order<>(futures).future();
    }

    /**
     * 所有结果全部处理完成
     */
    @SuppressWarnings("rawtypes")
    public static <T> CompletableFuture<Void> all(List<CompletableFuture<T>> futures) {
        CompletableFuture[] allFutures = new CompletableFuture[futures.size()];
        return CompletableFuture.allOf(futures.toArray(allFutures));
    }

    /**
     * 所有结果全部处理完成
     */
    @SuppressWarnings("rawtypes")
    public static <T> CompletableFuture<List<T>> allResult(List<CompletableFuture<T>> futures) {
        CompletableFuture[] allFutures = new CompletableFuture[futures.size()];
        return CompletableFuture.allOf(futures.toArray(allFutures)).thenApply(res -> {
            List<T> ts = Lists.newArrayList();
            try {
                for (CompletableFuture<T> future : futures) {
                    ts.add(future.get());
                }
            } catch (Exception ignored) {
            }
            return ts;
        });
    }

    /**
     * 顺序任务
     *
     * @author lifeng
     * @date 2020年9月16日 下午5:54:48
     */
    private static class Order<T> {
        private List<Supplier<CompletableFuture<T>>> futures;
        private int index;
        private CompletableFuture<Void> future = new CompletableFuture<>();

        private Order(List<Supplier<CompletableFuture<T>>> futures) {
            this.futures = futures;
        }

        /**
         * 外部任务
         *
         * @return 异步结果
         */
        public CompletableFuture<Void> future() {

            // 执行内部任务
            this.innerFuture();

            // 返回异步结果
            return future;
        }

        /**
         * 内部顺序执行任务
         */
        private void innerFuture() {
            if (index >= futures.size()) {
                future.complete(null);
            }
            futures.get(index++).get().whenComplete((r, e) -> {
                if (e != null) {
                    future.completeExceptionally(e);
                } else {
                    this.innerFuture();
                }
            });
        }
    }
}