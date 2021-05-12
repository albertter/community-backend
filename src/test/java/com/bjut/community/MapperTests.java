package com.bjut.community;

import com.bjut.community.dao.DiscussPostMapper;
import com.bjut.community.dao.MessageMapper;
import com.bjut.community.dao.UserMapper;
import com.bjut.community.entity.DiscussPost;
import com.bjut.community.entity.Message;
import com.bjut.community.entity.User;
import org.junit.Test;
//import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
class MapperTests {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void selectTest() {
        User user = userMapper.selectById(101);
        System.out.println(user);
    }

    @Test
    public void insertTest() {
        User user = new User();
        user.setUsername("Mario");
        user.setEmail("123@123.com");
        user.setCreateTime(new Date());
        int rows = userMapper.insertUser(user);
        System.out.println(rows);
    }

    @Test
    public void testSelectPosts() {
//        DiscussPost discussPost = new DiscussPost();
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(149, 0, 10);
        for (DiscussPost discussPost1 : list) {
            System.out.println(discussPost1);
        }
        int rows = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(rows);
    }

    @Test
    public void testMessage() {
        List<Message> conversations = messageMapper.selectConversations(111, 10, 10);
//        System.out.println(conversations);
        for (Message conversation : conversations) {
            System.out.println(conversation);
        }
        System.out.println(messageMapper.selectLettersUnreadCount(111, null));
    }

}
