package com.swak.kotlin

import kotlinx.coroutines.experimental.reactor.mono
import reactor.core.publisher.Mono
import java.util.function.Supplier

/**
 * 发送 Supplier 提供的数据
 */
public fun <T> create(supplier: Supplier<T>): Mono<T> = mono {
    supplier.get()
}