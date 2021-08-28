package com.bjut.community.entity;

import lombok.Data;

/**
 * @author: 褚真
 * @date: 2021/8/3
 * @time: 15:46
 * @description:
 */
@Data
public class Choice {
    private int id;
    private String text;
    private int pollId;
}
