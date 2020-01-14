package com.swak.reactivex.threads;

import java.util.concurrent.Executor;

/** See {@link #directExecutor} for behavioral notes. */
public enum DirectExecutor implements Executor {
	INSTANCE;

	@Override
	public void execute(Runnable command) {
		command.run();
	}

	@Override
	public String toString() {
		return "MoreExecutors.directExecutor()";
	}
}