package com.bjut.community.service.impl;

import com.bjut.community.dao.DiscussPostMapper;
import com.bjut.community.entity.DiscussPost;
import com.bjut.community.service.DiscussPostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
//@CacheConfig(cacheNames = "PostCache")
//@Transactional
public class DiscussPostServiceImpl implements DiscussPostService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private DiscussPostMapper discussPostMapper;
//    @Autowired
//    private RedisTemplate redisTemplate;

    @Cacheable(value = "DiscussPosts", condition = "#offset<5")
    @Override
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit, int orderMod) {
        return discussPostMapper.selectDiscussPosts(userId, offset, limit, orderMod);
    }

    @Cacheable("DiscussPostRows")
    @Override
    public int findDiscussPostRows(int userId) {
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    //    @CachePut(key = "#discussPost.id")
    @CachePut(cacheNames = "addposts", key = "#discussPost.id")
    @Override
    public int addDiscussPost(DiscussPost discussPost) {
        if (discussPost == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        return discussPostMapper.insertDiscussPost(discussPost);

    }

    @Cacheable("DiscussPostById")
    @Override
    public DiscussPost findDiscussPostById(int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }

    @Override
    public int updateCommentCount(int id, int commentCount) {

        return discussPostMapper.updateCommentCount(id, commentCount);

    }

    @Override
    public int updateType(int id, int type) {
        return discussPostMapper.updateType(id, type);
    }

    @Override
    public int updateStatus(int id, int status) {
        return discussPostMapper.updateStatus(id, status);
    }

    @Override
    public int updateScore(int id, double score) {
        return discussPostMapper.updateScore(id, score);
    }

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
