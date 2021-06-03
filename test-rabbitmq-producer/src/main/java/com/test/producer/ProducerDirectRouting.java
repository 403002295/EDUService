package com.test.producer;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * created by wangzexu on 4/9/21
 */
public class ProducerDirectRouting {
    private static final String QUEUE_INFORM_EMAIL = "queue_routing_email";
    private static final String QUEUE_INFORM_SMS = "queue_routing_sms";
    private static final String EXCHANGE_ROUTING_INFORM="exchange_routing_inform";

    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setVirtualHost("/");
        connectionFactory.setHost("172.0.0.1");
        if (args.length > 0) {
            connectionFactory.setHost(args[0]);
        }
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setVirtualHost("/");

        Connection connection = null;
        Channel channel = null;

        try {
            connection = connectionFactory.newConnection();
            channel = connection.createChannel();

            //声明交换机
            channel.exchangeDeclare(EXCHANGE_ROUTING_INFORM, BuiltinExchangeType.DIRECT);
            //生命队列
            channel.queueDeclare(QUEUE_INFORM_SMS,true,false,false,null);
            channel.queueDeclare(QUEUE_INFORM_EMAIL,true,false,false,null);

            //绑定交换机
            /**
             * 1、队列名称
             * 2、交换机名称
             * 3、路由Key
             */
            channel.queueBind(QUEUE_INFORM_SMS, EXCHANGE_ROUTING_INFORM,QUEUE_INFORM_SMS);
            channel.queueBind(QUEUE_INFORM_EMAIL, EXCHANGE_ROUTING_INFORM,QUEUE_INFORM_EMAIL);


            //发送消息
            Channel finalChannel = channel;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    long i = 0;
                    while (true){
                        try {
                            System.out.println(i++);
                            finalChannel.basicPublish(EXCHANGE_ROUTING_INFORM,QUEUE_INFORM_EMAIL,null,(i+"  this is email ,the routingkey is "+QUEUE_INFORM_EMAIL).getBytes(StandardCharsets.UTF_8));
                            finalChannel.basicPublish(EXCHANGE_ROUTING_INFORM,QUEUE_INFORM_SMS,null,(i+"  this is email ,the routingkey is "+QUEUE_INFORM_SMS).getBytes(StandardCharsets.UTF_8));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            new Thread(runnable).start();
            new Thread(runnable).start();

            System.out.println("--------=========== --------");





        } catch (Exception e) {

        }
    }
}
