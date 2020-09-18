package com.ace.cache.test.rest;

import com.ace.cache.test.entity.User;
import com.ace.cache.test.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wanghaobin
 * @create 2020/9/16.
 */
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/user")
    public User getUser(){
        return userService.get("test");
    }
}
