package com.bjut.community.config;

import com.bjut.community.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

/**
 * @author: 褚真
 * @date: 2021/8/7
 * @time: 12:07
 * @description:
 */
@Service
public class FileUploadConfiguration implements CommandLineRunner {
    @Autowired
    private FileStorageService fileStorageService;

    @Override
    public void run(String... args) throws Exception {
        fileStorageService.clear();
        fileStorageService.init();
    }
}
