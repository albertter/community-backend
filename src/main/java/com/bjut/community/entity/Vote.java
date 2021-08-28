package com.bjut.community.entity;

/**
 * @author: 褚真
 * @date: 2021/8/3
 * @time: 15:51
 * @description:
 */
public class Vote {
    private int id;
    private User user;
    private Poll poll;
    private Choice choice;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Poll getPoll() {
        return poll;
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
    }

    public Choice getChoice() {
        return choice;
    }

    public void setChoice(Choice choice) {
        this.choice = choice;
    }
}
