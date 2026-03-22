package com.xuan.constant;

/**
 * Redis常量类
 */
public class RedisConstant {
    public static final String ARTICLE_VIEW_COUNT = "article:viewCount";
    public static final String ARTICLE_LIKE_COUNT = "article:likeCount";
    public static final String ARTICLE_LIKE_USER_SET = "article:like:users:";
    public static final String LOCK_LIKE_COUNT_SYNC = "lock:likeCountSync";
    public static final String LOCK_VIEW_COUNT_SYNC = "lock:viewCountSync";
    public static final String RATE_LIMIT_KEY = "visitor:rate:";
    public static final String BLOCKED_KEY = "visitor:blocked:";
    public static final String TOKEN_PREFIX = "token:active:";
    public static final String KEY_VERIFY_CODE = "verify_code";
    public static final String KEY_RATE_LIMIT = "rate_limit";
    public static final String KEY_ATTEMPT_COUNT = "attempt_count";
    public static final String KEY_LOCK = "lock";
    public static final String VISITOR_KEY = "visitor:fingerprint:";
}
