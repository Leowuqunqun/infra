package com.zhenyun.tiangong.mdt.sandbox.file;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * 用来处理文件存储，对接多个平台
 */
@Slf4j
@Getter
@Setter
public class FileStorageService {

    private FileStorageService self;
    private CopyOnWriteArrayList<FileStorage> fileStorageList;
    private PlatformEnum defaultPlatform;


    /**
     * 获取默认的存储平台
     */
    public <T extends FileStorage> T getFileStorage() {
        return self.getFileStorage(defaultPlatform);
    }

    /**
     * 获取对应的存储平台
     */
    @SuppressWarnings("unchecked")
    public <T extends FileStorage> T getFileStorage(PlatformEnum platform) {
        for (FileStorage fileStorage : fileStorageList) {
            if (fileStorage.getPlatform().equals(platform)) {
                return (T)fileStorage;
            }
        }
        return null;
    }

    /**
     * 上传文件，成功返回文件信息，失败返回 null
     */
    public FileInfo upload(UploadPretreatment pre) {
        FileWrapper file = pre.getFileWrapper();
        if (file == null) throw new FileStorageRuntimeException("文件不允许为 null ！");
        if (pre.getPlatform() == null) throw new FileStorageRuntimeException("platform 不允许为 null ！");

        FileInfo fileInfo = new FileInfo();
        fileInfo.setCreateTime(new Date());
        fileInfo.setSize(file.getSize());
        fileInfo.setOriginalFilename(file.getName());
        fileInfo.setExt(FilenameUtils.getExtension(file.getName()));
        fileInfo.setObjectId(pre.getObjectId());
        fileInfo.setObjectType(pre.getObjectType());
        fileInfo.setPath(pre.getPath());
        fileInfo.setPlatform(pre.getPlatform());
        fileInfo.setMetadata(pre.getMetadata());
        if (StringUtils.isNotBlank(pre.getSaveFilename())) {
            fileInfo.setFilename(pre.getSaveFilename());
        } else {
            throw new FileStorageRuntimeException("文件名不能为空！");
        }
        FileStorage fileStorage = self.getFileStorage(pre.getPlatform());
        if (fileStorage == null) throw new FileStorageRuntimeException("没有找到对应的存储平台！");
        fileStorage.save(fileInfo, pre);
        return null;
    }


    /**
     * 获取文件下载器
     */
    public InputStream download(String url) {
        return self.download(getFileInfoByUrl(url));
    }



    public void destroy() {
        for (FileStorage fileStorage : fileStorageList) {
            try {
                fileStorage.close();
                log.info("销毁存储平台 {} 成功", fileStorage.getPlatform());
            } catch (Exception e) {
                log.error("销毁存储平台 {} 失败，{}", fileStorage.getPlatform(), e.getMessage(), e);
            }
        }
    }
}
