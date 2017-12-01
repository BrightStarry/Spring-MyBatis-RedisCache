package com.zx.springmybatis.config.cache;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author:ZhengXing
 * datetime:2017/12/1 0001 12:46
 * 自定义的redis缓存中的过期时间属性
 */
@Data
@ConfigurationProperties(prefix = "spring.cache")
@Component
public class CustomRedisCacheExpireProperties {
    //该属性在spring cache框架自己的类中也会被获取
    //此处获取是为了对长度进行校验,防止 缓存名字 - 缓存时间 没有一一匹配
    private List<String> cacheNames;

    //缓存时间,和缓存名一一对应
    private List<Long> cacheExpires;

    /**
     * 生成Map,用来放入RedisManager中
     */
    public Map<String, Long> generateExpireMap() {
        Map<String, Long> expireMap = new HashMap<>();
        /**
         * 校验参数值
         */
        //如果未配置cacheNames属性,返回空map
        //如果未配置cacheExpires属性,也返回空map
        if (CollectionUtils.isEmpty(cacheNames) || CollectionUtils.isEmpty(cacheExpires))
            return expireMap;
        //长度校验:只要数组不为空,有x个cacheNames,就需要x个cacheExpires,如果某个name无需缓存时间,设置为0即可
        //其内部实现就是使用该Map生成若干个RedisCacheMetadata,该对象和cacheName一一对应,并且其中的默认过期时间就是0
        //不对.我在redis中试了下,将key过期时间设为0或负数,该key会直接过期.
        //找了很久..没找到其判断过期时间的代码
        if(cacheNames.size() != cacheExpires.size())
            //此处随便抛出一个非法状态异常,可自定义异常抛出
            throw new IllegalStateException("cacheExpires设置非法.cacheNames和cacheExpires长度不一致");
        //遍历cacheNames
        for (int i = 0; i < cacheNames.size(); i++) {
            //只有当cacheExpires设置的大于0时,才放入map,否则就使用默认的过期时间
            long expire = cacheExpires.get(i);
            if (expire > 0)
                expireMap.put(cacheNames.get(i),expire);
        }
        return expireMap;
    }
}
