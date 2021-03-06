package com.bjut.community.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author chuzhen
 */
@Data
public class Message implements Serializable {
    private int id;
    private int fromId;
    private int toId;
    private String conversationId;
    private String content;
    private int status;
    private Date createTime;
}
