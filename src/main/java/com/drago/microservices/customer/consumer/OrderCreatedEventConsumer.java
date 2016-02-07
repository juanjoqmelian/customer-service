package com.drago.microservices.customer.consumer;


import com.drago.microservices.customer.repository.CustomerRepository;
import com.drago.microservices.customer.repository.RepositoryFactory;
import com.dragosolutions.microservices.event.Event;
import com.dragosolutions.microservices.event.EventsTopic;
import com.dragosolutions.microservices.event.consumer.EventConsumer;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class OrderCreatedEventConsumer implements EventConsumer {

    private final Channel channel;
    private final String queueName;
    private final Consumer consumer;
    private final CustomerRepository customerRepository;

    public OrderCreatedEventConsumer() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        Connection connection = connectionFactory.newConnection();
        channel = connection.createChannel();
        queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EventsTopic.EVENTS_TOPIC, "");
        consumer = new DefaultConsumer(channel) {

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                //TODO - check customer's credit and insert credit log.
                System.out.println("Order created event received!!!");
            }
        };
        customerRepository = RepositoryFactory.getCustomerRepository("localhost", 27017, "customer");
    }

    @Override
    public void consume(Event event) {
        try {
            channel.basicConsume(queueName, true, consumer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
