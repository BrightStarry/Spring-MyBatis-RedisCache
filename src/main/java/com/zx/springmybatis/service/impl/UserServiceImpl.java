package com.zx.springmybatis.service.impl;

import com.zx.springmybatis.dao.UserMapper;
import com.zx.springmybatis.entity.User;
import com.zx.springmybatis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * author:Administrator
 * datetime:2017/11/7 0007 10:14
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public List<User> getAll() {
        return userMapper.getAll();
    }
}
