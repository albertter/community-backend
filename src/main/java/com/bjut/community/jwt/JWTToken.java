package com.bjut.community.jwt;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author 褚真
 * @date 2021/7/4 004
 */
public class JWTToken implements AuthenticationToken {

    // 密钥
    private String token;

    public JWTToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
