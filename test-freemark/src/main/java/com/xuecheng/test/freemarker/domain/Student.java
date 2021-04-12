package com.xuecheng.test.freemarker.domain;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * created by wangzexu on 2021/3/8
 */
@Data
public class Student {

    private String name;
    private int age;
    private float mondy;
    private Date birthday;
    private List<Student> friends;
    private Student bestFriend;


}
