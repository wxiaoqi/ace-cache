package com.ace.cache.aspect;

import com.ace.cache.annotation.Cache;
import com.ace.cache.annotation.CacheGlobalLock;
import com.ace.cache.parser.IKeyGenerator;
import com.ace.cache.parser.impl.DefaultKeyGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class KeyGenerateService {
    @Autowired
    private IKeyGenerator keyParser;
    private ConcurrentHashMap<String, IKeyGenerator> generatorMap = new ConcurrentHashMap<String, IKeyGenerator>();

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
    public String getKey(Cache anno, Class<?>[] parameterTypes,
                          Object[] arguments) throws InstantiationException,
            IllegalAccessException {
        String key;
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

        key = keyGenerator.getKey(anno.key(), anno.scope(), parameterTypes,
                arguments);
        return key;
    }

    public String getKey(CacheGlobalLock anno, Class<?>[] parameterTypes,
                         Object[] arguments) throws InstantiationException,
            IllegalAccessException {
        String key;
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

        key = keyGenerator.getKey(anno.key(), anno.scope(), parameterTypes,
                arguments);
        return key;
    }
}
