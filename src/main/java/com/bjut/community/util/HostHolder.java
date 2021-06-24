package com.bjut.community.util;

import com.bjut.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * 持有用户信息，用于代替session对象
 */
@Component
@Deprecated
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<>();

    public User getUser() {
        return users.get();
    }

    public void setUser(User user) {
        users.set(user);
    }

    public void clear() {
        users.remove();
    }
}
