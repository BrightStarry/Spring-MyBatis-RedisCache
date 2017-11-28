package com.zx.springmybatis.dao.driver;

import com.google.common.base.CaseFormat;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Field;

/**
 * author:ZhengXing
 * datetime:2017/11/28 0028 14:19
 * 通用条件查询语言驱动
 */
public class CommonConditionLanguageDriver extends XMLLanguageDriver{
    /**
     * 重写父类方法,以便在条件查询时,将不为空属性,加入where条件,
     * 例如:
     *   select * from user
     *   <where>
     *      <if test="username != null">and username=#{username}</if>
     *      <if test="password != null">and password=#{password}</if>
     *   </where>
     * parameterType:mapper中方法接收的参数,如果参数有多个,其值为map,当参数为多个时,无法获悉每个参数的类型(应该是)
     */
    @Override
    public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {
        //追加where
        StringBuilder sql = new StringBuilder().append("<where>");
        //默认将该参数类型作为实体类类型处理,获取所有属性
        Field[] fields = parameterType.getDeclaredFields();

        //遍历实体类的每个属性
        for (Field field : fields) {
            //将java中 userId形式的属性转换为数据库中 user_id形式的
            String sqlField = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName());
            //循环增加的语句
            String temp = "<if test=\"javaField != null\">and sqlField=#{javaField} </if>";
            //将字符串中的自定义标识字符:javaField和sqlField替换
            temp = temp.replaceAll("javaField",field.getName())
                    .replaceAll("sqlField", sqlField);
            sql.append(temp);
        }
        sql.append("</where>");

        //增加<script>标签,表示该sql需要解析
        script = "<script>" + script +  sql.toString() + "</script>";
        //继续执行父类的方法实现,构建SqlSource
        return super.createSqlSource(configuration, script, parameterType);
    }
}
