package com.bjut.community.entity;

import lombok.Data;

/**
 * @author: 褚真
 * @date: 2021/8/7
 * @time: 11:53
 * @description:
 */
@Data
public class UploadFile {
    private String filename;
    private String url;

    public UploadFile(String filename, String url) {
        this.filename = filename;
        this.url = url;
    }
}
