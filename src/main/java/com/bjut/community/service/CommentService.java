package com.bjut.community.service;

import com.bjut.community.entity.Comment;

import java.util.List;

/**
 * Created by Chu Zhen on 2021/7/4 004 15:53
 *
 * @author zhen
 */
public interface CommentService {
    public List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit);

    public int findCountByEntity(int entityType, int entityId);

    public Comment findCommentsById(int id);

    public int addComment(Comment comment);
}
