package com.bjut.community;

import com.bjut.community.service.impl.DiscussPostServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class PostCacheTest {
    @Autowired
    private DiscussPostServiceImpl discussPostServiceImpl;

    @Test
    public void test1() {
        discussPostServiceImpl.findDiscussPostById(1);
        discussPostServiceImpl.findDiscussPostById(1);
    }
}
