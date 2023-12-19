package com.zhenyun.tiangong.mdt.sandbox.file;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 文件上传预处理对象
 */
@Getter
@Setter
@Accessors(chain = true)
public class UploadPretreatment {
    private FileStorageService fileStorageService;
    /**
     * 要上传到的平台
     */
    private PlatformEnum platform;
    /**
     * 要上传的文件包装类
     */
    private FileWrapper fileWrapper;

    /**
     * 文件所属对象id
     */
    private String objectId;
    /**
     * 文件所属对象类型
     */
    private String objectType;
    /**
     * 文件存储路径
     */
    private String path = "";

    /**
     * 保存文件名，如果不设置则自动生成
     */
    private String saveFilename;

    /**
     * 文件元数据
     */
    private Map<String, String> metadata;

    /**
     * 设置要上传到的平台
     */
    public UploadPretreatment setPlatform(boolean flag,PlatformEnum platform) {
        if (flag) setPlatform(platform);
        return this;
    }

    /**
     * 设置要上传的文件包装类
     */
    public UploadPretreatment setFileWrapper(boolean flag,FileWrapper fileWrapper) {
        if (flag) setFileWrapper(fileWrapper);
        return this;
    }

    /**
     * 设置文件所属对象id
     *
     * @param objectId 如果不是 String 类型会自动调用 toString() 方法
     */
    public UploadPretreatment setObjectId(boolean flag,Object objectId) {
        if (flag) setObjectId(objectId);
        return this;
    }

    /**
     * 设置文件所属对象id
     *
     * @param objectId 如果不是 String 类型会自动调用 toString() 方法
     */
    public UploadPretreatment setObjectId(Object objectId) {
        this.objectId = objectId == null ? null : objectId.toString();
        return this;
    }

    /**
     * 设置文件所属对象类型
     */
    public UploadPretreatment setObjectType(boolean flag,String objectType) {
        if (flag) setObjectType(objectType);
        return this;
    }

    /**
     * 设置文文件存储路径
     */
    public UploadPretreatment setPath(boolean flag,String path) {
        if (flag) setPath(path);
        return this;
    }

    /**
     * 设置保存文件名，如果不设置则自动生成
     */
    public UploadPretreatment setSaveFilename(boolean flag,String saveFilename) {
        if (flag) setSaveFilename(saveFilename);
        return this;
    }


    /**
     * 获取文件名
     */
    public String getName() {
        return fileWrapper.getName();
    }

    /**
     * 设置文件名
     */
    public UploadPretreatment setName(boolean flag,String name) {
        if (flag) setName(name);
        return this;
    }

    /**
     * 设置文件名
     */
    public UploadPretreatment setName(String name) {
        fileWrapper.setName(name);
        return this;
    }

    /**
     * 获取原始文件名
     */
    public String getOriginalFilename() {
        return fileWrapper.getName();
    }

    /**
     * 设置原始文件名
     */
    public UploadPretreatment setOriginalFilename(boolean flag,String originalFilename) {
        if (flag) setOriginalFilename(originalFilename);
        return this;
    }

    /**
     * 设置原始文件名
     */
    public UploadPretreatment setOriginalFilename(String originalFilename) {
        fileWrapper.setName(originalFilename);
        return this;
    }

    /**
     * 获取文件元数据
     */
    public Map<String, String> getMetadata() {
        if (metadata == null) metadata = new LinkedHashMap<>();
        return metadata;
    }

    /**
     * 设置文件元数据
     */
    public UploadPretreatment putMetadata(boolean flag,String key,String value) {
        if (flag) putMetadata(key,value);
        return this;
    }

    /**
     * 设置文件元数据
     */
    public UploadPretreatment putMetadata(String key,String value) {
        getMetadata().put(key,value);
        return this;
    }

    /**
     * 设置文件元数据
     */
    public UploadPretreatment putMetadataAll(boolean flag,Map<String, String> metadata) {
        if (flag) putMetadataAll(metadata);
        return this;
    }

    /**
     * 设置文件元数据
     */
    public UploadPretreatment putMetadataAll(Map<String, String> metadata) {
        getMetadata().putAll(metadata);
        return this;
    }

    /**
     * 上传文件，成功返回文件信息，失败返回null
     */
    public FileInfo upload() {
        return fileStorageService.upload(this);
    }
}
