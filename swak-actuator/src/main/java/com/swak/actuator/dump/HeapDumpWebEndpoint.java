package com.swak.actuator.dump;

import static com.swak.Application.APP_LOGGER;

import java.io.Closeable;
import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.PlatformManagedObject;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import com.swak.actuator.endpoint.annotation.Endpoint;
import com.swak.actuator.endpoint.annotation.Operation;
import com.swak.common.entity.Result;

/**
 * Web {@link Endpoint} to expose heap dumps.
 *
 * @author Lari Hotari
 * @author Phillip Webb
 * @author Raja Kolli
 * @author Andy Wilkinson
 * @since 2.0.0
 */
@Endpoint(id = "heapdump")
public class HeapDumpWebEndpoint {

	private final long timeout;

	private final Lock lock = new ReentrantLock();

	private HeapDumper heapDumper;

	public HeapDumpWebEndpoint() {
		this(TimeUnit.SECONDS.toMillis(10));
	}

	protected HeapDumpWebEndpoint(long timeout) {
		this.timeout = timeout;
	}

	@Operation
	public Result heapDump(@Nullable Boolean live) {
		try {
			if (this.lock.tryLock(this.timeout, TimeUnit.MILLISECONDS)) {
				try {
					return Result.success(dumpHeap(live != null ? live : true));
				} finally {
					this.lock.unlock();
				}
			}
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		} catch (Exception ex) {
			return Result.error(ex.getMessage());
		}
		return Result.success();
	}

	private Resource dumpHeap(boolean live) throws IOException, InterruptedException {
		if (this.heapDumper == null) {
			this.heapDumper = createHeapDumper();
		}
		File file = createTempFile(live);
		this.heapDumper.dumpHeap(file, live);
		return new TemporaryFileSystemResource(file);
	}

	private File createTempFile(boolean live) throws IOException {
		String date = new SimpleDateFormat("yyyy-MM-dd-HH-mm").format(new Date());
		File file = File.createTempFile("heapdump" + date + (live ? "-live" : ""), ".hprof");
		file.delete();
		return file;
	}

	/**
	 * Factory method used to create the {@link HeapDumper}.
	 * 
	 * @return the heap dumper to use
	 * @throws HeapDumperUnavailableException
	 *             if the heap dumper cannot be created
	 */
	protected HeapDumper createHeapDumper() throws HeapDumperUnavailableException {
		return new HotSpotDiagnosticMXBeanHeapDumper();
	}

	/**
	 * Strategy interface used to dump the heap to a file.
	 */
	@FunctionalInterface
	protected interface HeapDumper {

		/**
		 * Dump the current heap to the specified file.
		 * 
		 * @param file
		 *            the file to dump the heap to
		 * @param live
		 *            if only <em>live</em> objects (i.e. objects that are reachable
		 *            from others) should be dumped
		 * @throws IOException
		 *             on IO error
		 * @throws InterruptedException
		 *             on thread interruption
		 */
		void dumpHeap(File file, boolean live) throws IOException, InterruptedException;

	}

	/**
	 * {@link HeapDumper} that uses
	 * {@code com.sun.management.HotSpotDiagnosticMXBean} available on Oracle and
	 * OpenJDK to dump the heap to a file.
	 */
	protected static class HotSpotDiagnosticMXBeanHeapDumper implements HeapDumper {

		private Object diagnosticMXBean;

		private Method dumpHeapMethod;

		@SuppressWarnings("unchecked")
		protected HotSpotDiagnosticMXBeanHeapDumper() {
			try {
				Class<?> diagnosticMXBeanClass = ClassUtils
						.resolveClassName("com.sun.management.HotSpotDiagnosticMXBean", null);
				this.diagnosticMXBean = ManagementFactory
						.getPlatformMXBean((Class<PlatformManagedObject>) diagnosticMXBeanClass);
				this.dumpHeapMethod = ReflectionUtils.findMethod(diagnosticMXBeanClass, "dumpHeap", String.class,
						Boolean.TYPE);
			} catch (Throwable ex) {
				throw new HeapDumperUnavailableException("Unable to locate HotSpotDiagnosticMXBean", ex);
			}
		}

		@Override
		public void dumpHeap(File file, boolean live) {
			ReflectionUtils.invokeMethod(this.dumpHeapMethod, this.diagnosticMXBean, file.getAbsolutePath(), live);
		}

	}

	/**
	 * Exception to be thrown if the {@link HeapDumper} cannot be created.
	 */
	protected static class HeapDumperUnavailableException extends RuntimeException {

		private static final long serialVersionUID = 1L;

		public HeapDumperUnavailableException(String message, Throwable cause) {
			super(message, cause);
		}

	}

	private static final class TemporaryFileSystemResource extends FileSystemResource {

		private TemporaryFileSystemResource(File file) {
			super(file);
		}

		@Override
		public ReadableByteChannel readableChannel() throws IOException {
			ReadableByteChannel readableChannel = super.readableChannel();
			return new ReadableByteChannel() {

				@Override
				public boolean isOpen() {
					return readableChannel.isOpen();
				}

				@Override
				public void close() throws IOException {
					closeThenDeleteFile(readableChannel);
				}

				@Override
				public int read(ByteBuffer dst) throws IOException {
					return readableChannel.read(dst);
				}

			};
		}

		@Override
		public InputStream getInputStream() throws IOException {
			return new FilterInputStream(super.getInputStream()) {

				@Override
				public void close() throws IOException {
					closeThenDeleteFile(this.in);
				}

			};
		}

		private void closeThenDeleteFile(Closeable closeable) throws IOException {
			try {
				closeable.close();
			} finally {
				deleteFile();
			}
		}

		private void deleteFile() {
			try {
				Files.delete(getFile().toPath());
			} catch (IOException ex) {
				APP_LOGGER.warn("Failed to delete temporary heap dump file '" + getFile() + "'", ex);
			}
		}

		@Override
		public boolean isFile() {
			// Prevent zero-copy so we can delete the file on close
			return false;
		}

	}
}
