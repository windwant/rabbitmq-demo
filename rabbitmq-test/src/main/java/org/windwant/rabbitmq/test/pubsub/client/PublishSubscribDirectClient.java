package org.windwant.rabbitmq.test.pubsub.client;

import com.rabbitmq.client.*;
import org.windwant.rabbitmq.test.core.ConnectionMgr;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 广播Direct接收
 * Created by windwant on 2016/8/15.
 */
public class PublishSubscribDirectClient {
    private DefaultConsumer consumer;
    private final String EXCHANGE_NAME = "exchange_direct";
    public void run(){
        try {
            ConnectionFactory connectionFactory = ConnectionMgr.getConnection();
            Connection connection = connectionFactory.newConnection();
            final Channel channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");//direct

            String queueName = channel.queueDeclare().getQueue();//随机queue
            channel.queueBind(queueName, EXCHANGE_NAME, "direct_test");//通道绑定队列
//            channel.queueBind(queueName, EXCHANGE_NAME, "direct_test1");//
            consumer = new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    System.out.println(" [x] Received '" + message + "'");
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            };
            //autoAck false
            channel.basicConsume(queueName, false, consumer);
        } catch (ConfigurationException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        new PublishSubscribDirectClient().run();
    }
}
