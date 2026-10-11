package com.hanxuan.admin.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文件存储配置
 *
 * @author hanxuan
 */
@Data
@Component
@ConfigurationProperties(prefix = "file")
public class FileStorageConfig {

    /**
     * 存储类型：local-本地磁盘
     */
    private String storageType = "local";

    /**
     * 本地存储路径
     */
    private String localPath = "./upload";

    /**
     * 文件访问前缀
     */
    private String urlPrefix = "/file/download";
}
