/*
 * Copyright (C) 2011 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.swak.utils;

import java.util.*;
import java.util.concurrent.*;

/**
 * Static utility methods pertaining to {@link Queue} and {@link Deque}
 * instances. Also see this class's counterparts {@link Lists}, {@link Sets},
 * and {@link Maps}.
 *
 * @author Kurt Alfred Kluever
 * @since 11.0
 */
public final class Queues {
    private Queues() {
    }

    // ArrayBlockingQueue

    /**
     * Creates an empty {@code ArrayBlockingQueue} with the given (fixed) capacity
     * and nonfair access policy.
     */
    public static <E> ArrayBlockingQueue<E> newArrayBlockingQueue(int capacity) {
        return new ArrayBlockingQueue<>(capacity);
    }

    /**
     * Creates an empty {@code ArrayDeque}.
     *
     * @since 12.0
     */
    public static <E> ArrayDeque<E> newArrayDeque() {
        return new ArrayDeque<>();
    }

    /**
     * Creates an {@code ArrayDeque} containing the elements of the specified
     * iterable, in the order they are returned by the iterable's iterator.
     *
     * @since 12.0
     */
    public static <E> ArrayDeque<E> newArrayDeque(Iterable<? extends E> elements) {
        if (elements instanceof Collection) {
            return new ArrayDeque<>(CollectionUtils.cast(elements));
        }
        ArrayDeque<E> deque = new ArrayDeque<>();
        addAll(deque, elements);
        return deque;
    }

    /**
     * Creates an empty {@code ConcurrentLinkedQueue}.
     */
    public static <E> ConcurrentLinkedQueue<E> newConcurrentLinkedQueue() {
        return new ConcurrentLinkedQueue<>();
    }

    /**
     * Creates a {@code ConcurrentLinkedQueue} containing the elements of the
     * specified iterable, in the order they are returned by the iterable's
     * iterator.
     */
    public static <E> ConcurrentLinkedQueue<E> newConcurrentLinkedQueue(Iterable<? extends E> elements) {
        if (elements instanceof Collection) {
            return new ConcurrentLinkedQueue<>(CollectionUtils.cast(elements));
        }
        ConcurrentLinkedQueue<E> queue = new ConcurrentLinkedQueue<>();
        addAll(queue, elements);
        return queue;
    }

    /**
     * Creates an empty {@code LinkedBlockingDeque} with a capacity of
     * {@link Integer#MAX_VALUE}.
     *
     * @since 12.0
     */
    public static <E> LinkedBlockingDeque<E> newLinkedBlockingDeque() {
        return new LinkedBlockingDeque<>();
    }

    /**
     * Creates an empty {@code LinkedBlockingDeque} with the given (fixed) capacity.
     *
     * @throws IllegalArgumentException if {@code capacity} is less than 1
     * @since 12.0
     */
    public static <E> LinkedBlockingDeque<E> newLinkedBlockingDeque(int capacity) {
        return new LinkedBlockingDeque<>(capacity);
    }

    /**
     * Creates a {@code LinkedBlockingDeque} with a capacity of
     * {@link Integer#MAX_VALUE}, containing the elements of the specified iterable,
     * in the order they are returned by the iterable's iterator.
     *
     * @since 12.0
     */
    public static <E> LinkedBlockingDeque<E> newLinkedBlockingDeque(Iterable<? extends E> elements) {
        if (elements instanceof Collection) {
            return new LinkedBlockingDeque<>(CollectionUtils.cast(elements));
        }
        LinkedBlockingDeque<E> deque = new LinkedBlockingDeque<>();
        addAll(deque, elements);
        return deque;
    }

    /**
     * Creates an empty {@code LinkedBlockingQueue} with a capacity of
     * {@link Integer#MAX_VALUE}.
     */
    public static <E> LinkedBlockingQueue<E> newLinkedBlockingQueue() {
        return new LinkedBlockingQueue<>();
    }

    /**
     * Creates an empty {@code LinkedBlockingQueue} with the given (fixed) capacity.
     *
     * @throws IllegalArgumentException if {@code capacity} is less than 1
     */
    public static <E> LinkedBlockingQueue<E> newLinkedBlockingQueue(int capacity) {
        return new LinkedBlockingQueue<>(capacity);
    }

