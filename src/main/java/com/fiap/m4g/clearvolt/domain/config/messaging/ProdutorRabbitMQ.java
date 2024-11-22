package com.fiap.m4g.clearvolt.domain.config.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//@Service
public class ProdutorRabbitMQ {

    //@Autowired
    private RabbitTemplate rabbitTemplate;

    public void enviarMensagem(String msg) {
        rabbitTemplate.convertAndSend(ConfigRabbitMQ.roteador,
                ConfigRabbitMQ.chave_rota,
                msg);
        System.out.println("Mensagem enviada: " + msg);

    }

}
