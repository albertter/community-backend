package com.bjut.community.controller.interceptor;

import com.bjut.community.entity.User;
import com.bjut.community.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;
//
//    @Autowired
//    private HostHolder hostHolder;

//    @Override
//    //这个方法将在请求处理之前进行调用。
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        // 从cookie中获取凭证
//        String ticket = CookieUtil.getValue(request, "ticket");
//
//        if (ticket != null) {
//            // 查询凭证
//            LoginTicket loginTicket = userService.findLoginTicket(ticket);
//            // 检查凭证是否有效
//            if (loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())) {
//                // 根据凭证查询用户
//                User user = userService.findUserById(loginTicket.getUserId());
//                // 在本次请求中持有用户
////                hostHolder.setUser(user);
//                // 构建用户认证的结果,并存入SecurityContext,以便于Security进行授权.
//            }
//        }
//
//        return true;
//    }

    @Override
    //只有在 preHandle() 方法返回值为true 时才会执行。会在Controller 中的方法调用之后，DispatcherServlet 返回渲染视图之前被调用。
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        System.out.println("loginuser " + user);
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

//    @Override
//    //只有在 preHandle() 方法返回值为true 时才会执行。在整个请求结束之后， DispatcherServlet 渲染了对应的视图之后执行。
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        hostHolder.clear();
//    }
}
