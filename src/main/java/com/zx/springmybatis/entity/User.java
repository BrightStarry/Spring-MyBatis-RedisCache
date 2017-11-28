package com.zx.springmybatis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

/**
 * author:Administrator
 * datetime:2017/11/7 0007 09:10
 */

@Data
@Builder
@Table
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class User {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;
    private String name;
    private String password;
    private String loginAddress;
    private Long gradeId;
}
