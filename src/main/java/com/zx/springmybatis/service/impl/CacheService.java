package com.zx.springmybatis.service.impl;

import com.zx.springmybatis.dao.GradeMapper;
import com.zx.springmybatis.dao.UserMapper;
import com.zx.springmybatis.dto.GradeDTO;
import com.zx.springmybatis.entity.Grade;
import com.zx.springmybatis.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

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
    @Caching
    public List<Grade> findAll() {
        log.info("查询所有班级");
        return gradeMapper.selectAll();
    }

    /**
     * 修改某个班级
     * 修改班级后,清空findAll方法的缓存
     */
    @CacheEvict(key = "'CacheService-findAll-'")
    public void updateGradeById(Long gradeId,Grade grade) {

        Example example = new Example(Grade.class);
        example.createCriteria().andEqualTo("id", gradeId);

        int i = gradeMapper.updateByExampleSelective(grade, example);
        //根据id直接更新
        //gradeMapper.updateByExampleSelective();
        System.out.println("更新条数:" + i);
    }

    /**
     * 查询单个班级,根据id
     */
    @Cacheable
    public Grade findOneByGradeId(Long gradeId) {
        return gradeMapper.selectByPrimaryKey(gradeId);
    }

    /**
     * 增加班级方法.
     * 插入时将其直接存入缓存;
     * 注意,这样就需要返回该对象整个
     * 关于id,此处已经自动回写了
     */
    @CachePut(key = "'CacheService-findOneByGradeId-' + #result.id")
    public Grade insertGrade(Grade grade) {
        gradeMapper.insert(grade);
        return grade;
    }


    /**
     * 查询某个班级和班级下所有学生
     */
    @Cacheable(cacheNames = "c")
    public GradeDTO findGradeForUsers(Long gradeId) {
        log.info("查询班级和其下所有学生");
        Grade grade = gradeMapper.selectByPrimaryKey(gradeId);
        List<User> users = userMapper.select(new User().setGradeId(gradeId));
        return new GradeDTO(grade, users);
    }


}
