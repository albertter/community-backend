package com.bjut.community.controller.interceptor;

import com.bjut.community.entity.User;
import com.bjut.community.service.impl.DataServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class DataInterceptor implements HandlerInterceptor {

//    @Autowired
//    private HostHolder hostHolder;

    @Autowired
    private DataServiceImpl dataServiceImpl;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        User user = (User) SecurityUtils.getSubject().getPrincipal();
//        // count uv
//        String ip = request.getRemoteHost();
//        dataServiceImpl.recordUV(ip);
//        // count dau
//        if (user != null) {
//            dataServiceImpl.recordDAU(user.getId());
//
//        }
        return true;
    }
}
