package com.test;

import com.xuecheng.test.freemarker.FreemarkerTestApplication;

import com.xuecheng.test.freemarker.config.RabbitMQConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * created by wangzexu on 4/12/21
 */
@SpringBootTest(classes = FreemarkerTestApplication.class)
@RunWith(SpringRunner.class)
public class RabbitMQTest {


    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    public void testSendByTopics(){
        for (int i = 0; i < 100; i++) {
            String message = "sms email inform to user "+i;
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_TOPICS_INFORM, "inform.email", message);
            System.out.println("Send Message is "+message);
        }
    }
}