    /**
     * Creates a {@code LinkedBlockingQueue} with a capacity of
     * {@link Integer#MAX_VALUE}, containing the elements of the specified iterable,
     * in the order they are returned by the iterable's iterator.
     *
     * @param elements the elements that the queue should contain, in order
     * @return a new {@code LinkedBlockingQueue} containing those elements
     */
    public static <E> LinkedBlockingQueue<E> newLinkedBlockingQueue(Iterable<? extends E> elements) {
        if (elements instanceof Collection) {
            return new LinkedBlockingQueue<>(CollectionUtils.cast(elements));
        }
        LinkedBlockingQueue<E> queue = new LinkedBlockingQueue<>();
        addAll(queue, elements);
        return queue;
    }

    /**
     * Creates an empty {@code PriorityBlockingQueue} with the ordering given by its
     * elements' natural ordering.
     *
     * @since 11.0 (requires that {@code E} be {@code Comparable} since 15.0).
     */
    @SuppressWarnings("rawtypes")
    public static <E extends Comparable> PriorityBlockingQueue<E> newPriorityBlockingQueue() {
        return new PriorityBlockingQueue<>();
    }

    /**
     * Creates a {@code PriorityBlockingQueue} containing the given elements.
     *
     * <p>
     * <b>Note:</b> If the specified iterable is a {@code SortedSet} or a
     * {@code PriorityQueue}, this priority queue will be ordered according to the
     * same ordering.
     *
     * @since 11.0 (requires that {@code E} be {@code Comparable} since 15.0).
     */
    @SuppressWarnings("rawtypes")
    public static <E extends Comparable> PriorityBlockingQueue<E> newPriorityBlockingQueue(
            Iterable<? extends E> elements) {
        if (elements instanceof Collection) {
            return new PriorityBlockingQueue<>(CollectionUtils.cast(elements));
        }
        PriorityBlockingQueue<E> queue = new PriorityBlockingQueue<>();
        addAll(queue, elements);
        return queue;
    }

    /**
     * Creates an empty {@code PriorityQueue} with the ordering given by its
     * elements' natural ordering.
     *
     * @since 11.0 (requires that {@code E} be {@code Comparable} since 15.0).
     */
    @SuppressWarnings("rawtypes")
    public static <E extends Comparable> PriorityQueue<E> newPriorityQueue() {
        return new PriorityQueue<>();
    }

    /**
     * Creates a {@code PriorityQueue} containing the given elements.
     *
     * <p>
     * <b>Note:</b> If the specified iterable is a {@code SortedSet} or a
     * {@code PriorityQueue}, this priority queue will be ordered according to the
     * same ordering.
     *
     * @since 11.0 (requires that {@code E} be {@code Comparable} since 15.0).
     */
    @SuppressWarnings("rawtypes")
    public static <E extends Comparable> PriorityQueue<E> newPriorityQueue(Iterable<? extends E> elements) {
        if (elements instanceof Collection) {
            return new PriorityQueue<>(CollectionUtils.cast(elements));
        }
        PriorityQueue<E> queue = new PriorityQueue<>();
        addAll(queue, elements);
        return queue;
    }

    /**
     * Creates an empty {@code SynchronousQueue} with nonfair access policy.
     */
    public static <E> SynchronousQueue<E> newSynchronousQueue() {
        return new SynchronousQueue<>();
    }

    /**
     * Drains the queue as {@link BlockingQueue#drainTo(Collection, int)}, but if
     * the requested {@code
     * numElements} elements are not available, it will wait for them up to the
     * specified timeout.
     *
     * @param q           the blocking queue to be drained
     * @param buffer      where to add the transferred elements
     * @param numElements the number of elements to be waited for
     * @param timeout     how long to wait before giving up
     * @return the number of elements transferred
     * @throws InterruptedException if interrupted while waiting
     * @since 28.0
     */
    public static <E> int drain(BlockingQueue<E> q, Collection<? super E> buffer, int numElements,
                                java.time.Duration timeout) throws InterruptedException {
        return drain(q, buffer, numElements, timeout.toNanos(), TimeUnit.NANOSECONDS);
    }

