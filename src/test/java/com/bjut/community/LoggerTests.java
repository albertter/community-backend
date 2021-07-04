package com.bjut.community;

import org.junit.Test;
//import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
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
