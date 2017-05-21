package com.ace.cache.test.config;

import com.ace.cache.parser.IUserKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Ace on 2017/5/21.
 */
@Configuration
public class CacheConfig {
    @Bean
    IUserKeyGenerator getUserKeyGenerator(){
        return new IUserKeyGenerator() {
            @Override
            public String getCurrentUserAccount() {
                return "";
            }
        };
    }
}
