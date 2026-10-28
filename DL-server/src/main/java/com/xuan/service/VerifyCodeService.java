package com.xuan.service;

/**
 * 验证码服务
 */
public interface VerifyCodeService {

    /**
     * 使用安全随机数生成六位邮件验证码。
     * @return 含前导零的六位验证码
     */
    String generateCode();

    /**
     * 原子预约验证码及发送冷却，锁定或冷却期间不覆盖现有预约。
     * @param userId 管理员 ID
     * @param code 待发送验证码
     * @return 本次预约的唯一标识，预约失败时为 null
     */
    String reserveCode(Long userId, String code);

    /**
     * 邮件发送失败时仅撤销仍属于本次预约的验证码和冷却。
     * @param userId 管理员 ID
     * @param reservation reserveCode 返回的预约标识
     */
    void cancelCode(Long userId, String reservation);

    /**
     * 获取当前管理员发送验证码的剩余冷却时间。
     * @param userId 管理员 ID
     * @return 剩余秒数，无冷却时为零
     */
    Long getRemainingCooldown(Long userId);

    /**
     * 检查当前管理员的邮件验证码是否被锁定。
     * @param userId 管理员 ID
     * @return 是否处于锁定期
     */
    boolean isLocked(Long userId);

    /**
     * 读取验证码锁定的剩余时间。
     * @param userId 管理员 ID
     * @return 剩余整分钟数，无锁定时为零
     */
    Long getLockRemainingMinutes(Long userId);

    /**
     * 检查当前管理员是否可以尝试邮件验证码验证。
     * @param userId 管理员 ID
     * @return 未锁定时为 true
     */
    boolean canAttempt(Long userId);

    /**
     * 原子验证并消费验证码；成功保留发送冷却，失败累计次数并按阈值锁定。
     * @param userId 管理员 ID
     * @param code 提交的验证码
     * @return 验证是否成功
     */
    boolean verifyCode(Long userId, String code);

    /**
     * 读取锁定前剩余的验证码尝试次数。
     * @param userId 管理员 ID
     * @return 剩余次数，锁定时为零
     */
    Long getRemainingAttempts(Long userId);
}
