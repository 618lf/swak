package com.swak.utils;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.List;

import com.swak.Constants;

/**
 * 
 * 暂时 简单的使用 bio 中的文件读取方式， 主要添加nio 的文件操作方式
 * 
 * @see org.apache.commons.io.IOUtils
 * @author lifeng
 *
 */
public class IOUtils {

	/**
	 * @param closeable
	 *            the object to close, may be null or already closed
	 * @throws IOException
	 * @since 2.0
	 */
	public static void close(Closeable closeable) throws IOException {
		if (closeable != null) {
			closeable.close();
		}
	}

	/**
	 * Unconditionally close a <code>Closeable</code>.
	 * <p>
	 * Equivalent to {@link Closeable#close()}, except any exceptions will be
	 * ignored. This is typically used in finally blocks.
	 * <p>
	 * Example code:
	 * 
	 * <pre>
	 * Closeable closeable = null;
	 * try {
	 * 	closeable = new FileReader("foo.txt");
	 * 	// process closeable
	 * 	closeable.close();
	 * } catch (Exception e) {
	 * 	// error handling
	 * } finally {
	 * 	IOUtils.closeQuietly(closeable);
	 * }
	 * </pre>
	 *
	 * @param closeable
	 *            the object to close, may be null or already closed
	 * @since 2.0
	 */
	public static void closeQuietly(Closeable closeable) {
		try {
			if (closeable != null) {
				closeable.close();
			}
		} catch (IOException ioe) {
			// ignore
		}
	}

	/**
	 * Get the contents of an <code>InputStream</code> as a list of Strings, one
	 * entry per line, using the specified character encoding.
	 * <p>
	 * Character encoding names can be found at
	 * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
	 * <p>
	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedInputStream</code>.
	 *
	 * @param input
	 *            the <code>InputStream</code> to read from, not null
	 * @param encoding
	 *            the encoding to use, null means platform default
	 * @return the list of Strings, never null
	 * @throws NullPointerException
	 *             if the input is null
	 * @throws IOException
	 *             if an I/O error occurs
	 * @throws UnsupportedCharsetException
	 *             thrown instead of {@link UnsupportedEncodingException} in version
	 *             2.2 if the encoding is not supported.
	 * @since 1.1
	 */
	public static List<String> readLines(InputStream input) throws IOException {
		return readLines(input, StandardCharsets.UTF_8);
	}

	/**
	 * Get the contents of an <code>InputStream</code> as a list of Strings, one
	 * entry per line, using the specified character encoding.
	 * <p>
	 * Character encoding names can be found at
	 * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
	 * <p>
	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedInputStream</code>.
	 *
	 * @param input
	 *            the <code>InputStream</code> to read from, not null
	 * @param encoding
	 *            the encoding to use, null means platform default
	 * @return the list of Strings, never null
	 * @throws NullPointerException
	 *             if the input is null
	 * @throws IOException
	 *             if an I/O error occurs
	 * @throws UnsupportedCharsetException
	 *             thrown instead of {@link UnsupportedEncodingException} in version
	 *             2.2 if the encoding is not supported.
	 * @since 1.1
	 */
	public static List<String> readLines(InputStream input, String encoding) throws IOException {
		Charset charset = encoding == null ? Constants.DEFAULT_ENCODING : Charset.forName(encoding);
		return readLines(input, charset);
	}

	/**
	 * Get the contents of an <code>InputStream</code> as a list of Strings, one
	 * entry per line, using the specified character encoding.
	 * <p>
	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedInputStream</code>.
	 *
	 * @param input
	 *            the <code>InputStream</code> to read from, not null
	 * @param encoding
	 *            the encoding to use, null means platform default
	 * @return the list of Strings, never null
	 * @throws NullPointerException
	 *             if the input is null
	 * @throws IOException
	 *             if an I/O error occurs
	 * @since 2.3
	 */
	public static List<String> readLines(InputStream input, Charset encoding) throws IOException {
		Charset charset = encoding == null ? Constants.DEFAULT_ENCODING : encoding;
		InputStreamReader reader = new InputStreamReader(input, charset);
		return readLines(reader);
	}

	/**
	 * Get the contents of a <code>Reader</code> as a list of Strings, one entry per
	 * line.
	 * <p>
	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedReader</code>.
	 *
	 * @param input
	 *            the <code>Reader</code> to read from, not null
	 * @return the list of Strings, never null
	 * @throws NullPointerException
	 *             if the input is null
	 * @throws IOException
	 *             if an I/O error occurs
	 * @since 1.1
	 */
	public static List<String> readLines(Reader input) throws IOException {
		BufferedReader reader = toBufferedReader(input);
		List<String> list = new ArrayList<String>();
		String line = reader.readLine();
		while (line != null) {
			list.add(line);
			line = reader.readLine();
		}
		return list;
	}

	/**
	 * Returns the given reader if it is a {@link BufferedReader}, otherwise creates
	 * a toBufferedReader for the given reader.
	 * 
	 * @param reader
	 *            the reader to wrap or return
	 * @return the given reader or a new {@link BufferedReader} for the given reader
	 * @since 2.2
	 */
	public static BufferedReader toBufferedReader(Reader reader) {
		return reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
	}

	/**
	 * 关闭文件通道
	 * 
	 * @param channel
	 */
	public static void closeQuietly(FileChannel channel) {
		if (channel != null) {
			try {
				channel.close();
			} catch (IOException e) {
			}
		}
	}
}
