package com.swak.wechat.token;

/**
 * 用于可过期的 token
 *
 * @author lifeng
 */
public interface ExpireAble {

    /**
     * 添加的時間
     *
     * @return 添加时间
     */
    Long getAddTime();

    /**
     * 有效的期限
     *
     * @return 有效期限
     */
    Integer getExpires_in();

    /**
     * 判断是否过期
     *
     * @param adjustSecond 允许设置一个区间，例如至少保证 access_token 还有10分中的有效期
     * @return 是否失效
     */
    default boolean isExpired(int adjustSecond) {
        if (this.getAddTime() != null && (System.currentTimeMillis() - this.getAddTime()
                + adjustSecond * 1000 <= this.getExpires_in() * 1000)) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}
