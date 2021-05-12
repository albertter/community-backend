package com.bjut.community;

import com.bjut.community.util.MailClient;
import org.junit.Test;
//import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testtextMail() {
        mailClient.send("zhen.chu@foxmail.com", "JavaSSM community Test", "test");
    }

    @Test
    public void testtextMail2() {
        Context context = new Context();
        context.setVariable("username", "sunday");

        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);
        mailClient.send("zhen.chu@foxmail.com", "html", content);
    }
}
