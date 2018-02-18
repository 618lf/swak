package com.swak.security.context;

import java.util.concurrent.Callable;

import com.swak.security.subjct.Subject;

/**
 * 简单的执行器
 * @author lifeng
 * @param <V>
 */
public class SubjectCallable<V> implements Callable<V> {
	
	private Subject subject;
	private Callable<V> target;
	public SubjectCallable(Subject subject, Callable<V> target) {
		this.target = target;
		this.subject = subject;
	}

	@Override
	public V call() throws Exception {
		try {
            this.bind();
            return target.call();
        } finally {
            this.restore();
        }
	}
	
	private void bind() {
		ThreadContext.remove();
        ThreadContext.bind(this.subject);
	}
	
	private void restore() {
		ThreadContext.remove();
	}
}