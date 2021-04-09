package com.bjut.community.service;

import com.bjut.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
public class AlphaService {
    @Autowired
    private AlphaDao alphaDao;
    public AlphaService() {
        System.out.println("实例化AlphaService");
    }

    @PostConstruct // 构造完之后调用该方法
    public void init() {
        System.out.println("AlphaService init");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("销毁AlphaService");
    }
    public String find(){
        return alphaDao.select();
    }
}
