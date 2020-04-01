package com.swak.vertx.transport.multipart;

import java.io.File;

/**
 * 通过 text/plain; charset=UTF-8 输出文件
 *
 * @author: lifeng
 * @date: 2020/3/29 21:13
 */
public class PlainFile {

    private File file;
    private Runnable accept;

    public Runnable accept() {
        return accept;
    }

    public PlainFile accept(Runnable accept) {
        this.accept = accept;
        return this;
    }

    public File file() {
        return file;
    }

    public PlainFile file(File file) {
        this.file = file;
        return this;
    }

    public static PlainFile of(File file) {
        return new PlainFile().file(file);
    }
}
