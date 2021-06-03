package com.rabbitmq.consumer;

import com.rabbitmq.client.*;
import com.rabbitmq.client.Consumer;

import java.io.IOException;

/**
 * created by wangzexu on 4/9/21
 */
public class ConsumerFanoutPublishSubscribeEmail {
    private static final String QUEUE_INFORM_EMAIL = "queue_inform_email";
    private static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    private static final String EXCHANGE_FANOUT_INFORM="exchange_fanout_inform";

    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("172.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setVirtualHost("/");

        Connection connection = null;
        Channel channel = null;

        try {
            connection = connectionFactory.newConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_FANOUT_INFORM, BuiltinExchangeType.FANOUT);
            channel.queueDeclare(QUEUE_INFORM_SMS, true, false, false, null);
            channel.queueDeclare(QUEUE_INFORM_EMAIL, true, false, false, null);

            channel.queueBind(QUEUE_INFORM_SMS, EXCHANGE_FANOUT_INFORM, "");
            channel.queueBind(QUEUE_INFORM_EMAIL, EXCHANGE_FANOUT_INFORM, "");

            Consumer defaultConsumer = new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    long deliveryTag = envelope.getDeliveryTag();
                    String exchange = envelope.getExchange();
                    String routingKey = envelope.getRoutingKey();
                    System.out.println(properties.toString());
                    System.out.println("交换机:"+exchange+"  路由："+routingKey+"  递交标签："+deliveryTag+" \n 消息："+new String(body,"utf-8"));
                }
            };
            String s = channel.basicConsume(QUEUE_INFORM_EMAIL, true,defaultConsumer );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
