package com.rabbitmq.consumer;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;


/**
 * created by wangzexu on 4/7/21
 */
public class Consumer {
    private static final String EXCHANGE = "EXCHANGE_NAME";
    private static final String QUEUE = "QUEUE_NAME";


    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        //设置工厂的 IP 用户名 密码 虚拟机
        connectionFactory.setHost("172.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setVirtualHost("/");

        //获取资源 结束代码时释放资源
        //连接connection Chanel
        Connection connection;
        Channel channel;

        try {
            connection = connectionFactory.newConnection();
            channel = connection.createChannel();

            //声明队列
            channel.queueDeclare(QUEUE,true,false,false,null);
            //定义消费方法
            DefaultConsumer defaultConsumer = new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String exchange = envelope.getExchange();
                    String routingKey = envelope.getRoutingKey();
                    long deliveryTag = envelope.getDeliveryTag();
                    String message = new String(body, StandardCharsets.UTF_8);
                    System.out.println("交换机:"+exchange+"  路由："+routingKey+"  递交标签："+deliveryTag+" \n 消息："+message);
                }
            };

            /**
             * 监听队列String queue, boolean autoAck,Consumer callback
             * 参数明细
             * 1、队列名称
             * 2、是否自动回复，设置为true为表示消息接收到自动向mq回复接收到了，mq接收到回复会删除消息，设置
             为false则需要手动回复
             * 3、消费消息的方法，消费者接收到消息后调用此方法 */
            channel.basicConsume(QUEUE, true, defaultConsumer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

    }
}
