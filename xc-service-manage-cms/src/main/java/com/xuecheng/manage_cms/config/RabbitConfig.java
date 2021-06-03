package com.xuecheng.manage_cms.config;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * created by wangzexu on 4/29/21
 */
@Configuration
public class RabbitConfig {

    public static final String EX_ROUTING_CMS_POSTPAGE = "ex_routing_cms_postpage";

    @Bean
    public Exchange EXCHANGE_TOPICS_INFORM() {
        return ExchangeBuilder.directExchange(EX_ROUTING_CMS_POSTPAGE).durable(true).build();
    }





}
