package com.zx.springmybatis.dto;

import com.zx.springmybatis.entity.Grade;
import com.zx.springmybatis.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * author:ZhengXing
 * datetime:2017/11/29 0029 11:28
 * 班级信息和班级下所有学生
 */
@Data
@Builder
@AllArgsConstructor
@Accessors(chain = true)
public class GradeDTO implements Serializable {
    private Grade grade;
    private List<User> users;
}
