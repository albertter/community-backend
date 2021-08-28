package com.bjut.community.service;

import com.bjut.community.entity.DiscussPost;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

public interface DiscussPostService {
    /**
     * 分页查询帖子列表
     *
     * @param userId 用户ID
     * @param offset 起始页
     * @param limit  总页数
     * @return 帖子列表
     */
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit, int orderMod);

    //    @Cacheable
    public int findDiscussPostRows(int userId);

    //    @CachePut(key = "#discussPost.id")
    public int addDiscussPost(DiscussPost discussPost);

    //    @Cacheable
    public DiscussPost findDiscussPostById(int id);

    public int updateCommentCount(int id, int commentCount);

    public int updateType(int id, int type);

    public int updateStatus(int id, int status);

    public int updateScore(int id, double score);

//    private DiscussPost getCache(int postId) {
//        String redisKey = RedisKeyUtil.getPostKey(postId);
//        return (DiscussPost) redisTemplate.opsForValue().get(redisKey);
//    }
//
//    private List<DiscussPost> initCache(int userId, int offset, int limit) {
//        List<DiscussPost> posts = discussPostMapper.selectDiscussPosts(userId, offset, limit);
//        String redisKey = RedisKeyUtil.getUserKey(userId);
//        redisTemplate.opsForValue().set(redisKey, posts, 3600, TimeUnit.SECONDS);
//        return posts;
//    }
//
//    private void clearCache(int userId) {
//        String redisKey = RedisKeyUtil.getUserKey(userId);
//        redisTemplate.delete(redisKey);
//
//    }
}
