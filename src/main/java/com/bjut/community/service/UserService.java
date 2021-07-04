package com.bjut.community.service;

import com.bjut.community.entity.LoginTicket;
import com.bjut.community.entity.User;

import java.util.Map;

public interface UserService {

    /**
     * 查找用户
     * @param id 用户ID
     * @return 用户
     */
    public User findUserById(int id);


    /**
     * 注册
     * @param user 用户
     * @return 注册信息
     */
    public Map<String, Object> register(User user);

    /**
     * 激活用户
     * @param userID 用户ID
     * @param code 激活码
     * @return 激活成功？
     */
    public int activation(int userID, String code);

    /**
     * 登录
     * @param username 用户名
     * @param password 密码
     * @param expiredSeconds 过期时间
     * @return 登录信息
     */
    public Map<String, Object> login(String username, String password, int expiredSeconds);

    /**
     * 注销用户
     * @param ticket 登录凭证
     */
    public void logout(String ticket);

    public LoginTicket findLoginTicket(String ticket);

    public void updateHeader(int userId, String headerUrl);


    public int updatePassword(User user, String oldPassword, String newPassword);

    public User findUserByUsername(String username);


    public User findUserByName(String username);

    public int findTypeByName(String username);

}
