package com.xuecheng.manage_course;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * created by wangzexu on 5/10/21
 */
@SpringBootApplication
@EntityScan("com.xuecheng.framework.domain.course")//扫描实体类
@ComponentScan(basePackages={"com.xuecheng.api"})//扫描接口
@ComponentScan(basePackages={"com.xuecheng.manage_course"})
@ComponentScan(basePackages={"com.xuecheng.framework"})//扫描common下的所有类
//@EnableAutoConfiguration(exclude={DruidDataSourceAutoConfigure.class})
//@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class CourseSpringbootApplication {
    public static void main(String[] args) {
        SpringApplication.run(CourseSpringbootApplication.class, args);
    }
}
