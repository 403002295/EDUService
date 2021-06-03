package com.xuecheng.client_cms.configuration;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * created by wangzexu on 4/29/21
 *
 *  配置rabbitMQ  接受消息从mongoDB 中取数据的服务 所以配置监听
 */
@Configuration
public class RabbitMQConfig {

    //对列名称
    public static final String QUEUE_CMS_POSTPAGE = "queue_cms_postpage";
    //交换机名称
    public static final String EX_ROUTING_CMS_POSTPAGE = "ex_routing_cms_postpage";

    //队列名称
    @Value("${xuecheng.mq.queue}")
    public String queue_cms_postpage_name;

    //routing key
    @Value("${xuecheng.mq.routingKey}")
    public String routingkey;


    /**
     * 声明 交换机 名称 配置文件中国的队列名称
     * @return
     */
    @Bean(QUEUE_CMS_POSTPAGE)
    public Queue QUEUE_CMS_POSTPAGE() {
        Queue queue = new Queue(queue_cms_postpage_name);
        return queue;
    }

    /**
     * 声明交换机
     * @return
     */
    @Bean(EX_ROUTING_CMS_POSTPAGE)
    public Exchange EX_ROUTING_CMS_POSTPAGE() {

        return ExchangeBuilder.directExchange(EX_ROUTING_CMS_POSTPAGE).durable(true).build();

    }

    /**
     * 绑定队列到交换机
     * @param exchange 交换机
     * @param queue  队列
     * @return 绑定对象
     */
    @Bean
    public Binding BINDING_QUEUE_INFORM_SMS(@Qualifier(EX_ROUTING_CMS_POSTPAGE) Exchange exchange,@Qualifier(QUEUE_CMS_POSTPAGE) Queue queue) {

        return BindingBuilder.bind(queue).to(exchange).with(routingkey).noargs();
    }







}
