package com.bjut.community.service;

import com.bjut.community.dao.MessageMapper;
import com.bjut.community.entity.Message;
import com.bjut.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService implements CommunityConstant {
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private DiscussPostService discussPostService;

    public List<Message> findConversations(int userId, int offset, int limit) {
        return messageMapper.selectConversations(userId, offset, limit);
    }

    public int findConversationsCount(int userId) {
        return messageMapper.selectConversationsCount(userId);
    }

    public List<Message> findLetters(String conversationsId, int offset, int limit) {
        return messageMapper.selectLetters(conversationsId, offset, limit);
    }

    public int findLettersCount(String conversationsId) {
        return messageMapper.selectLettersCount(conversationsId);
    }

    public int findLettersUnreadCount(int userId, String conversationId) {
        return messageMapper.selectLettersUnreadCount(userId, conversationId);
    }

    public int addMessage(Message message) {
        return messageMapper.insertMessage(message);
    }

    public int readMessage(List<Integer> ids) {
        return messageMapper.updateStatus(ids, 1);
    }

    public Message findLatestNotice(int userId, String topic) {
        return messageMapper.selectLatestNotice(userId, topic);
    }

    public int findNoticeCount(int userId, String topic) {
        return messageMapper.selectNoticeCount(userId, topic);
    }

    public int findNoticeUnreadCount(int userId, String topic) {
        return messageMapper.selectNoticeUnreadCount(userId, topic);
    }

    public List<Message> findNotices(int userId, String topic, int offset, int limit) {
        return messageMapper.selectNotices(userId, topic, offset, limit);
    }

}
