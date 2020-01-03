package com.ace.cache.parser.impl;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ace.cache.config.RedisConfig;
import com.ace.cache.parser.IUserKeyGenerator;
import com.ace.cache.utils.ReflectionUtils;
import com.ace.cache.constants.CacheScope;
import com.ace.cache.utils.WebUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ace.cache.parser.IKeyGenerator;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Service
public class DefaultKeyGenerator extends IKeyGenerator {
    @Autowired
    RedisConfig redisConfig;

    @Autowired(required = false)
    private IUserKeyGenerator userKeyGenerator;

    @Override
    public String buildKey(String key, CacheScope scope, Class<?>[] parameterTypes, Object[] arguments) {
        boolean isFirst = true;
        if (key.indexOf("{") > 0) {
            key = key.replace("{", ":{");
            Pattern pattern = Pattern.compile("\\d+(\\.?[\\w]+)*");
            Matcher matcher = pattern.matcher(key);
            while (matcher.find()) {
                String tmp = matcher.group();
                String express[] = matcher.group().split("\\.");
                String i = express[0];
                int index = Integer.parseInt(i) - 1;
                Object value = arguments[index];
                if (parameterTypes[index].isAssignableFrom(List.class)) {
                    List result = (List) arguments[index];
                    value = result.get(0);
                }
                if (value == null || value.equals("null"))
                    value = "";
                if (express.length > 1) {
                    for (int idx = 1; idx < express.length; idx ++) {
                        String field = express[idx];
                        value = ReflectionUtils.getFieldValue(value, field);
                    }
                }
                if (isFirst) {
                    key = key.replace("{" + tmp + "}", value.toString());
                } else {
                    key = key.replace("{" + tmp + "}", LINK + value.toString());
                }
            }
        }
        if (CacheScope.user.equals(scope) && StringUtils.isNotBlank(redisConfig.getUserKey())){
            ServletRequestAttributes servletContainer = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = servletContainer.getRequest();
            String accessToken = request.getHeader(redisConfig.getUserKey());
            if(StringUtils.isBlank(accessToken)){
                accessToken = WebUtil.getCookieValue(request,redisConfig.getUserKey());
            }
            if (StringUtils.isNotBlank(accessToken)){
                key = key + accessToken;
            }
        }
        return key;
    }

    @Override
    public IUserKeyGenerator getUserKeyGenerator() {
        return userKeyGenerator;
    }

}
