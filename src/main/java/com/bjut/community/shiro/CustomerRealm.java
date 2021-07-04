package com.bjut.community.shiro;

import com.bjut.community.entity.User;
import com.bjut.community.jwt.JWTToken;
import com.bjut.community.service.UserService;
import com.bjut.community.util.CommunityConstant;
import com.bjut.community.jwt.JWTUtil;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * @author zhen
 */
public class CustomerRealm extends AuthorizingRealm implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(CustomerRealm.class);

    @Autowired
    private UserService userService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }

    /**
     * 只有当需要检测用户权限的时候才会调用此方法，例如checkRole,checkPermission之类的
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //获取身份信息
        String username = JWTUtil.getUsername(principalCollection.toString());
        User user = userService.findUserByUsername(username);
//        User primaryPrincipal = (User) principalCollection.getPrimaryPrincipal();
//        System.out.println("调用授权验证: " + primaryPrincipal);

//        UserService userService = (UserService) ApplicationContextUtils
//                .getBean("userService");
        int type = user.getType();

        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
//        simpleAuthorizationInfo.addRole(user.getRole());
        if (type == 0) {
            simpleAuthorizationInfo.addRole(AUTHORITY_USER);
        }
        if (type == 1) {
            simpleAuthorizationInfo.addRole(AUTHORITY_ADMIN);
        }
        if (type == 2) {
            simpleAuthorizationInfo.addRole(AUTHORITY_MODERATOR);
        }
        return simpleAuthorizationInfo;
//        return null;
    }

    /**
     * 默认使用此方法进行用户名正确与否验证，错误抛出异常即可。
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //根据身份信息
        System.out.println(authenticationToken);
        String token = (String) authenticationToken.getCredentials();
        System.out.println(token);
        String username = JWTUtil.getUsername(token);
        System.out.println(username);
        if (username == null) {
            throw new AuthenticationException("token invalid");
        }
        User user = userService.findUserByUsername(username);
        if (user == null) {
            throw new AuthenticationException("User didn't existed!");
        }
        if (!JWTUtil.verify(token, username, user.getPassword())) {
            throw new AuthenticationException("Username or password error");
        }

        return new SimpleAuthenticationInfo(token, token, "CustomerRealm");
    }
}
