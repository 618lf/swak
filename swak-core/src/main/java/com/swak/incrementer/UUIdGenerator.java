package com.swak.incrementer;

import java.util.UUID;

import com.swak.utils.StringUtils;

/**
 * UUID
 *
 * @author: lifeng
 * @date: 2020/3/29 11:53
 */
public class UUIdGenerator implements IdGenerator {

    @Override
    @SuppressWarnings("unchecked")
    public String id() {
        return uuid();
    }

    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", StringUtils.EMPTY);
    }
}
