package com.zx.springmybatis.dao;


import com.zx.springmybatis.config.mybatis.CommonMapper;
import com.zx.springmybatis.entity.Grade;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public interface GradeMapper extends CommonMapper<Grade> {


    /**
     * 最原始批量增加
     */
    @Insert("<script>INSERT INTO grade(name) values " +
            "<foreach collection=\"list\" item=\"item\" index=\"index\" separator=\",\">" +
            "(#{item.name})" +
            "</foreach></script>")
    void addAll(List<Grade> grades);

    /**
     * 使用Provider批量增加
     */
    @InsertProvider(type = Provider.class,method = "batchInsert")
    void addAll1(List<Grade> list);

    /**
     * 使用内部类作为Provider
     */
    class Provider{
        /**
         * 返回String作为sql语句
         * 不使用SQL构建器
         * 此处的sql是原生sql
         *
         * 参数: map中存储了MyBatisMapper方法中的参数;
         * 如果方法只有一个参数,也可以直接写相同类型的参数直接接收;
         * 如果方法使用了@Param注解,则使用map用@Param的value作为key接收
         * 如果多个参数,且未使用@Param注解,则使用map,用索引作为key接收
         * 具体可以下断点自行查看map
         */
        public String batchInsert(Map map) {
            List<Grade> list = (List<Grade>) map.get("list");
            StringBuilder result = new StringBuilder();
            result.append("INSERT INTO grade(name) VALUES");
            list.stream().forEach(item ->{
                result.append("(").append("\"" + item.getName() + "\"").append(")");
                result.append(",");
            });
            result.deleteCharAt(result.length()-1);
            return  result.toString();
        }
    }

}