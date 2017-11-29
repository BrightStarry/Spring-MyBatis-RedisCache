package com.zx.springmybatis.config.cache;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import java.util.Arrays;


/**
 * author:ZhengXing
 * datetime:2017/11/29 0029 13:32
 * redis缓存配置类
 *
 * CachingConfigurerSupport该类使用空方法实现了CachingConfigurer接口,
 * 子类只需要实现想要自定义的方法即可配置 缓存管理器/主键生成器/缓存解析器/异常处理器等;
 * 如果不实现该接口,配置该类后,还需在注解中指定对应的keyGenerator才能生效
 *
 */
@Configuration
public class RedisCacheConfig  extends CachingConfigurerSupport{

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    /**
     * 匿名内部类构建主键生成器
     * 其参数分别为 调用缓存的类(service)/调用缓存的方法/方法的参数列表
     */
    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return (object,method,params)->{
            //类名:方法名:参数[0]参数[1]...
            StringBuilder key = new StringBuilder(object.getClass().getSimpleName() + "-" + method.getName() + ":");
            for (Object param : params) {
                //直接追加,只要该参数是基本类型或实现了toString方法,就没问题,否则会显示xx@hashcode那种类型的字符
                //如果参数过多,需要自定义key
                key.append(param.toString());
            }
            return key.toString();
        };
    }

    /**
     * 配置RedisTemplate
     * 是为了替换默认的JDK的序列化器,使用默认的序列化器,key会乱码
     */
    @Bean
    public RedisTemplate redisTemplate() {
        //创建StringRedis模版
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate(redisConnectionFactory);
        // 使用Jackson2JsonRedisSerialize 替换默认序列化
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        //value使用jackJson序列化,key使用string序列化,string序列化不支持list等类型
//        stringRedisTemplate.setKeySerializer(new StringRedisSerializer());//不需要该设置,key也不会乱码.
        stringRedisTemplate.setValueSerializer(jackson2JsonRedisSerializer);

        //InitializingBean接口提供的一个方法,在spring容器属性被初始化完成后再调用该方法
        stringRedisTemplate.afterPropertiesSet();

        return  stringRedisTemplate;
    }

    /**
     * 创建缓存管理器
     * 主要为了自定义若干cacheNames和缓存过期时间
     */
    @Bean
    @Override
    public CacheManager cacheManager() {
        String[] cacheNames = {"a","b"};
        RedisCacheManager redisCacheManager = new RedisCacheManager(redisTemplate(), Arrays.asList(cacheNames));
        redisCacheManager.setDefaultExpiration(86400);
        return redisCacheManager;
    }
}
