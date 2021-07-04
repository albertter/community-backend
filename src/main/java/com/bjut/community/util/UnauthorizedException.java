package com.bjut.community.util;

/**
 * @author 褚真
 * @date 2021/7/4 004
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String msg) {
        super(msg);
    }

    public UnauthorizedException() {
        super();
    }
}
