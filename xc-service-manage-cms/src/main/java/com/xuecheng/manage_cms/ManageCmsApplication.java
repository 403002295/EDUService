package com.xuecheng.manage_cms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * created by wangzexu on 2021/3/1
 */
@SpringBootApplication
@EntityScan("com.xuecheng.framework.domain.cms")// scan entity
@ComponentScan(basePackages = {"com.xuecheng.api"})// scan interface
@ComponentScan(basePackages = {"com.xuecheng.manage_cms"})// scan all class in this project
@ComponentScan(basePackages = "com.xuecheng.framework") //扫描common工程下的类
public class ManageCmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ManageCmsApplication.class, args);

    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(new OkHttp3ClientHttpRequestFactory());
    }


}
