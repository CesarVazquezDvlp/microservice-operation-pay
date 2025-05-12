package com.microservice.operation.pay.rabbit;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQMessageProducer {
	private static final Logger logger = LoggerFactory.getLogger(RabbitMQMessageProducer.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.estatus}")
    private String estatusExchange;

    @Value("${rabbitmq.routing-key.estatus}")
    private String estatusRoutingKey;

    public RabbitMQMessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(Map<String, String> message) {
        logger.info("Enviando mensaje a RabbitMQ Exchange: {}, Routing Key: {}, Mensaje: {}",
                    estatusExchange, estatusRoutingKey, message);
        rabbitTemplate.convertAndSend(estatusExchange, estatusRoutingKey, message);
        logger.info("Mensaje enviado correctamente a RabbitMQ.");
    }
}
