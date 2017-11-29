package com.zx.springmybatis.dao;

import com.zx.springmybatis.entity.Grade;
import com.zx.springmybatis.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.ArrayUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * author:ZhengXing
 * datetime:2017/11/16 0016 15:58
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private GradeMapper gradeMapper;

    @Test
    public void test() {
        User temp = User.builder().id(1L).build();

        Example example = new Example(User.class)//传入实体类对象构造
                .selectProperties("id", "name")//设置要查询的字段
                .excludeProperties("id");//设置不查询的字段,与要查询同时设置，要查询的优先
        example.orderBy("id").desc();//排序
        example.createCriteria();//其他方法类似，基本都能用方法名理解
//                .andLessThan("id","4");//查询属性小于该值的记录
//                .andGreaterThan("id","4");//查询属性大于该值的记录
//                .andAllEqualTo(temp);//查询字段值等于该对象的属性值的记录，所有属性。
//                .andEqualTo(temp);//查询字段值等于该对象的属性值的记录，非空属性。
//                .andBetween("name","a","c");//between查询
//                .andCondition("name = 'a' or name ='b'");//可以直接使用sql查询，此处输入where后面的字符

        List<User> userList = userMapper.selectByExample(example);
        userList.forEach(user -> {
            log.info("user:{}", user);
        });
    }

    @Test
    public void test1() {
        List<Grade> grades = Arrays.asList(new Grade("a"), new Grade("b"), new Grade("c"));
        gradeMapper.addAll(grades);
    }

    @Test
    public void test2() {

        User user = new User().setName("a").setPassword("aa");
        List<User> a = userMapper.findByCondition(user);
        a.forEach(item -> System.out.println(a));
    }

    @Test
    public void test3() {
        User a = userMapper.a(1L);
        System.out.println(a);
    }

    /**
     * 测试redis
     */
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Test
    public void test4(){
        redisTemplate.opsForValue().set("a","test");
    }


}