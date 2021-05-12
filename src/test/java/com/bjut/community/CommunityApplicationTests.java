package com.bjut.community;

import com.bjut.community.config.AlphaConfig;
import com.bjut.community.dao.AlphaDao;
import com.bjut.community.service.AlphaService;
//import org.junit.jupiter.api.Test;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
class CommunityApplicationTests implements ApplicationContextAware {

    //	@Test
//	void contextLoads() {
//	}
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    @Test
    public void testApplicationContext(){
        System.out.println(applicationContext);
        AlphaDao dao = applicationContext.getBean(AlphaDao.class);
        System.out.println(dao.select());
    }
    @Test
    public void testBeanManagement(){
        AlphaService alphaService = applicationContext.getBean(AlphaService.class);
        System.out.println(alphaService);
    }

    @Autowired //依赖注入
    private AlphaDao alphaDao;
    @Autowired //依赖注入
    private AlphaService alphaService;
    @Autowired //依赖注入
    private AlphaConfig alphaConfig;
    @Test
    public void testDI(){
        System.out.println(alphaDao);
    }
}
