package com.ace.cache.aspect;

import com.ace.cache.annotation.CacheGlobalLock;
import com.ace.cache.api.CacheAPI;
import com.ace.cache.parser.IKeyGenerator;
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
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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
    private CacheAPI cacheAPI;
    private ConcurrentHashMap<String, IKeyGenerator> generatorMap = new ConcurrentHashMap<String, IKeyGenerator>();

    private ThreadLocal<String> lock = new ThreadLocal<>();

    @Pointcut("@annotation(com.ace.cache.annotation.CacheGlobalLock)")
    public void aspect() {
    }
    @Around("aspect()&&@annotation(anno)")
    public Object interceptor(ProceedingJoinPoint invocation, CacheGlobalLock anno)
            throws Throwable{
        MethodSignature signature = (MethodSignature) invocation.getSignature();
        Method method = signature.getMethod();
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] arguments = invocation.getArgs();
        String key = keyGenerateService.getKey(anno, parameterTypes, arguments);
        String  value = getVal(key);
        if (StringUtils.isNotBlank(value)){
            if (anno.waitLock()){
                while (StringUtils.isNotBlank(value)){
                    try
                    {
                        Thread.sleep(anno.retry());
                        value = getVal(key);
                    }
                    catch (Exception ex){
                        log.error(ex.getMessage(),ex);
                    }
                }
            }
            else{
                return null;
            }
        }
        //Lock
        lock(key,anno.expire());
        Object result = invocation.proceed();
        unlock(key);
        return result;
    }

    private void lock(String key, int expire){
        String value = UUID.randomUUID().toString();
        lock.set(value);
        cacheAPI.set(key,value,expire);
    }
    private void unlock(String key){
        String localVal = lock.get();
        String val = getVal(key);
        if (localVal.equalsIgnoreCase(val)) {
            cacheAPI.remove(key);
        }
        lock.remove();
    }

    private String getVal(String key) {
        try {
            String value = cacheAPI.get(key);
            return value;
        } catch (Exception e) {
            return "";
        }
    }

}
