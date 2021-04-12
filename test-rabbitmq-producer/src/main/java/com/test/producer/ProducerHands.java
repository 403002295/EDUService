package com.test.producer;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * created by wangzexu on 4/6/21
 */
public class ProducerHands {
    //rabbitMQ 路由的是由两个组件决定的 他们有自己唯一的名字
    private static final String EXCHANGE_NAME = "exchange_hands_inform";
    private static final String QUEUE_INFORM_EMAIL = "queue_hands_email";
    private static final String QUEUE_INFORM_SMS = "queue_hands_sms";

    private static final long now = System.currentTimeMillis();

    private static ThreadLocal<Channel> threadLocal = new ThreadLocal<>();
    public static void main(String[] args) {

        //配置 IP 虚拟机 用户名  密码
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.1.135");
        factory.setPort(5672);
        factory.setUsername("mark");
        factory.setPassword("mark");
        factory.setVirtualHost("/");

        Connection connection = null;
        Channel channel = null;
        //创建交换机
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();

            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.HEADERS);
            //生命 队列
            /**
             * 声明队列，如果Rabbit中没有此队列将自动创建 * param1:队列名称
             * param2:是否持久化
             * param3:队列是否独占此连接
             * param4:队列不再使用时是否自动删除此队列 * param5:队列参数
             */
            channel.queueDeclare(QUEUE_INFORM_SMS,true,false,false,null);
            channel.queueDeclare(QUEUE_INFORM_EMAIL,true,false,false,null);
            Map handers_email = new HashMap();
            handers_email.put("inform_type", "email");
            Map handers_sms = new HashMap();
            handers_sms.put("inform_type", "sms");
            channel.queueBind(QUEUE_INFORM_EMAIL,EXCHANGE_NAME,"",handers_email);
            channel.queueBind(QUEUE_INFORM_EMAIL,EXCHANGE_NAME,"",handers_sms);
            String message = "0010 1001 0101 1010";
            /**
             * 消息发布方法
             * param1:Exchange的名称，如果没有指定，则使用Default Exchange
             * param2:routingKey,消息的路由Key，是用于Exchange(交换机)将消息转发到指定的消息队列
             * param3:消息包含的属性
             * param4:消息体
             */
            /**
             * 这里没有指定交换机，消息将发送给默认交换机，每个队列也会绑定那个默认的交换机，但是不能显
             示绑定或解除绑定
             * 默认的交换机，routingKey等于队列名称
             */
            AMQP.BasicProperties.Builder  properties = new AMQP.BasicProperties().builder();
            properties.headers(handers_email);
            channel.basicPublish(EXCHANGE_NAME,"",properties.build(),(message+" == sms").getBytes(StandardCharsets.UTF_8));




            Channel finalChannel = channel;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    threadLocal.set(finalChannel);
                    Channel channel1 = threadLocal.get();
                    try {
                        long times = 0;
                        while (true) {
                            times++;

                            AMQP.BasicProperties.Builder basicProperties = new AMQP.BasicProperties().builder();
                            basicProperties.headers(handers_sms);
                            channel1.basicPublish(EXCHANGE_NAME, "", basicProperties.build(), (Thread.currentThread().getName() + "== sms " + message + "==" + times).getBytes(StandardCharsets.UTF_8));
                            finalChannel.basicPublish(EXCHANGE_NAME,"",properties.build(),(message+" == email ").getBytes(StandardCharsets.UTF_8));

//                            Thread.sleep(1000);
                            boolean ok = System.currentTimeMillis() - now > 1000 * 60 ? true : false;
                            if (ok) {
                                break;
                            }
                            System.out.println((Thread.currentThread().getName() + " ;==" + message + "==" + times).getBytes(StandardCharsets.UTF_8));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };

            //多线程定时发送
            Thread test1 = new Thread(runnable,"test1");
            Thread test2 = new Thread(runnable,"test2");
            Thread test3 = new Thread(runnable,"test3");
            test1.start();
            test2.start();
            test3.start();

           /* for (int i = 0; i < 100; i++) {
                new Thread(runnable,"test"+3+i).start();
            }*/


            System.out.println("message:"+message+" 已经发送");


        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }finally {

            boolean ok;
            while (true){

                ok = System.currentTimeMillis() - now > 1000 * 60 * 2*1.5 ? true : false;
                if (ok) {
                    break;
                }
                System.out.println(Thread.currentThread().getName()+" 等改时间结束"+((1000 * 60 * 2*1.5)-(System.currentTimeMillis()-now))/1000 + "s");
            }
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
