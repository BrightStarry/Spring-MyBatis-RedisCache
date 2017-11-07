package com.zx.springmybatis.entity;

import lombok.Data;

/**
 * author:Administrator
 * datetime:2017/11/7 0007 09:10
 */

@Data
public class User {

    private Long id;
    private String name;
    private String password;
    private String loginAddress;
}
