package com.ace.cache.aspect;

import com.ace.cache.annotation.Cache;
import com.ace.cache.parser.ICacheResultParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ResultParseService {
    private ConcurrentHashMap<String, ICacheResultParser> parserMap = new ConcurrentHashMap<String, ICacheResultParser>();

    /**
     * 解析结果
     *
     * @param anno
     * @param value
     * @param returnType
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public Object getResult(Cache anno, String value,
                             Type returnType) throws InstantiationException,
            IllegalAccessException {
        String parserClsName = anno.parser().getName();
        ICacheResultParser parser = null;
        Object result = null;
        if (parserMap.containsKey(parserClsName)) {
            parser = parserMap.get(parserClsName);
        } else {
            parser = anno.parser().newInstance();
            parserMap.put(parserClsName, parser);
        }
        if (String.class.equals(returnType)){
            return value;
        }
        else if (parser != null) {
            if (anno.result()[0].equals(Object.class)) {
                result = parser.parse(value, returnType,
                        null);
            } else {
                result = parser.parse(value, returnType,
                        anno.result());
            }
        }
        return result;
    }
}
