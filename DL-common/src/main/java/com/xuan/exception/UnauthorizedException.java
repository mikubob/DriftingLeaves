package com.xuan.exception;

import com.xuan.constant.MessageConstant;

public class UnauthorizedException extends TokenException{
    public UnauthorizedException() {
    }
    public UnauthorizedException(String msg) {
        super(msg);
    }
}
