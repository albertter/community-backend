package com.bjut.community.entity;

import lombok.Data;

/**
 * @author: 褚真
 * @date: 2021/8/3
 * @time: 15:51
 * @description:
 */
@Data
public class Vote {
    private int id;
    private User user;
    private Poll poll;
    private Choice choice;
}
