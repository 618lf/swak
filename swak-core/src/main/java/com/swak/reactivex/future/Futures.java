package com.swak.reactivex.future;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.swak.utils.Lists;

/**
 * 快捷处理异步结果
 *
 * @author: lifeng
 * @date: 2020/3/29 12:16
 */
public class Futures {

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
}