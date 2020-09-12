package com.ace.cache.aspect;

import com.ace.cache.annotation.CacheGlobalLock;
import com.ace.cache.config.RedisConfig;
import com.ace.cache.service.impl.RedisLock;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;

/**
 * 基于缓存的全局锁注解拦截
 */
@Aspect
@Service
@Slf4j
public class CacheGlobalLockAspect {
    @Autowired
    private KeyGenerateService keyGenerateService;
    @Autowired
    private RedisConfig redisConfig;
    @Autowired
    private RedisLock redisLock;

    @Pointcut("@annotation(com.ace.cache.annotation.CacheGlobalLock)")
    public void aspect() {
    }

    @Around("aspect()&&@annotation(anno)")
    public Object interceptor(ProceedingJoinPoint invocation, CacheGlobalLock anno)
            throws Throwable {
        MethodSignature signature = (MethodSignature) invocation.getSignature();
        Method method = signature.getMethod();
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] arguments = invocation.getArgs();
        String key = redisConfig.addSys(keyGenerateService.getKey(anno, parameterTypes, arguments));
        String timestamp = String.valueOf(System.currentTimeMillis() + anno.expire() * 60 * 1000);
        if (!redisLock.lock(key, timestamp)) {
            if (anno.waitLock()) {
                while (!redisLock.lock(key, timestamp)) {
                    try {
                        Thread.sleep(anno.retry());
                    } catch (Exception ex) {
                        log.error(ex.getMessage(), ex);
                    }
                }
            } else {
                return null;
            }
        }
        Object result = invocation.proceed();
        redisLock.release(key, timestamp);
        return result;
    }

}
