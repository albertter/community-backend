package com.bjut.community.service.impl;

import com.bjut.community.dao.DiscussPostMapper;
import com.bjut.community.entity.DiscussPost;
import com.bjut.community.service.DiscussPostService;
import com.bjut.community.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
//@CacheConfig(cacheNames = "PostCache")
//@Transactional
public class DiscussPostServiceImpl implements DiscussPostService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private DiscussPostMapper discussPostMapper;
//    @Autowired
//    private RedisTemplate redisTemplate;

    //    @Cacheable(value = "discuss", condition = "#offset<5", keyGenerator = "simpleKeyGenerator")
    @Override
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit, int orderMod) {
        logger.info("进来查库了--------->{}", userId);
        if(offset>10){
            return discussPostMapper.selectDiscussPosts(userId, offset, limit, orderMod);
        }
        return discussPostMapper.selectDiscussPosts(userId, offset, limit, orderMod);

    }

    @Cacheable("DiscussPostRows")
    @Override
    public int findDiscussPostRows(int userId) {
        return discussPostMapper.selectDiscussPostRows(userId);
    }

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
//    private List<DiscussPost> addCache(int userId, int offset, int limit, int orderMod) {
//        List<DiscussPost> posts = discussPostMapper.selectDiscussPosts(userId, offset, limit, orderMod);
//        String redisKey = RedisKeyUtil.getPostCacheKey(userId);
//        redisTemplate.opsForZSet().set(redisKey, posts, 600, TimeUnit.SECONDS);
//        return posts;
//    }
//
//    private void clearCache(int userId) {
//        String redisKey = RedisKeyUtil.getUserKey(userId);
//        redisTemplate.delete(redisKey);
//
//    }
}
