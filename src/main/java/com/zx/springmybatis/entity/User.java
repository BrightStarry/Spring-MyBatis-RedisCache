package com.zx.springmybatis.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * author:Administrator
 * datetime:2017/11/7 0007 09:10
 */

@Data
@Table
public class User {
    @Id
    private Long id;
    private String name;
    private String password;
    private String loginAddress;
}
