package com.rabbitmq.consumer;

import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * created by wangzexu on 4/11/21
 */
public class ConsumerTopicsEmail {
    private static final String QUEUE_INFORM_EMAIL = "queue_inform_email";
    private static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    private static final String EXCHANGE_TOPICS_INFORM = "exchange_topics_inform";

    public static void main(String[] args) {
        Connection connection = null;
        Channel channel = null;

        try{
            //创建一个连接工厂  工厂模式的左作用就是对于多个重复需要的对象进行 工厂创建 配置一次多次使用
            ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.setHost("192.168.1.135");
            connectionFactory.setPort(5672);
            connectionFactory.setUsername("mark");
            connectionFactory.setPassword("mark");
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

            channel.queueDeclare(QUEUE_INFORM_EMAIL, true, false, false, null);

            channel.queueBind(QUEUE_INFORM_EMAIL, EXCHANGE_TOPICS_INFORM, "inform.#.email.#");

            Consumer defaultConsumer = new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String exchange = envelope.getExchange();
                    String routingKey = envelope.getRoutingKey();
                    long deliveryTag = envelope.getDeliveryTag();
                    String message = new String(body, StandardCharsets.UTF_8);
                    System.out.println("交换机:"+exchange+"  路由："+routingKey+"  递交标签："+deliveryTag+" \n 消息："+message);

                }
            };
            channel.basicConsume(QUEUE_INFORM_EMAIL,true,defaultConsumer);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

}
