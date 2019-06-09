package com.swak.rocketmq.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * 事务回调监听
 * 
 * @author lifeng
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Listener {

    /**
     * Declare the txProducerGroup that is used to relate callback event to the listener, rocketMQTemplate must send a
     * transactional message with the declared txProducerGroup.
     * <p>
     * <p>It is suggested to use the default txProducerGroup if your system only needs to define a TransactionListener class.
     */
    String txProducerGroup() default "rocketmq_transaction_default_global_name";

    /**
     * Set ExecutorService params -- corePoolSize
     */
    int corePoolSize() default 1;

    /**
     * Set ExecutorService params -- maximumPoolSize
     */
    int maximumPoolSize() default 1;

    /**
     * Set ExecutorService params -- keepAliveTime
     */
    long keepAliveTime() default 1000 * 60; //60ms

    /**
     * Set ExecutorService params -- blockingQueueSize
     */
    int blockingQueueSize() default 2000;
}