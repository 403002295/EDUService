package com.xuecheng.test.freemarker.rabbitmq.consumer;

import com.xuecheng.test.freemarker.config.RabbitMQConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.channels.Channel;

/**
 * created by wangzexu on 4/12/21
 */
@Component
public class ReceiveHandler {

    //监听email
    @RabbitListener(queues = {RabbitMQConfig.QUEUE_INFORM_EMAIL})
    public void receive_email(String msg, Message message, Channel channel) {
        System.out.println(msg);
        System.out.println(message.toString());
    }
    //监听email
    @RabbitListener(queues = {RabbitMQConfig.QUEUE_INFORM_SMS})
    public void receive_sms(String msg, Message message, Channel channel) {
        System.out.println(msg);
        System.out.println(message.toString());
    }
}
