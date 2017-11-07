package com.zx.springmybatis.dao;

import com.zx.springmybatis.entity.User;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * author:Administrator
 * datetime:2017/11/7 0007 10:12
 * 用户mapper
 */
@Repository
public interface UserMapper {
    @Select("select * from user")
    List<User> getAll();
}
