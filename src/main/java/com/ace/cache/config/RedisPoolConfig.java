package com.ace.cache.config;

/**
 * @author 小郎君
 * @create 2020/9/11.
 */

public class RedisPoolConfig {
    private String maxActive;
    private String maxIdle;
    private String maxWait;

    public String getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(String maxActive) {
        this.maxActive = maxActive;
    }

    public String getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(String maxIdle) {
        this.maxIdle = maxIdle;
    }

    public String getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(String maxWait) {
        this.maxWait = maxWait;
    }
}
