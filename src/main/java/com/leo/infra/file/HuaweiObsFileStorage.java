package com.zhenyun.tiangong.mdt.sandbox.file;

import com.obs.services.ObsClient;
import com.obs.services.internal.ObsConvertor;
import com.obs.services.model.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

/**
 * 华为云 OBS 存储
 */
@Getter
@Setter
@NoArgsConstructor
public class HuaweiObsFileStorage implements FileStorage {
    private String platform;
    private String bucketName;
    private String domain;
    private String basePath;
    private String defaultAcl;
    private FileStorageClientFactory<ObsClient> clientFactory;

    public HuaweiObsFileStorage(FileStorageProperties.HuaweiObsConfig config, FileStorageClientFactory<ObsClient> clientFactory) {
        platform = config.getPlatform();
        bucketName = config.getBucketName();
        domain = config.getDomain();
        basePath = config.getBasePath();
        defaultAcl = config.getDefaultAcl();
        this.clientFactory = clientFactory;
    }

    public ObsClient getClient() {
        return clientFactory.getClient();
    }


    @Override
    public void close() {
        clientFactory.close();
    }

    public String getFileKey(FileInfo fileInfo) {
        return fileInfo.getBasePath() + fileInfo.getPath() + fileInfo.getFilename();
    }


    @Override
    public void setPlatform(PlatformEnum platform) {
        this.platform = platform.name();
    }

    @Override
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    @Override
    public boolean save(FileInfo fileInfo, UploadPretreatment pre) {
        fileInfo.setBasePath(basePath);
        String newFileKey = getFileKey(fileInfo);
        fileInfo.setUrl(domain + newFileKey);
        AccessControlList fileAcl = getAcl(fileInfo.getFileAcl());
        ObjectMetadata metadata = getObjectMetadata(fileInfo);
        ObsClient client = getClient();
        String uploadId = null;
        try (InputStream in = pre.getFileWrapper().getInputStream()) {
            //todo size>5g (分片上传
            PutObjectRequest request = new PutObjectRequest(bucketName, newFileKey, in);
            request.setMetadata(metadata);
            request.setAcl(fileAcl);
            client.putObject(request);
            return true;
        } catch (IOException e) {
            client.deleteObject(bucketName, newFileKey);
            throw new FileStorageRuntimeException("文件上传失败！platform：" + platform + "，filename：" + fileInfo.getOriginalFilename(), e);
        }
    }

    /**
     * 获取文件的访问控制列表
     */
    public AccessControlList getAcl(Object acl) {
        if (acl instanceof AccessControlList) {
            return (AccessControlList) acl;
        } else if (acl instanceof String || acl == null) {
            String sAcl = (String) acl;
            if (StringUtils.isEmpty(sAcl)) sAcl = defaultAcl;
            if (sAcl == null) return null;
            return ObsConvertor.getInstance().transCannedAcl(sAcl);
        } else {
            throw new FileStorageRuntimeException("不支持的ACL：" + acl);
        }
    }

    /**
     * 获取对象的元数据
     */
    public ObjectMetadata getObjectMetadata(FileInfo fileInfo) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(fileInfo.getSize());
        metadata.setContentType(fileInfo.getContentType());
        BeanUtils.copyProperties(fileInfo.getMetadata(), metadata);
        return metadata;
    }

    @Override
    public void download(FileInfo fileInfo, Consumer<InputStream> consumer) {
        ObsObject object = getClient().getObject(bucketName, getFileKey(fileInfo));
        try (InputStream in = object.getObjectContent()) {
            consumer.accept(in);
        } catch (IOException e) {
            throw new FileStorageRuntimeException("文件下载失败！fileInfo：" + fileInfo, e);
        }
    }

}
