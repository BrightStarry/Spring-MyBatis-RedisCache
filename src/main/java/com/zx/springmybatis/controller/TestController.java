package com.zx.springmybatis.controller;

import com.zx.springmybatis.entity.User;
import com.zx.springmybatis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * author:Administrator
 * datetime:2017/11/7 0007 10:16
 */
@RestController
@RequestMapping("/")
public class TestController {

    @Autowired
    private UserService userService;

    @GetMapping("/getAll")
    public List<User> getAll() {
        return userService.getAll();
    }
}
