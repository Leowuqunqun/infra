package com.zhenyun.tiangong.mdt.sandbox.file;

import com.obs.services.ObsClient;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Setter
@Accessors(chain = true)
public class FileStorageServiceBuilder {
    /**
     * 配置参数
     */
    private FileStorageProperties properties;

    /**
     * Client 工厂
     */
    private List<List<FileStorageClientFactory<?>>> clientFactoryList = new ArrayList<>();
    /**
     * 存储平台
     */
    private List<FileStorage> fileStorageList = new ArrayList<>();


    public FileStorageServiceBuilder(FileStorageProperties properties) {
        this.properties = properties;
    }


    /**
     * 添加 Client 工厂
     */
    public FileStorageServiceBuilder addFileStorageClientFactory(List<FileStorageClientFactory<?>> list) {
        clientFactoryList.add(list);
        return this;
    }

    /**
     * 添加 Client 工厂
     */
    public FileStorageServiceBuilder addFileStorageClientFactory(FileStorageClientFactory<?> factory) {
        clientFactoryList.add(Collections.singletonList(factory));
        return this;
    }

    /**
     * 添加存储平台
     */
    public FileStorageServiceBuilder addFileStorage(List<? extends FileStorage> storageList) {
        fileStorageList.addAll(storageList);
        return this;
    }

    /**
     * 添加存储平台
     */
    public FileStorageServiceBuilder addFileStorage(FileStorage storage) {
        fileStorageList.add(storage);
        return this;
    }

    /**
     * 创建
     */
    public FileStorageService build() {
        if (properties == null) throw new FileStorageRuntimeException("properties 不能为 null");

        //初始化各个存储平台
        fileStorageList.addAll(buildHuaweiObsFileStorage(properties.getHuaweiObs(),clientFactoryList));

        //本体
        FileStorageService service = new FileStorageService();
        service.setSelf(service);
        service.setFileStorageList(new CopyOnWriteArrayList<>(fileStorageList));
        service.setDefaultPlatform(properties.getDefaultPlatform());

        return service;
    }

    /**
     * 创建一个 FileStorageService 的构造器
     */
    public static FileStorageServiceBuilder create(FileStorageProperties properties) {
        return new FileStorageServiceBuilder(properties);
    }

    /**
     * 根据配置文件创建华为云 OBS 存储平台
     */
    public static List<HuaweiObsFileStorage> buildHuaweiObsFileStorage(List<? extends FileStorageProperties.HuaweiObsConfig> list, List<List<FileStorageClientFactory<?>>> clientFactoryList) {
        if (CollectionUtils.isEmpty(list)) return Collections.emptyList();
        buildFileStorageDetect(list,"华为云 OBS ","com.obs.services.ObsClient");
        return list.stream().map(config -> {
            log.info("加载华为云 OBS 存储平台：{}",config.getPlatform());
            FileStorageClientFactory<ObsClient> clientFactory = getFactory(config.getPlatform(),clientFactoryList,() -> new HuaweiObsFileStorageClientFactory(config));
            return new HuaweiObsFileStorage(config,clientFactory);
        }).collect(Collectors.toList());
    }



    /**
     * 获取或创建指定存储平台的 Client 工厂对象
     */
    public static <Client> FileStorageClientFactory<Client> getFactory(String platform,List<List<FileStorageClientFactory<?>>> list,Supplier<FileStorageClientFactory<Client>> defaultSupplier) {
        if (list != null) {
            for (List<FileStorageClientFactory<?>> factoryList : list) {
                for (FileStorageClientFactory<?> factory : factoryList) {
                    if (Objects.equals(platform,factory.getPlatform())) {
                        try {
                            return (FileStorageClientFactory<Client>)factory;
                        } catch (Exception e) {
                            throw new FileStorageRuntimeException("获取 FileStorageClientFactory 失败，类型不匹配，platform：" + platform,e);
                        }
                    }
                }
            }
        }
        return defaultSupplier.get();
    }

    /**
     * 判断是否没有引入指定 Class
     */
    public static boolean doesNotExistClass(String name) {
        try {
            Class.forName(name);
            return false;
        } catch (ClassNotFoundException e) {
            return true;
        }
    }

    /**
     * 创建存储平台时的依赖检查
     */
    public static void buildFileStorageDetect(List<?> list,String platformName,String... classNames) {
        if (CollectionUtils.isEmpty(list)) return;
        for (String className : classNames) {
            if (doesNotExistClass(className)) {
                throw new FileStorageRuntimeException("检测到" + platformName + "配置，但是没有找到对应的依赖类：" + className + "，所以无法加载此存储平台");
            }
        }
    }

}
