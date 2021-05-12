package com.bjut.community.util;

public interface CommunityConstant {
    int ACTIVATION_SUCCESS = 0; //激活成功
    int ACTIVATION_REPEAT = 1; //重复激活
    int ACTIVATION_FAILURE = 2;//激活失败
    int DEFAULT_EXPIRED_SUCCESS = 3600 * 12; //默认状态登录凭证超时时间
    int REMEMBER_EXPIRED_SUCCESS = 3600 * 12 * 100; // 记住状态登录凭证超时时间

    int ENTITY_TYPE_POST = 1;
    int ENTITY_TYPE_COMMENT = 2;
    int ENTITY_TYPE_USER = 3;

    String TOPIC_COMMENT = "comment";
    String TOPIC_LIKE = "like";
    String TOPIC_FOLLOW = "follow";

    int SYSTEM_USER_ID = 1;

}
