package com.bjut.community.service;

import com.bjut.community.dao.DiscussPostMapper;
import com.bjut.community.entity.DiscussPost;
import com.bjut.community.entity.User;
import com.bjut.community.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@CacheConfig(cacheNames = "PostCache")
public class DiscussPostService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Cacheable
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit) {
        return discussPostMapper.selectDiscussPosts(userId, offset, limit);
    }

    @Cacheable
    public int findDiscussPostRows(int userID) {
        return discussPostMapper.selectDiscussPostRows(userID);
    }

    @CachePut(key = "#discussPost.id")
    public int addDiscussPost(DiscussPost discussPost) {
        if (discussPost == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        return discussPostMapper.insertDiscussPost(discussPost);

    }

    @Cacheable
    public DiscussPost findDiscussPostById(int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }

    public int updateCommentCount(int id, int commentCount) {

        return discussPostMapper.updateCommentCount(id, commentCount);

    }

    public int updateType(int id, int type) {
        return discussPostMapper.updateType(id, type);
    }

    public int updateStatus(int id, int status) {
        return discussPostMapper.updateStatus(id, status);
    }

    public int updateScore(int id, double score) {
        return discussPostMapper.updateScore(id, score);
    }

    private DiscussPost getCache(int postId) {
        String redisKey = RedisKeyUtil.getPostKey(postId);
        return (DiscussPost) redisTemplate.opsForValue().get(redisKey);
    }

    private List<DiscussPost> initCache(int userId, int offset, int limit) {
        List<DiscussPost> posts = discussPostMapper.selectDiscussPosts(userId, offset, limit);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey, posts, 3600, TimeUnit.SECONDS);
        return posts;
    }

    private void clearCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);

    }
}
