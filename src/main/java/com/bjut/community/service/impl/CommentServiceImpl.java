package com.bjut.community.service.impl;

import com.bjut.community.dao.CommentMapper;
import com.bjut.community.entity.Comment;
import com.bjut.community.service.CommentService;
import com.bjut.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommunityConstant, CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private DiscussPostServiceImpl discussPostServiceImpl;

    @Override
    public List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentsByEntity(entityType, entityId, offset, limit);
    }
    @Override
    public int findCountByEntity(int entityType, int entityId) {
        return commentMapper.selectCountByEntity(entityType, entityId);
    }
    @Override
    public Comment findCommentsById(int id) {
        return commentMapper.selectCommentById(id);
    }
    @Override
//    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED) // 添加评论事务
    public int addComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        int rows = commentMapper.insertComment(comment);
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            discussPostServiceImpl.updateCommentCount(comment.getEntityId(), count);
        }
        return rows;

    }

}
