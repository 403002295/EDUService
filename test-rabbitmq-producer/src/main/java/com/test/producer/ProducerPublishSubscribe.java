package com.test.producer;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * created by wangzexu on 4/9/21
 * 发布/订阅 模式  每一个消费者都监听自己的队列
 */
public class ProducerPublishSubscribe {
    private static final String QUEUE_INFORM_EMAIL = "queue_inform_email";
    private static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    private static final String EXCHANGE_FANOUT_INFORM="exchange_fanout_inform";

    private static final long firstTime = System.currentTimeMillis();

    public static void main(String[] args) throws IOException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.1.135");
        if (args.length > 0) {
            String ip = args[0];
            connectionFactory.setHost(ip);
        }
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("mark");
        connectionFactory.setPassword("mark");
        connectionFactory.setVirtualHost("/");

        Connection connection = null;
        Channel channel = null;

        try {
            connection = connectionFactory.newConnection();
            channel = connection.createChannel();

            //生命队列 和 交换机 并绑定

            channel.exchangeDeclare(EXCHANGE_FANOUT_INFORM,BuiltinExchangeType.FANOUT);
            channel.queueDeclare(QUEUE_INFORM_SMS, true, false, false, null);
            channel.queueDeclare(QUEUE_INFORM_EMAIL, true, false, false, null);

            channel.queueBind(QUEUE_INFORM_SMS, EXCHANGE_FANOUT_INFORM, "");
            channel.queueBind(QUEUE_INFORM_EMAIL, EXCHANGE_FANOUT_INFORM, "");

            Channel finalChannel = channel;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        long i = 0;
                        while (true) {
                            i++;
                            finalChannel.basicPublish(EXCHANGE_FANOUT_INFORM,"",null,(Thread.currentThread().getName()+" = fanout publish&subscribe" + " : "+i).getBytes(StandardCharsets.UTF_8));
                            if (System.currentTimeMillis() - firstTime > 1000 * 60) {
                                break;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };

            new Thread(runnable).start();
            new Thread(runnable).start();

            long i =0;
            while (true) {
                i++;
                finalChannel.basicPublish(EXCHANGE_FANOUT_INFORM,"",null,(Thread.currentThread().getName()+" = fanout publish&subscribe" + " : "+i).getBytes(StandardCharsets.UTF_8));
                if (System.currentTimeMillis() - firstTime > 1000 * 60) {
                    break;
                }
            }
            channel.basicPublish(EXCHANGE_FANOUT_INFORM,"",null,"fanout 发布订阅".getBytes(StandardCharsets.UTF_8));



        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }finally {
            while (true) {

                if (System.currentTimeMillis() - firstTime < 1000 * 70) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else {
                    break;
                }
            }
            if (null != channel) {
                try {
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }
            if (null != connection) {
                connection.close();
            }
        }
    }

}
