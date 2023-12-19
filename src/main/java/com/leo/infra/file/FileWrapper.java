package com.zhenyun.tiangong.mdt.sandbox.file;


import java.io.IOException;
import java.io.InputStream;

/**
 * 文件包装接口
 */
public interface FileWrapper {
    /**
     * 获取文件名称
     */
    String getName();

    /**
     * 设置文件名称
     */
    void setName(String name);

    /**
     * 获取文件的 InputStream
     */
    InputStream getInputStream() throws IOException;


    /**
     * 获取文件大小
     */
    Long getSize();




}
