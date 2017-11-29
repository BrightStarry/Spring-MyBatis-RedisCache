package com.zx.springmybatis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

/**
 * author:ZhengXing
 * datetime:2017/11/28 0028 09:34
 * 班级
 */
@Data
@Builder
@Table
@NoArgsConstructor
@AllArgsConstructor
public class Grade implements Serializable{
    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;
    private String name;

    public Grade(String name) {
        this.name = name;
    }
}
