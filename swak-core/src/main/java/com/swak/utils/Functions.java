package com.swak.utils;

import java.util.function.Function;

import org.springframework.lang.Nullable;

import com.swak.entity.Result;

/**
 * Functions
 *
 * @author: lifeng
 * @date: 2020/3/29 14:07
 */
public final class Functions {
    private Functions() {
    }

    /**
     * Returns the Result function.
     */
    public static Function<Object, Result> resultFunction() {
        return ResultFunction.INSTANCE;
    }

    /**
     * enum singleton pattern
     */
    private enum ResultFunction implements Function<Object, Result> {

        /**
         * 唯一对象
         */
        INSTANCE;

        @Override
        public Result apply(Object o) {
            return Result.success(o);
        }

        @Override
        public String toString() {
            return "Functions.resultFunction()";
        }
    }

    /**
     * Returns the identity function.
     */
    @SuppressWarnings("unchecked")
    public static <E> Function<E, E> identity() {
        return (Function<E, E>) IdentityFunction.INSTANCE;
    }

    /**
     * enum singleton pattern
     */
    private enum IdentityFunction implements Function<Object, Object> {

        /**
         * 唯一对象
         */
        INSTANCE;

        @Override
        public Object apply(@Nullable Object o) {
            return o;
        }

        @Override
        public String toString() {
            return "IdentityFunction.identity()";
        }
    }
}
