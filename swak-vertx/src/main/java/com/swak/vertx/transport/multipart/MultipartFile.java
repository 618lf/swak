package com.swak.vertx.transport.multipart;

import java.io.File;

/**
 * 上传文件
 *
 * @author: lifeng
 * @date: 2020/3/29 21:13
 */
public class MultipartFile {

    private String name;
    private String fileName;
    private byte[] data;
    private File file;
    private Runnable accept;

    public MultipartFile() {

    }

    public MultipartFile(String name, String fileName, File file) {
        this.name = name;
        this.fileName = fileName;
        this.file = file;
    }

    public MultipartFile(String name, String fileName, byte[] data) {
        this.name = name;
        this.fileName = fileName;
        this.data = data;
    }

    public Runnable accept() {
        return accept;
    }

    public MultipartFile accept(Runnable accept) {
        this.accept = accept;
        return this;
    }

    public String name() {
        return name;
    }

    public MultipartFile name(String name) {
        this.name = name;
        return this;
    }

    public String fileName() {
        return fileName;
    }

    public MultipartFile fileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public byte[] data() {
        return data;
    }

    public MultipartFile data(byte[] data) {
        this.data = data;
        return this;
    }

    public File file() {
        return file;
    }

    public MultipartFile file(File file) {
        this.file = file;
        return this;
    }

    public static MultipartFile of(File file) {
        return new MultipartFile().file(file);
    }

    public static MultipartFile of(byte[] data) {
        return new MultipartFile().data(data);
    }
}