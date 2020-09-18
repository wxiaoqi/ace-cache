package com.ace.cache.test.service.impl;

import com.ace.cache.annotation.Cache;
import com.ace.cache.annotation.CacheClear;
import com.ace.cache.annotation.CacheGlobalLock;
import com.ace.cache.parser.ICacheResultParser;
import com.ace.cache.test.cache.MyKeyGenerator;
import com.ace.cache.test.entity.User;
import com.ace.cache.test.service.UserService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by Ace on 2017/5/21.
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Override
    @Cache(key = "user{1}",desc = "用户信息缓存")
    public User get(String account) {
        log.debug("从方法内读取....");
        ((UserService) AopContext.currentProxy()).getAge(account);
        User user = new User("Ace", 24, account);
        return user;
    }

    @Override
    @Cache(key = "user:list")
    public List<User> getLlist() {
        System.out.println("从方法内读取....");
        log.debug("从方法内读取....");
        List<User> users = new ArrayList<User>();
        for (int i = 0; i < 20; i++) {
            User user = new User("Ace", i, "ace");
            users.add(user);
        }
        return users;
    }

    @Override
    @Cache(key = "user:set", parser = SetCacheResultParser.class)
    public Set<User> getSet() {
        log.debug("从方法内读取....");
        Set<User> users = new HashSet<User>();
        for (int i = 0; i < 20; i++) {
            User user = new User("Ace", i, "ace");
            users.add(user);
        }
        return users;
    }

    @Override
    @Cache(key = "user:map",parser = UserMapCacheResultParser.class)
    public Map<String, User> getMap() {
        log.debug("从方法内读取....");
        Map<String,User> users = new HashMap<String, User>();
        for (int i = 0; i < 20; i++) {
            User user = new User("Ace", i, "ace");
            users.put(user.getAccount() + i, user);
        }
        return users;
    }

    @Override
    @CacheClear(pre = "user")
    public void save(User user) {

    }

    @Override
    @Cache(key="user",generator = MyKeyGenerator.class)
    public User get(int age) {
        log.debug("从方法内读取....");
        User user = new User("Ace", age, "Ace");
        return user;
    }

    @Override
    @CacheGlobalLock(key="user_lock{1}")
    public void biz(String account) {
        log.debug("注解分布式锁...");
    }

    @Override
    @Cache(key="user:age{1}")
    public int getAge(String account) {
        log.debug("从方法内读取....");
        return 11;
    }

    @Override
    @Cache(key="user:name{1}")
    public String getName(String account) {
        log.debug("从方法内读取....");
        return "小郎君";
    }
    /**
     * 对map返回结果做处理
     *
     * @Created by Ace on 2017/5/22.
     */
    public static class UserMapCacheResultParser implements ICacheResultParser {
        @Override
        public Object parse(String value, Type returnType, Class<?>... origins) {
            return JSON.parseObject(value, new TypeReference<HashMap<String, User>>() {
            });
        }
    }
    /**
     * 对set返回结果做处理
     *
     * @Created by Ace on 2017/5/22.
     */
    public static class SetCacheResultParser implements ICacheResultParser {
        @Override
        public Object parse(String value, Type returnType, Class<?>... origins) {
//            origins[0]
            return JSON.parseObject(value, new TypeReference<HashSet<User>>() {
            });
        }
    }
}