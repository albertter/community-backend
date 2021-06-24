package com.bjut.community.service;

import com.bjut.community.dao.CommentMapper;
import com.bjut.community.entity.Comment;
import com.bjut.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService implements CommunityConstant {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private DiscussPostService discussPostService;

    public List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentsByEntity(entityType, entityId, offset, limit);
    }

    public int findCountByEntity(int entityType, int entityId) {
        return commentMapper.selectCountByEntity(entityType, entityId);
    }

    public Comment findCommentsById(int id) {
        return commentMapper.selectCommentById(id);
    }

//    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED) // 添加评论事务
    public int addComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        int rows = commentMapper.insertComment(comment);
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(), count);
        }
        return rows;

    }

}
