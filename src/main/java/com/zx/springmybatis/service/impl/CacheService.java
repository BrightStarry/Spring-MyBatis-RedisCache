package com.zx.springmybatis.service.impl;

import com.zx.springmybatis.dao.GradeMapper;
import com.zx.springmybatis.dao.UserMapper;
import com.zx.springmybatis.dto.GradeDTO;
import com.zx.springmybatis.entity.Grade;
import com.zx.springmybatis.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * author:ZhengXing
 * datetime:2017/11/29 0029 10:27
 *
 * 缓存测试类
 */
@Service
@Slf4j
@CacheConfig(cacheNames = "a")
public class CacheService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private GradeMapper gradeMapper;

    /**
     * 查询所有班级
     * 注意,@Cacheable中的cacheNames值需要在yml中配置,也就是spring.cache.cache-names
     * 如果需要指定一个特别的固定的key,需要使用单引号包裹
     */
    @Cacheable
    public List<Grade> findAll() {
        log.info("查询所有班级");
        return gradeMapper.selectAll();
    }

    /**
     * 查询某个班级和班级下所有学生
     */
    @Cacheable
    public GradeDTO findGradeForUsers(Long gradeId) {
        log.info("查询班级和其下所有学生");
        Grade grade = gradeMapper.selectByPrimaryKey(gradeId);
        List<User> users = userMapper.select(new User().setGradeId(gradeId));
        return new GradeDTO(grade, users);
    }


}
