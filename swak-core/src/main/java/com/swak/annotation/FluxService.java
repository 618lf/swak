package com.swak.annotation;

import com.swak.utils.StringUtils;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * 注册服务
 *
 * @author: lifeng
 * @date: 2020/3/28 17:14
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Service
public @interface FluxService {

    /**
     * 实例化的名称
     */
    @AliasFor(annotation = Service.class)
    String value() default StringUtils.EMPTY;

    /**
     * 定义是否顺序执行 handler，每个Verticle 都有一个 Context，通过这个Context来提交需要运行的 HandlerHodler
     * <br>
     * <p>
     * WorkContext 中持有一个Queue，来保证运行这个Verticle的handler是顺序的。<br>
     * <p>
     * 也就是同一個Verticle 並不是并发执行的。<br>
     * <p>
     * 但是在JDBC阻塞时编程中，不需要此特性。默认情况下不使用此特性 <br>
     * <p>
     * 如果是非阻塞的Verticle 可以使用这个特性<br>
     */
    Context context() default Context.Concurrent;

    /**
     * 发布服务的个数
     */
    int instances() default 1;

    /**
     * 可以设置在哪个 pool 中运行
     */
    String use_pool() default StringUtils.EMPTY;

    /**
     * 指定服务类
     */
    Class<?> service() default void.class;
}