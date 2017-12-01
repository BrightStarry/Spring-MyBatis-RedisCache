package com.zx.springmybatis.service.impl;

import com.sun.xml.internal.ws.api.model.CheckedException;
import com.zx.springmybatis.dto.GradeDTO;
import com.zx.springmybatis.entity.Grade;
import com.zx.springmybatis.entity.User;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    @SneakyThrows(InterruptedException.class)//lombok 消除受检异常
    public void test2() {
        log.info("第一次查询");
        findGradeForUsers();
        TimeUnit.SECONDS.sleep(2);
        log.info("第二次查询");
        findGradeForUsers();
    }


    @Test
    public void test3() {
        findAll();
        findAll();

        Grade grade = Grade.builder()
                .name("aaaaa").build();
        cacheService.updateGradeById(1L, grade);

//        findAll();
    }

    @Test
    public void test() {
        Grade grade1 = cacheService.insertGrade(Grade.builder()
                .name("xxxxxxxx").build());
        System.out.println("新增成功:" + grade1);
        Grade grade = cacheService.findOneByGradeId(grade1.getId());
        System.out.println(grade);
    }

    private void findAll() {
        List<Grade> grades = cacheService.findAll();
        grades.forEach(item -> {
            System.out.println(grades);
        });
    }

    private void findGradeForUsers() {
        GradeDTO gradeDTO = cacheService.findGradeForUsers(1L);
        System.out.println(gradeDTO);
    }


}