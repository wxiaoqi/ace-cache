package com.ace.cache.aspect;

import com.ace.cache.annotation.Cache;
import com.ace.cache.api.CacheAPI;
import com.ace.cache.utils.CacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * 缓存开启注解拦截
 *
 * @author 小郎君
 * @description
 * @date 2017年5月18日
 * @since 1.7
 */
@Aspect
@Service
@Slf4j
public class CacheAspect {

    @Autowired
    private CacheAPI cacheAPI;
    @Autowired
    KeyGenerateService keyGenerateService;
    @Autowired
    ResultParseService resultParseService;

    @Pointcut("@annotation(com.ace.cache.annotation.Cache)")
    public void aspect() {
    }

    @Around("aspect()&&@annotation(anno)")
    public Object interceptor(ProceedingJoinPoint invocation, Cache anno)
            throws Throwable {
        MethodSignature signature = (MethodSignature) invocation.getSignature();
        Method method = signature.getMethod();
        Object result = null;
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] arguments = invocation.getArgs();
        String key = "";
        String value = "";
        try {
            key = keyGenerateService.getKey(anno, parameterTypes, arguments);
            if (!CacheUtil.isForceRefreshCache()){
                log.debug("redis get key : " + key);
                value = cacheAPI.get(key);
                if (null!= value) {
                    Type returnType = method.getGenericReturnType();
                    result = resultParseService.getResult(anno, value, returnType);
                }
            }
        } catch (Exception e) {
            log.error("获取缓存失败：" + key, e);
        } finally {
            if (result == null) {
                result = invocation.proceed();
                if (StringUtils.isNotBlank(key)) {
                    log.debug("redis set key : " + key);
                    cacheAPI.set(key, result, anno.expire());
                }
            }
            CacheUtil.clear();
        }
        return result;
    }




}
