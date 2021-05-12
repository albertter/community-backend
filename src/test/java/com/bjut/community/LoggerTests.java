package com.bjut.community;

import com.bjut.community.config.AlphaConfig;
import com.bjut.community.dao.AlphaDao;
import com.bjut.community.service.AlphaService;
import org.junit.Test;
//import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

import java.util.logging.Logger;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class LoggerTests {
    private static final Logger logger = (Logger) LoggerFactory.getLogger(LoggerTests.class);

    @Test
    public void testLogger() {

    }
}