    /**
     * Drains the queue as {@link BlockingQueue#drainTo(Collection, int)}, but if
     * the requested {@code
     * numElements} elements are not available, it will wait for them up to the
     * specified timeout.
     *
     * @param q           the blocking queue to be drained
     * @param buffer      where to add the transferred elements
     * @param numElements the number of elements to be waited for
     * @param timeout     how long to wait before giving up, in units of {@code unit}
     * @param unit        a {@code TimeUnit} determining how to interpret the timeout
     *                    parameter
     * @return the number of elements transferred
     * @throws InterruptedException if interrupted while waiting
     */
    public static <E> int drain(BlockingQueue<E> q, Collection<? super E> buffer, int numElements, long timeout,
                                TimeUnit unit) throws InterruptedException {
        Preconditions.checkNotNull(buffer);
        /*
         * This code performs one System.nanoTime() more than necessary, and in return,
         * the time to execute Queue#drainTo is not added *on top* of waiting for the
         * timeout (which could make the timeout arbitrarily inaccurate, given a queue
         * that is slow to drain).
         */
        long deadline = System.nanoTime() + unit.toNanos(timeout);
        int added = 0;
        while (added < numElements) {
            // we could rely solely on #poll, but #drainTo might be more efficient when
            // there are multiple
            // elements already available (e.g. LinkedBlockingQueue#drainTo locks only once)
            added += q.drainTo(buffer, numElements - added);
            if (added < numElements) {
                E e = q.poll(deadline - System.nanoTime(), TimeUnit.NANOSECONDS);
                if (e == null) {
                    break;
                }
                buffer.add(e);
                added++;
            }
        }
        return added;
    }

    /**
     * Drains the queue as
     * drain(BlockingQueue, Collection, int, Duration)， but with a
     * different behavior in case it is interrupted while waiting. In that case, the
     * operation will continue as usual, and in the end the thread's interruption
     * status will be set (no {@code
     * InterruptedException} is thrown).
     *
     * @param q           the blocking queue to be drained
     * @param buffer      where to add the transferred elements
     * @param numElements the number of elements to be waited for
     * @param timeout     how long to wait before giving up
     * @return the number of elements transferred
     * @since 28.0
     */
    public static <E> int drainUninterruptibly(BlockingQueue<E> q, Collection<? super E> buffer, int numElements,
                                               java.time.Duration timeout) {
        return drainUninterruptibly(q, buffer, numElements, timeout.toNanos(), TimeUnit.NANOSECONDS);
    }

    /**
     * Drains the queue as
     * {@linkplain #drain(BlockingQueue, Collection, int, long, TimeUnit)}, but with
     * a different behavior in case it is interrupted while waiting. In that case,
     * the operation will continue as usual, and in the end the thread's
     * interruption status will be set (no {@code
     * InterruptedException} is thrown).
     *
     * @param q           the blocking queue to be drained
     * @param buffer      where to add the transferred elements
     * @param numElements the number of elements to be waited for
     * @param timeout     how long to wait before giving up, in units of {@code unit}
     * @param unit        a {@code TimeUnit} determining how to interpret the timeout
     *                    parameter
     * @return the number of elements transferred
     */
    public static <E> int drainUninterruptibly(BlockingQueue<E> q, Collection<? super E> buffer, int numElements,
                                               long timeout, TimeUnit unit) {
        Preconditions.checkNotNull(buffer);
        long deadline = System.nanoTime() + unit.toNanos(timeout);
        int added = 0;
        boolean interrupted = false;
        try {
            while (added < numElements) {
                // we could rely solely on #poll, but #drainTo might be more efficient when
                // there are
                // multiple elements already available (e.g. LinkedBlockingQueue#drainTo locks
                // only once)
                added += q.drainTo(buffer, numElements - added);
                if (added < numElements) {
                    E e;
                    while (true) {
                        try {
                            e = q.poll(deadline - System.nanoTime(), TimeUnit.NANOSECONDS);
                            break;
                        } catch (InterruptedException ex) {
                            interrupted = true;
                        }
                    }
                    if (e == null) {
                        break;
                    }
                    buffer.add(e);
                    added++;
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
        return added;
    }

    static <T> void addAll(Collection<T> addTo, Iterable<? extends T> elementsToAdd) {
        if (elementsToAdd instanceof Collection) {
            Collection<? extends T> c = CollectionUtils.cast(elementsToAdd);
            addTo.addAll(c);
            return;
        }
        addAll(addTo, Preconditions.checkNotNull(elementsToAdd).iterator());
    }

    static <T> void addAll(Collection<T> addTo, Iterator<? extends T> iterator) {
        Preconditions.checkNotNull(addTo);
        Preconditions.checkNotNull(iterator);
    }
}
