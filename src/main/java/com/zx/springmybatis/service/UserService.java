package com.zx.springmybatis.service;

import com.zx.springmybatis.entity.User;

import java.util.List;

/**
 * author:Administrator
 * datetime:2017/11/7 0007 10:13
 */
public interface UserService {
    List<User> getAll();
}
