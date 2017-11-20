package com.ace.cache.test;

import com.ace.cache.EnableAceCache;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by Ace on 2017/5/21.
 */
@SpringBootApplication
@EnableAceCache
public class CacheTest {
    public static void main(String args[]) {
        SpringApplication app = new SpringApplication(CacheTest.class);
        app.run(args);
    }

}
