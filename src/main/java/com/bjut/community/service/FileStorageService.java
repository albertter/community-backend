package com.bjut.community.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * @author: 褚真
 * @date: 2021/8/7
 * @time: 11:32
 * @description: 文件上传服务
 */
public interface FileStorageService {

    void init();

    void save(MultipartFile multipartFile);

    Resource load(String filename);

    Stream<Path> load();

    void clear();

}
