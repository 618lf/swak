package com.swak.kotlin

import io.reactivex.Observable
import kotlinx.coroutines.experimental.rx2.rxObservable
import java.util.function.Supplier

/**
 * 发送 Supplier 提供的数据
 */
public fun <T> create(supplier: Supplier<T>): Observable<T> = rxObservable {
    this.send(supplier.get())
}