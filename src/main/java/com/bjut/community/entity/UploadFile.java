package com.bjut.community.entity;

/**
 * @author: 褚真
 * @date: 2021/8/7
 * @time: 11:53
 * @description:
 */
public class UploadFile {
    private String filename;
    private String url;

    public UploadFile(String filename, String url) {
        this.filename = filename;
        this.url = url;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "UploadFile{" +
                "filename='" + filename + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
