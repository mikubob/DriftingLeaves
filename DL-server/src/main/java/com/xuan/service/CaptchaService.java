package com.xuan.service;

import com.xuan.vo.CaptchaVO;

/**
 * 访客算术验证码服务，挑战绑定当前已验证访客并限时一次使用。
 */
public interface CaptchaService {
    /**
     * 创建当前访客的五分钟验证码，响应只包含题目和挑战 ID。
     * @return 不含答案的验证码
     * @throws com.xuan.exception.TokenException 未建立访客会话
     */
    CaptchaVO generate();

    /**
     * 原子消费当前访客的挑战并校验完整答案，校验失败同样消耗挑战。
     * @param id 挑战 ID
     * @param answer 客户端提交的整数答案
     * @throws com.xuan.exception.TokenException 未建立访客会话
     * @throws com.xuan.exception.ValidationException 挑战无效、已过期或答案错误
     */
    void verify(String id, String answer);
}
