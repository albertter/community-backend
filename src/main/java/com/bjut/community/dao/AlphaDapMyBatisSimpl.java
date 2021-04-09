package com.bjut.community.dao;

import org.springframework.stereotype.Repository;

@Repository
public class AlphaDapMyBatisSimpl implements AlphaDao{
    @Override
    public String select() {
//        System.out.println("MyBatis");
        return "MyBatis";
    }
}
