package com.swak.reactivex.server.tcp;

import java.net.InetSocketAddress;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.reactivex.server.NettyContext;

import io.reactivex.Observable;

/**
 * Wrap a {@link NettyContext} obtained from a {@link Mono} and offer methods to manage
 * its lifecycle in a blocking fashion.
 *
 * @author Simon Baslé
 */
@Deprecated
public class BlockingNettyContext {

	private static final Logger LOG = LoggerFactory.getLogger(BlockingNettyContext.class);

	private final NettyContext context;
	private final String description;
	private Thread shutdownHook;

	public BlockingNettyContext(Observable<? extends NettyContext> contextAsync, String description) {
		this.description = description;
		this.context = contextAsync.doOnNext(ctx -> LOG.info("Started {} on {}", description, ctx.address()))
				.blockingSingle();
	}

	/**
	 * Get the {@link NettyContext} wrapped by this facade.
	 * 
	 * @return the original NettyContext.
	 */
	public NettyContext getContext() {
		return context;
	}

	/**
	 * Install a {@link Runtime#addShutdownHook(Thread) JVM shutdown hook} that will
	 * shutdown this {@link BlockingNettyContext} if the JVM is terminated
	 * externally.
	 * <p>
	 * The hook is removed if shutdown manually, and subsequent calls to this method
	 * are no-op.
	 */
	public void installShutdownHook() {
		// don't return the hook to discourage uninstalling it externally
		if (this.shutdownHook != null) {
			return;
		}
		this.shutdownHook = new Thread(this::shutdownFromJVM);
		Runtime.getRuntime().addShutdownHook(this.shutdownHook);
	}

	/**
	 * Remove a {@link Runtime#removeShutdownHook(Thread) JVM shutdown hook} if one
	 * was {@link #installShutdownHook() installed} by this
	 * {@link BlockingNettyContext}.
	 *
	 * @return true if there was a hook and it was removed, false otherwise.
	 */
	public boolean removeShutdownHook() {
		if (this.shutdownHook != null && Thread.currentThread() != this.shutdownHook) {
			Thread sdh = this.shutdownHook;
			this.shutdownHook = null;
			return Runtime.getRuntime().removeShutdownHook(sdh);
		}
		return false;
	}

	/**
	 * @return the current JVM shutdown hook. Shouldn't be passed to users.
	 */
	protected Thread getShutdownHook() {
		return this.shutdownHook;
	}

	/**
	 * Shut down the {@link NettyContext} and wait for its termination, up to the
	 * {@link #setLifecycleTimeout(Duration) lifecycle timeout}.
	 */
	public void shutdown() {
		if (context.isDisposed()) {
			return;
		}
		removeShutdownHook(); // only applies if not called from the hook's thread
		context.dispose();
		context.onClose()
				.doOnError(e -> LOG.error("Stopped {} on {} with an error {}", description, context.address(), e))
				.doOnTerminate(() -> LOG.info("Stopped {} on {}", description, context.address())).blockingSingle();
	}

	protected void shutdownFromJVM() {
		if (context.isDisposed()) {
			return;
		}
		final String hookDesc = Thread.currentThread().toString();

		context.dispose();
		context.onClose()
				.doOnError(e -> LOG.error("Stopped {} on {} with an error {} from JVM hook {}", description,
						context.address(), e, hookDesc))
				.doOnTerminate(
						() -> LOG.info("Stopped {} on {} from JVM hook {}", description, context.address(), hookDesc))
				.blockingSingle();
	}
	
	/**
	 * Return this server's port.
	 * @return The port the server is bound to.
	 */
	public int getPort() {
		return context.address().getPort();
	}

	/**
	 * Return the server's host String. That is, the hostname or in case the server was bound
	 * to a literal IP adress, the IP string representation (rather than performing a reverse-DNS
	 * lookup).
	 *
	 * @return the host string, without reverse DNS lookup
	 * @see NettyContext#address()
	 * @see InetSocketAddress#getHostString()
	 */
	public String getHost() {
		return context.address().getHostString();
	}

}
