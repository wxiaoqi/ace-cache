package com.ace.cache.test.service.impl;

import com.ace.cache.test.entity.User;
import com.ace.cache.test.service.UserService;
import com.ace.cache.annotation.Cache;
import com.ace.cache.annotation.CacheClear;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by Ace on 2017/5/21.
 */
@Service
public class UserServiceImpl implements UserService {
    private Logger log = Logger.getLogger(UserServiceImpl.class);
    @Override
    @Cache(key="user{1}")
    public User get(String account) {
        log.debug("从方法内读取....");
        User user = new User("Ace",24,account);
        return user;
    }

    @Override
    @Cache(key="user:list")
    public List<User> getLlist() {
        log.debug("从方法内读取....");
        List<User> users = new ArrayList<User>();
        for(int i=0;i<20;i++) {
            User user = new User("Ace", i, "ace");
            users.add(user);
        }
        return users;
    }

    @Override
    @Cache(key="user:set")
    public Set<User> getSet() {
        log.debug("从方法内读取....");
        Set<User> users = new HashSet<User>();
        for(int i=0;i<20;i++) {
            User user = new User("Ace", i, "ace");
            users.add(user);
        }
        return users;
    }

    @Override
    @Cache(key="user:map")
    public Map<String, User> getMap() {
        log.debug("从方法内读取....");
        return null;
    }

    @Override
    @CacheClear(key="user{1.account}")
    public void remove(User user) {

    }

}
