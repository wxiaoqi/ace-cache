package com.ace.cache.annotation;

import com.ace.cache.constants.CacheScope;
import com.ace.cache.parser.IKeyGenerator;
import com.ace.cache.parser.impl.DefaultKeyGenerator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 全局锁（基于redis缓存）
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.TYPE})
public @interface CacheGlobalLock {

    public String key() default "";
    /**
     * 过期时间（单位分钟）
     */
    public int expire() default 60;

    /**
     * 重试加锁时间间隔（单位ms）
     * @return
     */
    public int retry() default 5000;

    /**
     * 是否一直等待全局锁？ 默认为否，获取不到锁则不执行程序。
     */
    public boolean waitLock() default false;

    /**
     * 作用域
     *
     * @return
     * @author Ace
     * @date 2017年5月3日
     */
    public CacheScope scope() default CacheScope.application;

    /**
     * 键值解析类
     *
     * @return
     */
    public Class<? extends IKeyGenerator> generator() default DefaultKeyGenerator.class;
}
