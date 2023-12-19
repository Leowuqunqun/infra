package com.zhenyun.tiangong.mdt.sandbox.file;


import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Data
@Accessors(chain = true)
public class FileInfo implements Serializable {

    /**
     * 文件id
     */
    private String id;

    /**
     * 文件访问地址
     */
    private String url;

    /**
     * 文件大小，单位字节
     */
    private Long size;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 原始文件名
     */
    private String originalFilename;

    /**
     * 基础存储路径
     */
    private String basePath;

    /**
     * 存储路径
     */
    private String path;

    /**
     * 文件扩展名
     */
    private String ext;

    /**
     * MIME 类型
     */
    private String contentType;

    /**
     * 存储平台
     */
    private PlatformEnum platform;

    /**
     * 文件所属对象id
     */
    private String objectId;

    /**
     * 文件所属对象类型
     */
    private String objectType;

    /**
     * 文件元数据
     */
    private Map<String, String> metadata;

    private Object fileAcl;

    /**
     * 创建时间
     */
    private Date createTime;

}
