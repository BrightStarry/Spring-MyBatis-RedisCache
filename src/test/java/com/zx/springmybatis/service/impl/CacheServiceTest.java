package com.zx.springmybatis.service.impl;

import com.zx.springmybatis.dto.GradeDTO;
import com.zx.springmybatis.entity.Grade;
import com.zx.springmybatis.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * author:ZhengXing
 * datetime:2017/11/29 0029 10:51
 * 缓存测试
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class CacheServiceTest {
    @Autowired
    private CacheService cacheService;

    @Test
    public void test1() {
        log.info("第一次查询");
        findAll();
        log.info("第二次查询");
        findAll();
    }

    @Test
    public void test2() {
        log.info("第一次查询");
        findGradeForUsers();
        log.info("第二次查询");
        findGradeForUsers();
    }

    private void findAll() {
        List<Grade> grades = cacheService.findAll();
        grades.forEach(item ->{
            System.out.println(grades);
        });
    }

    private void findGradeForUsers() {
        GradeDTO gradeDTO = cacheService.findGradeForUsers(1L);
        System.out.println(gradeDTO);
    }




}