package com.bjut.community.service.impl;

import com.bjut.community.dao.MessageMapper;
import com.bjut.community.entity.Message;
import com.bjut.community.service.MessageService;
import com.bjut.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements CommunityConstant, MessageService {
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private DiscussPostServiceImpl discussPostServiceImpl;

    @Override
    public List<Message> findConversations(int userId, int offset, int limit) {
        return messageMapper.selectConversations(userId, offset, limit);
    }

    @Override
    public int findConversationsCount(int userId) {
        return messageMapper.selectConversationsCount(userId);
    }

    @Override
    public List<Message> findLetters(String conversationsId, int offset, int limit) {
        return messageMapper.selectLetters(conversationsId, offset, limit);
    }

    @Override
    public int findLettersCount(String conversationsId) {
        return messageMapper.selectLettersCount(conversationsId);
    }

    @Override
    public int findLettersUnreadCount(int userId, String conversationId) {
        return messageMapper.selectLettersUnreadCount(userId, conversationId);
    }

    @Override
    public int addMessage(Message message) {
        return messageMapper.insertMessage(message);
    }

    @Override
    public int readMessage(List<Integer> ids) {
        return messageMapper.updateStatus(ids, 1);
    }

    @Override
    public Message findLatestNotice(int userId, String topic) {
        return messageMapper.selectLatestNotice(userId, topic);
    }

    @Override
    public int findNoticeCount(int userId, String topic) {
        return messageMapper.selectNoticeCount(userId, topic);
    }

    @Override
    public int findNoticeUnreadCount(int userId, String topic) {
        return messageMapper.selectNoticeUnreadCount(userId, topic);
    }

    @Override
    public List<Message> findNotices(int userId, String topic, int offset, int limit) {
        return messageMapper.selectNotices(userId, topic, offset, limit);
    }

}
