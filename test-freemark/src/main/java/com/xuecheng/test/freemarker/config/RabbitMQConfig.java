package com.xuecheng.test.freemarker.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * created by wangzexu on 4/12/21
 */
@Configuration
public class RabbitMQConfig {
    public static final String QUEUE_INFORM_EMAIL = "queue_inform_email";
    public static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    public static final String EXCHANGE_TOPICS_INFORM="exchange_topics_inform";

    /**
     * 交换机位置
     * ExchangeBuilder 提供 fanout direct topic header 交换机类型的配置
     * @return the exchange
     */
    @Bean(EXCHANGE_TOPICS_INFORM)
    public Exchange EXCHANGE_TOPICS_INFORM(){
        return ExchangeBuilder.topicExchange(EXCHANGE_TOPICS_INFORM).build();
    }

    //声明队列
    @Bean(QUEUE_INFORM_SMS)
    public Queue QUEUE_INFROM_SMS(){
        Queue queue = new Queue(QUEUE_INFORM_SMS);
        return queue;
    }
    @Bean(QUEUE_INFORM_EMAIL)
    public Queue QUEUE_INFORM_EMAIL(){
        Queue queue = new Queue(QUEUE_INFORM_EMAIL);
        return queue;
    }

    /**
     * 绑定队列
     * @param queue 队列
     * @param exchange 交换机
     * @return 绑定
     */
    @Bean
    public Binding BINDING_QUEUE_INFORM_SMS(@Qualifier(QUEUE_INFORM_SMS)Queue queue,@Qualifier(EXCHANGE_TOPICS_INFORM)Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("inform.#.sms.#").noargs();

    }
    @Bean
    public Binding BING_QUEUE_INFORM_EMAIL(@Qualifier(QUEUE_INFORM_EMAIL)Queue queue,@Qualifier(EXCHANGE_TOPICS_INFORM)Exchange exchange){

        return BindingBuilder.bind(queue).to(exchange).with("inform.#.email.#").noargs();
    }

}
