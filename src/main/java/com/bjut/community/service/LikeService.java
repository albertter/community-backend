package com.bjut.community.service;

// redis的key为 entity，value为点赞用户的集合
public interface LikeService {


    public void like(int userId, int entityType, int entityId, int entityUserId);

    // 统计某entity点赞数量
    public long findEntityLikeCount(int entityType, int entityId);

    //查询用户是否对某实体是否点赞
    public int findEntityLikeStatus(int userId, int entityType, int entityId);

    //查询某用户获得的赞的数量
    public int findUserLikeCount(int userId);


}
