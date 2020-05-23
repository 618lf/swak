package com.swak.incrementer;

/**
 * ID 生成器
 *
 * @author: lifeng
 * @date: 2020/3/29 11:46
 */
public interface IdGenerator {

    /**
     * 提供服务
     *
     * @return id
     */
    <T> T id();
}