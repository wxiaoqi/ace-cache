package com.ace.cache.test.service;

import com.ace.cache.test.entity.User;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Ace on 2017/5/21.
 */
public interface UserService {

    User get(String account);

    List<User> getLlist();

    Set<User> getSet();

    Map<String, User> getMap();

    void save(User user);

    User get(int age);

    void biz(String account);

    int getAge(String account);

    String getName(String account);
}
