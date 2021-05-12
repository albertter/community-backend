package com.bjut.community.dao;

import com.bjut.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {
    List<Message> selectConversations(int userId, int offset, int limit);

    int selectConversationsCount(int userId);

    List<Message> selectLetters(String conversationId, int offset, int limit);

    int selectLettersCount(String conversationId);

    int selectLettersUnreadCount(int userId, String conversationId);

    int insertMessage(Message message);

    int updateStatus(List<Integer> ids, int status);

    Message selectLatestNotice(int userId, String topic);

    int selectNoticeCount(int userId, String topic);

    int selectNoticeUnreadCount(int userId, String topic);

    List<Message> selectNotices(int userId, String topic, int offset, int limit);

}
