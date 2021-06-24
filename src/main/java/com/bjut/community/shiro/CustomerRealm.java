package com.bjut.community.shiro;

import com.bjut.community.entity.User;
import com.bjut.community.service.UserService;
import com.bjut.community.util.ApplicationContextUtils;
import com.bjut.community.util.CommunityConstant;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.util.ObjectUtils;


/**
 * @author zhen
 */
public class CustomerRealm extends AuthorizingRealm implements CommunityConstant {

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //获取身份信息
        User primaryPrincipal = (User) principalCollection.getPrimaryPrincipal();
        System.out.println("调用授权验证: " + primaryPrincipal);

//        UserService userService = (UserService) ApplicationContextUtils
//                .getBean("userService");
        int type = primaryPrincipal.getType();

        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        if (type == 0) simpleAuthorizationInfo.addRole(AUTHORITY_USER);
        if (type == 1) simpleAuthorizationInfo.addRole(AUTHORITY_ADMIN);
        if (type == 2) simpleAuthorizationInfo.addRole(AUTHORITY_MODERATOR);
        return simpleAuthorizationInfo;
//        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //根据身份信息
        String principal = (String) authenticationToken.getPrincipal();
        UserService userService = (UserService) ApplicationContextUtils
                .getBean("userService");
        User user = userService.findUserByUsername(principal);
        if (!ObjectUtils.isEmpty(user)) {
            return new SimpleAuthenticationInfo(user, user.getPassword(),
                    this.getName());
        }
        return null;
    }
}
