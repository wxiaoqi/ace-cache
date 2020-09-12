package com.ace.cache.aspect;

import com.ace.cache.annotation.CacheClear;
import com.ace.cache.api.CacheAPI;
import com.ace.cache.config.RedisConfig;
import com.ace.cache.constants.CacheScope;
import com.ace.cache.parser.IKeyGenerator;
import com.ace.cache.parser.impl.DefaultKeyGenerator;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 清除缓存注解拦截
 *
 * @author 小郎君
 * @description
 * @date 2017年5月18日
 * @since 1.7
 */
@Aspect
@Service
@Slf4j
public class CacheClearAspect {
    @Autowired
    RedisConfig redisConfig;

    @Autowired
    private IKeyGenerator keyParser;
    @Autowired
    private CacheAPI cacheAPI;

    private ConcurrentHashMap<String, IKeyGenerator> generatorMap = new ConcurrentHashMap<String, IKeyGenerator>();

    @Pointcut("@annotation(com.ace.cache.annotation.CacheClear)")
    public void aspect() {
    }

    @Around("aspect()&&@annotation(anno)")
    public Object interceptor(ProceedingJoinPoint invocation, CacheClear anno)
            throws Throwable {
        Object result = invocation.proceed();
        try{
            clearCache(invocation, anno);
        }
        catch (Exception ex){
            log.error(ex.getMessage(),ex);
        }
        return result;
    }

    private void clearCache(ProceedingJoinPoint invocation, CacheClear anno) throws InstantiationException, IllegalAccessException {
        MethodSignature signature = (MethodSignature) invocation.getSignature();
        Method method = signature.getMethod();
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] arguments = invocation.getArgs();
        String key = null;
        List<String> keyPres = null;
        if (StringUtils.isNotBlank(anno.key())) {
            key = getKey(anno, anno.key(), CacheScope.application,
                    parameterTypes, arguments);
            log.debug("redis remove key : " + key);
            cacheAPI.remove(key);
        } else if (StringUtils.isNotBlank(anno.pre())) {
            keyPres = new ArrayList<>();
            key = getKey(anno, anno.pre(), CacheScope.application,
                    parameterTypes, arguments);
            keyPres.add(key);
            log.debug("redis remove key pre : " + key);
            cacheAPI.removeByPre(key);
        } else if (anno.keys().length >= 1) {
            keyPres = new ArrayList<>();
            for (String tmp : anno.keys()) {
                tmp = getKey(anno, tmp, CacheScope.application, parameterTypes,
                        arguments);
                keyPres.add(tmp);
                log.debug("redis remove key pre : " + tmp);
                cacheAPI.removeByPre(tmp);
            }
        }

        // 延时删除
        Long timeout = redisConfig.getRefreshTimeout();
        if (timeout >0 ) {
            final String fKey = key;
            final List<String> fKeypre = new ArrayList<>();
            fKeypre.addAll(keyPres);
            CompletableFuture.runAsync(()->{
                try{
                    Thread.sleep(timeout);
                }
                catch (Exception e){
                    log.debug(e.getMessage(),e);
                }
                log.debug("redis remove after "+ timeout + "ms :");
                if (StringUtils.isNotBlank(fKey)) {
                    log.debug("redis remove key : " + fKey);
                    cacheAPI.remove(fKey);
                }
                if(null!= fKeypre && !fKeypre.isEmpty()){
                    for (String tmp : fKeypre) {
                        log.debug("redis remove key pre : " + tmp);
                        cacheAPI.removeByPre(tmp);
                    }
                    fKeypre.clear();
                }
            });
        }
        key = null;
        keyPres.clear();
    }

    /**
     * 解析表达式
     *
     * @param anno
     * @param parameterTypes
     * @param arguments
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private String getKey(CacheClear anno, String key, CacheScope scope,
                          Class<?>[] parameterTypes, Object[] arguments)
            throws InstantiationException, IllegalAccessException {
        String finalKey;
        String generatorClsName = anno.generator().getName();
        IKeyGenerator keyGenerator = null;
        if (anno.generator().equals(DefaultKeyGenerator.class)) {
            keyGenerator = keyParser;
        } else {
            if (generatorMap.containsKey(generatorClsName)) {
                keyGenerator = generatorMap.get(generatorClsName);
            } else {
                keyGenerator = anno.generator().newInstance();
                generatorMap.put(generatorClsName, keyGenerator);
            }
        }
        finalKey = keyGenerator.getKey(key, scope, parameterTypes, arguments);
        return finalKey;
    }
}
