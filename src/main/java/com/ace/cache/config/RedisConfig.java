package com.ace.cache.config;

import com.ace.cache.utils.PropertiesLoaderUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.PostConstruct;

public class RedisConfig {
    private RedisPoolConfig pool = new RedisPoolConfig();
    private String host;
    private String password;
    private String timeout;
    private String database;
    private String port;
    private String enable;
    private String sysName;
    private String userKey;
    private Long refreshTimeout;


    @PostConstruct
    public void init() {
        PropertiesLoaderUtils prop = new PropertiesLoaderUtils("application.properties");
        if (!StringUtils.isBlank(prop.getProperty("redis.host"))) {
            host = prop.getProperty("redis.host");
            pool.setMaxActive(prop.getProperty("redis.pool.maxActive"));
            pool.setMaxIdle(prop.getProperty("redis.pool.maxIdle"));
            pool.setMaxWait(prop.getProperty("redis.pool.maxWait"));
            password = prop.getProperty("redis.password");
            timeout = prop.getProperty("redis.timeout");
            database = prop.getProperty("redis.database");
            port = prop.getProperty("redis.port");
            sysName = prop.getProperty("redis.sysName");
            enable = prop.getProperty("redis.enable");
            String refreshTimeoutStr = prop.getProperty("redis.cache.refreshTimeout");
            if (StringUtils.isNotBlank(refreshTimeoutStr)) {
                refreshTimeout = Long.parseLong(refreshTimeoutStr.trim());
            } else {
                refreshTimeout = 0L;
            }
            userKey = prop.getProperty("redis.userkey");

        }
    }

    public String addSys(String key) {
        String result = key;
        String sys = this.getSysName();
        if (key.startsWith(sys))
            result = key;
        else
            result = sys + ":" + key;
        return result;
    }


    public RedisPoolConfig getPool() {
        return pool;
    }

    public void setPool(RedisPoolConfig pool) {
        this.pool = pool;
    }

    public void setRefreshTimeout(Long refreshTimeout) {
        this.refreshTimeout = refreshTimeout;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public Long getRefreshTimeout() {
        return this.refreshTimeout;
    }
}
