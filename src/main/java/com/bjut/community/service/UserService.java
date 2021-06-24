package com.bjut.community.service;

import com.bjut.community.dao.UserMapper;
import com.bjut.community.entity.LoginTicket;
import com.bjut.community.entity.User;
import com.bjut.community.util.CommunityConstant;
import com.bjut.community.util.CommunityUtil;
import com.bjut.community.util.MailClient;
import com.bjut.community.util.RedisKeyUtil;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private RedisTemplate redisTemplate;
//    @Autowired
//    private LoginTicketMapper loginTicketMapper;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int id) {
        return userMapper.selectById(id);
//        User user = getCache(id);
//        if (user == null) {
//            user = initCache(id);
//        }
//        return user;
    }


    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        if (StringUtils.isNullOrEmpty(user.getUsername())) {
            map.put("usernameMsg", "用户名不能为空");
            return map;
        }
        if (StringUtils.isNullOrEmpty(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }
        if (StringUtils.isNullOrEmpty(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空");
            return map;
        }
        //验证用户名
        User u = userMapper.selectByName(user.getUsername());
        if (u != null) {
            map.put("usernameMsg", "该用户名已注册");
            return map;
        }
        //验证邮箱是否已经注册
        u = userMapper.selectByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg", "该邮箱已注册");
            return map;
        }
        //注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setStatus(0);
        user.setType(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("https://static.nowcoder.com/head/%dm.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //激活账号
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);

        String content = templateEngine.process("/mail/activation", context);
        System.out.println(content);
        mailClient.send(user.getEmail(), "激活账号", content);

        return map;
    }

    public int activation(int userID, String code) {
        User user = userMapper.selectById(userID);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            userMapper.updateUser(userID, 1);
            clearCache(userID);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }

    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isNullOrEmpty(username)) {
            map.put("usernameMsg", "用户名不能为空");
            return map;
        }
        if (StringUtils.isNullOrEmpty(password)) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "用户名不存在");
            return map;
        }
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "账号未激活");
            return map;
        }

        password = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "密码不正确");
            return map;
        }

        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);

//        loginTicketMapper.insertLoginTicket(loginTicket);

        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey, loginTicket); //字符串

        map.put("ticket", loginTicket.getTicket());

        return map;
    }

    public void logout(String ticket) {
//        loginTicketMapper.updateStatus(ticket, 1);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey, loginTicket);
    }

    public LoginTicket findLoginTicket(String ticket) {
//        return loginTicketMapper.selectByTicket(ticket);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }

    public int updateHeader(int userId, String headerUrl) {
        int rows = userMapper.updateHeader(userId, headerUrl);
        clearCache(userId);
        return rows;
    }


    public int updatePassword(User user, String oldPassword, String newPassword) {
        oldPassword = CommunityUtil.md5(oldPassword + user.getSalt());
        if (!user.getPassword().equals(oldPassword)) {
            return 0;
        }
        newPassword = CommunityUtil.md5(newPassword + user.getSalt());
        userMapper.updatePassword(user.getId(), newPassword);
        return 1;
    }

    public User findUserByUsername(String username) {
        return userMapper.selectByName(username);
    }

    private User getCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }

    private User initCache(int userId) {
        User user = userMapper.selectById(userId);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }

    private void clearCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);

    }

    //    public Collection<? extends GrantedAuthority> getAuthorities(int userId) {
//        User user = this.findUserById(userId);
//
//        List<GrantedAuthority> list = new ArrayList<>();
//        list.add(new GrantedAuthority() {
//            @Override
//            public String getAuthority() {
//                switch (user.getType()) {
//                    case 1:
//                        return AUTHORITY_ADMIN;
//                    case 2:
//                        return AUTHORITY_MODERATOR;
//                    default:
//                        return AUTHORITY_USER;
//                }
//            }
//        });
//        return list;
//    }
    public User findUserByName(String username) {
        return userMapper.selectByName(username);
    }

    public int findTypeByName(String username) {
        return userMapper.selectTypeByName(username);
    }

}
