package com.bjut.community.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository("AlphaHibernate")
@Primary
public class AlphaDapHibernateSimpl implements AlphaDao{
    @Override
    public String select() {
//        System.out.println("MyBatis");
        return "Hibernate";
    }
}
