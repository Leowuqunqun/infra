package com.zhenyun.tiangong.mdt.sandbox.file;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
public class FileStorageProperties {

    /**
     * 默认存储平台
     */
    private PlatformEnum defaultPlatform = PlatformEnum.huawei_obs;


    /**
     * 华为云 OBS
     */
    private List<? extends HuaweiObsConfig> huaweiObs = new ArrayList<>();


    /**
     * 基本的存储平台配置
     */
    @Data
    public static class BaseConfig {
        /**
         * 存储平台
         */
        private String platform = "";
    }

    /**
     * 华为云 OBS
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class HuaweiObsConfig extends BaseConfig {
        private String accessKey;
        private String secretKey;
        private String endPoint;
        private String bucketName;
        /**
         * 访问域名
         */
        private String domain = "";
        /**
         * 基础路径
         */
        private String basePath = "";

        private String defaultAcl;
        /**
         * 自动分片上传阈值，超过此大小则使用分片上传，默认 1024MB
         */
        private int multipartThreshold = 1024 * 1024 * 1024;

        /**
         * 其它自定义配置
         */
        private Map<String,Object> attr = new LinkedHashMap<>();
    }

}
