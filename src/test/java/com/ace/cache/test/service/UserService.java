package com.ace.cache.test.service;

import com.ace.cache.test.entity.User;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Ace on 2017/5/21.
 */
public interface UserService {
    public User get(String account);
    public List<User> getLlist();
    public Set<User> getSet();
    public Map<String,User> getMap();
    public void remove(User user);
}
