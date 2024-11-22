package com.fiap.m4g.clearvolt.domain.config.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class ConfigRabbitMQ {

    public static final String fila = "fila";
    public static final String roteador = "roteador";
    public static final String chave_rota = "rota";

    //@Bean
    public Queue fila() {
        return new Queue(fila, true);
    }

    //@Bean
    public DirectExchange directExchange() {
        return new DirectExchange(roteador);
    }

    //@Bean
    public Binding binding(Queue fila, DirectExchange roteador) {
        return BindingBuilder.bind(fila).to(roteador).with(chave_rota);
    }
}