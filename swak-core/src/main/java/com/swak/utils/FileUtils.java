package com.swak.utils;

import com.swak.App;
import org.springframework.core.io.Resource;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.UUID;

/**
 * 基于 NIO 的 高性能文件操作
 *
 * @author: lifeng
 * @date: 2020/3/29 14:02
 */
public class FileUtils {

    /**
     * 获取资源文件
     *
     * @param localtion 资源文件
     * @return 流
     */
    public static InputStream resource(String localtion) {
        try {
            Resource resource = App.resource(localtion);
            if (resource.isFile()) {
                return new FileInputStream(resource.getFile());
            }
            return resource.getInputStream();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 返回一个指定名称的临时文件
     *
     * @param name 名称
     * @return 临时文件
     */
    public static File tempFile(String name) {
        return new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString() + "." + name);
    }

    /**
     * 将 字节写入文件
     *
     * @param file  文件
     * @param datas 数据
     */
    public static void write(File file, byte[] datas) {
        FileChannel channel = null;
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            channel = out.getChannel();
            channel.write(ByteBuffer.wrap(datas));
        } catch (Exception ignored) {
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(channel);
        }
    }

    /**
     * 打开这个文件
     *
     * @param file 文件
     * @return 数据
     */
    public static byte[] read(File file) {
        FileInputStream fis = null;
        ByteArrayOutputStream out = null;
        try {
            fis = new FileInputStream(file);
            out = new ByteArrayOutputStream();
            byte[] buffer = new byte[512];
            int n;
            while ((n = fis.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }
            return out.toByteArray();
        } catch (Exception e) {
            return null;
        } finally {
            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(out);
        }
    }

    /**
     * 打开这个文件
     *
     * @param file 文件
     * @return 流
     */
    public static FileInputStream in(File file) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (Exception ignored) {
        }
        return fis;
    }

    /**
     * 打开这个文件
     *
     * @param file 文件
     * @return 流
     */
    public static FileOutputStream out(File file) {
        FileOutputStream fis = null;
        try {
            fis = new FileOutputStream(file);
        } catch (Exception ignored) {
        }
        return fis;
    }

    /**
     * 得到文件的名称，不包括扩展名
     *
     * @param fileUrl 文件
     * @return 名称
     */
    public static String getFileName(String fileUrl) {
        return StringUtils.removeStart(StringUtils.substringAfterLast(fileUrl, "/"), ".");
    }

    /**
     * 得到文件的扩展名, 大写
     *
     * @param fileUrl 文件
     * @return 扩展名
     */
    public static String getFileSuffix(String fileUrl) {
        return StringUtils.lowerCase(StringUtils.substringAfterLast(fileUrl, "."));
    }

    /**
     * 异步写文件, 执行成功之后会调用 completed (有问题)
     *
     * @param src       -- Channels.newChannel(getInputStream());
     * @param dist      -- AsynchronousFileChannel.open(out.toPath(),
     *                  StandardOpenOption.WRITE);
     * @param bytebuf   -- ByteBuffer.allocate(1024);
     * @param completed 回调
     * @throws IOException 异常
     */
    public static void asyncWrite(ReadableByteChannel src, AsynchronousFileChannel dist, ByteBuffer bytebuf,
                                  Runnable completed) throws IOException {
        bytebuf.compact();
        int read = src.read(bytebuf);
        if (read >= 0 || bytebuf.hasRemaining()) {
            bytebuf.flip();
            dist.write(bytebuf, 0, null, new CompletionHandler<Integer, Void>() {
                @Override
                public void completed(Integer result, Void attachment) {
					doContinue();
                }

                @Override
                public void failed(Throwable exc, Void attachment) {
					doContinue();
                }

                private void doContinue() {
                    try {
                        asyncWrite(src, dist, bytebuf, completed);
                    } catch (IOException ignored) {
                    }
                }
            });
        } else if (completed != null) {
            completed.run();
        }
    }

}