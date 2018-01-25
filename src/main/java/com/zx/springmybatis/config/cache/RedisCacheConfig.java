package com.zx.springmybatis.config.cache;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import java.util.*;


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

    //Spring构造的redis连接工厂
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    //自定义的用来读取yml文件中每个缓存名对应的缓存过期时间的属性类
    @Autowired
    private CustomRedisCacheExpireProperties customRedisCacheExpireProperties;

    /**
     * 匿名内部类构建主键生成器
     * 其参数分别为 调用缓存的类(service)/调用缓存的方法/方法的参数列表
     */
    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return (object,method,params)->{
            //类名:方法名:参数[0]参数[1]...
            StringBuilder key = new StringBuilder(object.getClass().getSimpleName() + "-" + method.getName() + "-");
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
     * 是为了替换默认的JDK的序列化器,使用默认的序列化器,key会乱码;
     *
     * 此处在Spring中的实现是,他有一个默认的RedisTemplate Bean,但使用了
     * @ConditionalOnMissingBean(type = RedisTemplate.class)这样一个注解,
     * 表示在我们没有配置自定义的bean的情况下,才使用它默认的bean
     */
    @Bean
    public RedisTemplate redisTemplate() {
        //创建StringRedis模版
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate(redisConnectionFactory);
        // 使用Jackson2JsonRedisSerialize 替换默认序列化
        Jackson2JsonRedisSerializer<?> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
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
     * 主要为了自定义若干cacheNames和缓存过期时间;
     *
     * 自定义该类后,如果缓存注解中使用了一个未配置的缓存名,并且,该类的一个dynamic属性为true,
     * 就会生成一个新的以该名字为名的{@link Cache}对象,放入集合;
     * 但如果给该缓存管理器配置了cacheNames(也就是调用了setCacheNames()方法),该dynamic属性就会被
     * 设置为false,将无法动态加入缓存名;那么就会抛出无法找到该缓存的异常;
     * 我觉得还是设置上比较好.
     */
    @Bean
    @Override
    public CacheManager cacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager(redisTemplate());
        //默认的过期时间,会被每个缓存名自己的过期时间覆盖
        redisCacheManager.setDefaultExpiration(3600);
        /**
         * 启动时加载远程缓存; 不开启:每次第一次查询即使缓存中已经有旧的缓存,也不会读取到;
         * 开启后如果缓存中已有缓存,第一次查询就会从缓存中读取
         */
        redisCacheManager.setLoadRemoteCachesOnStartup(true);
        //开启后,key会携带上cacheName作为前缀
        redisCacheManager.setUsePrefix(true);
        /**
         * 设置cacheNames,也可以在构造函数中设置,此处我使用在yml配置的cacheNames即可
         * 需要注意的是,显而易见,此处的RedisCacheManager还未注入yml中的cacheNames;
         * 所以如果使用redisCacheManager.getCacheNames()取出的将是空的;
         * 但是,如果使用setExpires()方法,设置好对应的cacheName和过期时间,还是能够生效的
         */
//        redisCacheManager.setCacheNames(Arrays.asList(cacheNames));
//        Collection<String> cacheNames = redisCacheManager.getCacheNames();

        //使用自定义的属性类,根据yml配置,生成缓存名和过期时间对应的map
        Map<String, Long> expires = customRedisCacheExpireProperties.generateExpireMap();
        //设置每个缓存对应的过期时间
        redisCacheManager.setExpires(expires);
        //给缓存管理器设置上缓存名s
        redisCacheManager.setCacheNames(customRedisCacheExpireProperties.getCacheNames());


        return redisCacheManager;
    }

    /**
     * 自定义缓存异常处理器.
     * 该CacheErrorHandler接口只有一个实现类SimpleCacheErrorHandler.只是抛出了所有异常未做任何处理
     *  有若干个方法,分别处理获取/修改/放入/删除缓存异常.
     *  若有需要.可自定义实现,比如因为缓存不是必须的,那就可以只做日志记录,不再抛出异常
     *
     */
    @Bean
    @Override
    public CacheErrorHandler errorHandler() {
       return  new SimpleCacheErrorHandler(){
            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                super.handleCacheGetError(exception, cache, key);
            }
        };
    }

    /**
     * 自定义缓存解析器(该类必须是线程安全的)
     * 解析该缓存由哪些(可以多个) cacheNames处理
     *
     * 其默认实现是SimpleCacheResolver
     *
     */
    @Override
    public CacheResolver cacheResolver() {
        return super.cacheResolver();
    }
}
