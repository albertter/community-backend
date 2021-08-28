package com.bjut.community.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class DiscussPost implements Serializable {
    private int id;
    private int userId;
    private String title;
    private String content;
    private int type;
    private int status;
    private Date createTime;
    private int commentCount;
    private double score;
}
