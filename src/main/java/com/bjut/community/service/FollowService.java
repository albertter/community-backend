package com.bjut.community.service;

import com.bjut.community.entity.User;
import com.bjut.community.service.impl.UserServiceImpl;
import com.bjut.community.util.CommunityConstant;
import com.bjut.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

// redis的key为 entity，value为点赞用户的集合
public interface FollowService{


    public void follow(int userId, int entityType, int entityId);

    public void unfollow(int userId, int entityType, int entityId);

    //
    public long findFolloweeCount(int userId, int entityType);

    //
    public long findFollowerCount(int entityType, int entityId);

    public boolean hasFollowed(int userId, int entityType, int entityId);

    // 查询某用户关注的人
    public List<Map<String, Object>> findFollowees(int userId, int offset, int limit);

    // 查询某用户的粉丝
    public List<Map<String, Object>> findFollowers(int userId, int offset, int limit);
}