package com.ace.cache.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Created by Ace on 2017/5/21.
 */
@SpringBootApplication
@ComponentScan({"com.ace.cache"})
@EnableAspectJAutoProxy
public class CacheTest {
    public static  void main(String args[]){
        SpringApplication app = new SpringApplication(CacheTest.class);
        app.run(args);
    }

}
