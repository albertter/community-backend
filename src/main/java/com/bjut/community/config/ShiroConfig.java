package com.bjut.community.config;


import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import com.bjut.community.shiro.CustomerRealm;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class ShiroConfig {

    // 1. shirofilter
    @Bean(name = "shiroFilterFactoryBean")
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(DefaultWebSecurityManager defaultWebSecurityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        //给filter设置安全管理器
        shiroFilterFactoryBean.setSecurityManager(defaultWebSecurityManager);
        //配置系统受限资源
        //配置系统公共资源
        HashMap<String, String> map = new HashMap<>();
        map.put("/resources/**", "anon");
        map.put("/login", "anon");//anon 设置为公共资源  放行资源放在下面
        map.put("/register", "anon");//anon 设置为公共资源  放行资源放在下面
        map.put("/captcha", "anon");//anon 设置为公共资源  放行资源放在下面
        map.put("/index", "anon");
        map.put("/discuss/**", "anon");

        map.put("/css/**", "anon");
        map.put("/jss/**", "anon");
        map.put("/img/**", "anon");

        map.put("/**", "authc");//authc 请求这个资源需要认证和授权

//        shiroFilterFactoryBean.setLoginUrl("/user/loginview");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(map);

        return shiroFilterFactoryBean;
    }

    @Bean
    public DefaultWebSecurityManager getDefaultWebSecurityManager(Realm realm) {
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        defaultWebSecurityManager.setRealm(realm);
        defaultWebSecurityManager.setRememberMeManager(rememberMeManager());
        return defaultWebSecurityManager;
    }

    @Bean
    public Realm getRealm() {
        Realm realm = new CustomerRealm();

//        //修改凭证校验匹配器
        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
//        //设置加密算法为md5
//        credentialsMatcher.setHashAlgorithmName("MD5");
//        //设置散列次数
//        credentialsMatcher.setHashIterations(1024);
//        realm.setCredentialsMatcher(credentialsMatcher);
//
//
//        //开启缓存管理
//        realm.setCacheManager(new RedisCacheManager());
//        realm.setCachingEnabled(true);//开启全局缓存
//        realm.setAuthenticationCachingEnabled(true);//认证认证缓存
//        realm.setAuthenticationCacheName("authenticationCache");
//        realm.setAuthorizationCachingEnabled(true);//开启授权缓存
//        realm.setAuthorizationCacheName("authorizationCache");
        return realm;
    }

    @Bean
    public ShiroDialect shiroDialect() {
        return new ShiroDialect();
    }

    /**
     * 配置记住我的管理器对象
     */
    @Bean
    public RememberMeManager rememberMeManager() {
        CookieRememberMeManager cManager = new CookieRememberMeManager();
        //    用户信息保存在cookie中
        SimpleCookie cookie = new SimpleCookie("rememberMe");
        //    保存时间
        cookie.setMaxAge(7 * 24 * 60 * 60);
        cManager.setCookie(cookie);
        return cManager;
    }

    @Bean
    protected CacheManager cacheManager() {
        return new MemoryConstrainedCacheManager();
    }


}
