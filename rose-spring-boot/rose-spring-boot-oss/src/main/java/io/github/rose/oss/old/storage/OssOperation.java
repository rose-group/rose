/*
 * Copyright © 2025 rose-group.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.rose.oss.old.storage;

import io.github.rose.oss.old.storage.domain.DownloadResponse;
import io.github.rose.oss.old.storage.domain.StorageItem;
import io.github.rose.oss.old.storage.domain.StorageRequest;
import io.github.rose.oss.old.storage.domain.StorageResponse;
import io.github.rose.oss.old.storage.exception.StorageException;
import io.github.rose.oss.old.storage.properties.BaseOssProperties;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * 文件存储
 *
 * @author Levin
 */
public interface OssOperation {

    /* OSS 云存储的 */
    /**
     * 配置文件前缀
     */
    String OSS_CONFIG_PREFIX_ALIYUN = "support.oss.cloud.aliyun";

    String OSS_CONFIG_PREFIX_MINIO = "support.oss.cloud.minio";

    String OSS_CONFIG_PREFIX_QINIU = "support.oss.cloud.qiniu";

    String OSS_CONFIG_PREFIX_TENCENT = "support.oss.cloud.tencent";

    String MINIO_OSS_OPERATION = "minioOssOperation";

    String ALI_YUN_OSS_OPERATION = "aliYunOssOperation";

    String QI_NIU_OSS_OPERATION = "qiNiuOssOperation";

    String TENCENT_OSS_OPERATION = "tencentOssOperation";

    /**
     * 获取临时token
     *
     * @param originName originName
     * @param random     random
     * @return token
     */
    default String token(String originName, boolean random) {
        return null;
    }

    /**
     * 获取临时token
     *
     * @param bucket     bucket
     * @param originName originName
     * @param random     random
     * @return token
     */
    default String token(String bucket, String originName, boolean random) {

        return null;
    }

    /**
     * 文件下载（流式下载）
     *
     * @param fileName 文件名
     * @return BufferedReader BufferedReader
     */
    DownloadResponse download(String fileName);

    /**
     * 文件下载（流式下载）
     *
     * @param bucketName 存储桶名
     * @param fileName   文件名
     * @return BufferedReader BufferedReader
     */
    DownloadResponse download(String bucketName, String fileName);

    /**
     * 文件下载（文件下载到本地）
     *
     * @param bucketName 存储桶名
     * @param fileName   文件名
     * @param file       保存的本地文件路径
     */
    void download(String bucketName, String fileName, File file);

    /**
     * 文件下载（文件下载到本地）
     *
     * @param fileName 文件名
     * @param file     保存的本地文件路径
     */
    void download(String fileName, File file);

    /**
     * 文件列表
     *
     * @return 文件内容
     */
    List<StorageItem> list();

    /**
     * 重命名
     *
     * @param oldName 原始名称
     * @param newName 新名称
     */
    void rename(String oldName, String newName);

    /**
     * 重命名
     *
     * @param bucketName 存储桶名
     * @param oldName    原始名称
     * @param newName    新名称
     */
    void rename(String bucketName, String oldName, String newName);

    /**
     * 上传文件到指定的 bucket
     *
     * @param fileName 文件名字
     * @param content  文件内容
     * @return StorageResponse
     */
    StorageResponse upload(String fileName, byte[] content);

    /**
     * 上传文件到指定的 bucket
     *
     * @param bucketName 存储桶名
     * @param fileName   文件名字
     * @param content    文件内容
     * @return StorageResponse
     */
    StorageResponse upload(String bucketName, String fileName, InputStream content);

    /**
     * 上传文件到指定的 bucket
     *
     * @param bucketName 存储桶名
     * @param fileName   文件名字
     * @param content    文件内容
     * @return StorageResponse
     */
    StorageResponse upload(String bucketName, String fileName, byte[] content);

    /**
     * 简化上传复杂度
     *
     * @param request request
     * @return 上传结果
     */
    StorageResponse upload(StorageRequest request);

    /**
     * 删除文件
     *
     * @param fileName 文件名
     */
    void remove(String fileName);

    /**
     * 删除文件
     *
     * @param bucketName 存储桶名
     * @param fileName   文件名
     */
    void remove(String bucketName, String fileName);

    /**
     * 删除文件
     *
     * @param bucketName 存储桶名
     * @param path       文件路径
     */
    void remove(String bucketName, Path path);

    /**
     * 获取目标名字
     *
     * @param request request
     * @return 目标名称
     * @throws RuntimeException RuntimeException
     */
    default String getTargetName(StorageRequest request) {
        if (request == null) {
            throw new RuntimeException("request 不能为空");
        }
        if (StringUtils.isBlank(request.getOriginName())) {
            throw new RuntimeException("originName 不能为空");
        }
        final StorageRequest.PrefixRule rule = request.getRule();
        if (rule == null) {
            return FileUtils.targetName(request.isRandomName(), request.getPrefix(), request.getOriginName());
        }
        String prefix;
        switch (rule) {
            case now_date_mouth:
                prefix = LocalDateTime.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyyMM", Locale.getDefault()));
            case now_date_mouth_day:
                prefix = LocalDateTime.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyyMMdd", Locale.getDefault()));
            case none:
                prefix = request.getPrefix();
            default:
                prefix = request.getPrefix();
        }
        return FileUtils.targetName(request.isRandomName(), prefix, request.getOriginName());
    }

    /**
     * 上传失败异常
     *
     * @param type    type
     * @param message message
     * @return StorageException
     * @see StorageException
     */
    default StorageException uploadError(BaseOssProperties.StorageType type, String message) {
        return new StorageException(type, message);
    }

    /**
     * 上传失败异常
     *
     * @param type type
     * @param e    异常
     * @return StorageException
     * @see StorageException
     */
    default StorageException uploadError(BaseOssProperties.StorageType type, Exception e) {
        return new StorageException(type, "文件上传失败," + e.getMessage());
    }

    /**
     * 下载失败异常
     *
     * @param type type
     * @param e    异常
     * @return StorageException
     * @see StorageException
     */
    default StorageException downloadError(BaseOssProperties.StorageType type, Exception e) {
        return new StorageException(type, "文件下载失败," + e.getMessage());
    }
}
