package com.zx.springmybatis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * author:Administrator
 * datetime:2017/11/7 0007 09:10
 */

@Data
@Builder
@Table
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;
    private String name;
    private String password;
    private String loginAddress;
}
