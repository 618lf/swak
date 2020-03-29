/*
 * Copyright (c) 2011-2017 Pivotal Software Inc, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.swak.reactor.publisher;

import java.time.Duration;

import org.reactivestreams.Publisher;

import reactor.core.CoreSubscriber;
import reactor.core.Fuseable;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Operators;
import reactor.util.annotation.Nullable;

/**
 * Represents an empty publisher which only calls onSubscribe and onComplete.
 * <p>
 * This Publisher is effectively stateless and only a single instance any. Use
 * the {@link #instance()} method to obtain a properly type-parametrized view of
 * it.
 *
 * @author lifeng
 * @see <a href=
 * "https://github.com/reactor/reactive-streams-commons">Reactive-Streams-Commons</a>
 */
public final class MonoFalse extends Mono<Boolean> implements Fuseable.ScalarCallable<Boolean> {

    static final Publisher<Boolean> INSTANCE = new MonoFalse();

    MonoFalse() {
        // deliberately no op
    }

    @Override
    public void subscribe(CoreSubscriber<? super Boolean> actual) {
        Operators.complete(actual);
    }

    /**
     * Returns a properly parametrized instance of this empty Publisher.
     *
     * @param <T> the output type
     * @return a properly parametrized instance of this empty Publisher
     */
    public static Mono<Boolean> instance() {
        return (Mono<Boolean>) INSTANCE;
    }

    @Override
    @Nullable
    public Boolean call() throws Exception {
        return Boolean.FALSE;
    }

    @Override
    @Nullable
    public Boolean block(Duration m) {
        return Boolean.FALSE;
    }

    @Override
    @Nullable
    public Boolean block() {
        return Boolean.FALSE;
    }
}
