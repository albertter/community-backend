package com.bjut.community.controller.interceptor;

import com.bjut.community.entity.User;
import com.bjut.community.service.impl.MessageServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class MessageInterceptor implements HandlerInterceptor {

//    @Autowired
//    private HostHolder hostHolder;

    @Autowired
    private MessageServiceImpl messageServiceImpl;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        if (user != null && modelAndView != null) {
            int letterUnreadCount = messageServiceImpl.findLettersUnreadCount(user.getId(), null);
            int noticeUnreadCount = messageServiceImpl.findNoticeUnreadCount(user.getId(), null);
            modelAndView.addObject("allUnreadCount", letterUnreadCount + noticeUnreadCount);
        }
    }
}
