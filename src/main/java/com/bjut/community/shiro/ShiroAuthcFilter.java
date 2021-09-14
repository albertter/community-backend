package com.bjut.community.shiro;

import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * @author 褚真
 * @date 2021/8/5 005
 */
public class ShiroAuthcFilter extends FormAuthenticationFilter {
    private static final Logger log = LoggerFactory.getLogger(ShiroAuthcFilter.class);

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        if (this.isLoginRequest(request, response)) {
            return true;
        } else {
            this.saveRequestAndRedirectToLogin(request, response);
            return false;
        }
    }
}
