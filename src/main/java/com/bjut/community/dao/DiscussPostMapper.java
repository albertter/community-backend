package com.bjut.community.dao;

import com.bjut.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    // @Param用于给参数加别名
    int selectDiscussPostRows(@Param("userId") int userId);
}
