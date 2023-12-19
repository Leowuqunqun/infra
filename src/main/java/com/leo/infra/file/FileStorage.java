package com.zhenyun.tiangong.mdt.sandbox.file;


import java.io.InputStream;
import java.util.function.Consumer;

/**
 * 文件存储接口，对应各个平台
 */
public interface FileStorage extends AutoCloseable {

    /**
     * 获取平台
     */
    PlatformEnum getPlatform();

    /**
     * 设置平台
     */
    void setPlatform(PlatformEnum platform);

    /**
     * 设置平台
     */
    void setBucketName(String bucketName);

    /**
     * 保存文件
     */
    boolean save(FileInfo fileInfo,UploadPretreatment pre);


    /**
     * 下载文件
     */
    void download(FileInfo fileInfo,Consumer<InputStream> consumer);


    /**
     * 释放相关资源
     */
    default void close() {
    }

}
