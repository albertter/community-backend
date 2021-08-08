package com.bjut.community.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author: 褚真
 * @date: 2021/8/3
 * @time: 14:29
 * @description:
 */
@Data
public class Tag {
    private int id;
    private String name;
    private Date createTime;
    private int count;
}
