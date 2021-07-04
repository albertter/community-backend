package com.bjut.community.service;

import com.bjut.community.entity.Message;

import java.util.List;

public interface MessageService {


    public List<Message> findConversations(int userId, int offset, int limit);

    public int findConversationsCount(int userId);

    public List<Message> findLetters(String conversationsId, int offset, int limit);

    public int findLettersCount(String conversationsId);

    public int findLettersUnreadCount(int userId, String conversationId);

    public int addMessage(Message message);

    public int readMessage(List<Integer> ids);

    public Message findLatestNotice(int userId, String topic);

    public int findNoticeCount(int userId, String topic);

    public int findNoticeUnreadCount(int userId, String topic);

    public List<Message> findNotices(int userId, String topic, int offset, int limit);

}
