package com.test.producer;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * created by wangzexu on 4/10/21
 */
public class ProducerTopic {
    private static final String QUEUE_INFORM_EMAIL = "queue_inform_email";
    private static final String QUEUE_INFORM_SMS = "queue_inform_email";
    private static final String EXCHANGE_TOPICS_INFORM = "exchange_topics_inform";

    public static void main(String[] args) {
        Connection connection = null;
        Channel channel = null;

        try{
            //创建一个连接工厂  工厂模式的左作用就是对于多个重复需要的对象进行 工厂创建 配置一次多次使用
            ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.setHost("172.0.0.1");
            connectionFactory.setPort(5672);
            connectionFactory.setUsername("guest");
            connectionFactory.setPassword("guest");
            connectionFactory.setVirtualHost("/");



            connection = connectionFactory.newConnection();
            channel = connection.createChannel();

            //声明队列
            //声明交换机
            //绑定交换机，并通配符
            channel.exchangeDeclare(EXCHANGE_TOPICS_INFORM, BuiltinExchangeType.TOPIC);
            /**
             * 名称
             * 是否持久化
             * 是否独占此队列
             * 队列不用是否自动删除
             * 参数
             */
            channel.queueDeclare(QUEUE_INFORM_SMS, true, false, false, null);
            channel.queueDeclare(QUEUE_INFORM_EMAIL, true, false, false, null);

            //发送邮件
            for (int i = 0; i < 10; i++) {
                channel.basicPublish(EXCHANGE_TOPICS_INFORM,"inform.email",null,("我爱你 通过邮件发送行吗？"+i).getBytes(StandardCharsets.UTF_8));
                System.out.println(i + "  我爱你 用邮件发送行吗？");

            }
            for (int i = 0; i < 10; i++) {
                channel.basicPublish(EXCHANGE_TOPICS_INFORM,"inform.sms",null,("我爱你 用短信是不是很贵，男人爱你就舍得给你花钱，是真的吗  ，").getBytes(StandardCharsets.UTF_8));
                System.out.println(" 用短信爱你？"+i);
            }

            for (int i = 0; i < 10; i++) {
                channel.basicPublish(EXCHANGE_TOPICS_INFORM,"inform.email.sms",null,("我爱你，我不管是短信 或是 email 我都要联系到你，因为我不能失去你？").getBytes());
                System.out.println("爱你还在乎形式吗？");
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

}
