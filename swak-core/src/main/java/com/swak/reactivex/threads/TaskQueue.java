/*
 * Copyright (c) 2011-2017 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package com.swak.reactivex.threads;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;

/**
 * A task queue that always run all tasks in order. The executor to run the
 * tasks is passed when the tasks when the tasks are executed, this executor is
 * not guaranteed to be used, as if several tasks are queued, the original
 * thread will be used.
 * <p>
 * More specifically, any call B to the
 * method that happens-after another call A to the same method, will result in
 * B's task running after A's.
 *
 * @author <a href="david.lloyd@jboss.com">David Lloyd</a>
 * @author <a href="mailto:tim.fox@jboss.com">Tim Fox</a>
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class TaskQueue {

    static final Logger log = LoggerFactory.getLogger(TaskQueue.class);
    private final LinkedList<Task> tasks = new LinkedList<>();
    private final Runnable runner;
    private Context current;

    public TaskQueue() {
        runner = this::run;
    }

    private void run() {
        for (; ; ) {
            final Task task;
            synchronized (tasks) {
                task = tasks.poll();
                if (task == null) {
                    current = null;
                    return;
                }
                if (task.exec != current) {
                    tasks.addFirst(task);
                    task.exec.execute(runner);
                    current = task.exec;
                    return;
                }
            }
            try {
                task.runnable.run();
            } catch (Throwable t) {
                log.error("Caught unexpected Throwable", t);
            }
        }
    }

    /**
     * Run a task.
     *
     * @param task the task to run.
     */
    public void execute(Runnable task, Context executor) {
        synchronized (tasks) {
            tasks.add(new Task(task, executor));
            if (current == null) {
                current = executor;
                executor.execute(runner);
            }
        }
    }

    private static class Task {

        private final Runnable runnable;
        private final Context exec;

        public Task(Runnable runnable, Context exec) {
            this.runnable = runnable;
            this.exec = exec;
        }
    }
}
