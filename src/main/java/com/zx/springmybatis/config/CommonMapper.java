package com.zx.springmybatis.config;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * author:ZhengXing
 * datetime:2017/11/16 0016 15:16
 * 通用Mapper顶层接口，所有mapper需要继承他
 */
public interface CommonMapper<T> extends Mapper<T>,MySqlMapper<T> {
}
