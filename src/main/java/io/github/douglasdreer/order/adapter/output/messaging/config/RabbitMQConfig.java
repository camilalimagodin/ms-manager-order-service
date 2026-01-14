package io.github.douglasdreer.order.adapter.output.messaging.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do RabbitMQ para mensageria assíncrona.
 * <p>
 * Define exchanges, queues, bindings e Dead Letter Queues (DLQ)
 * para garantir resiliência no processamento de mensagens.
 */
@Configuration
@Slf4j
public class RabbitMQConfig {

    // ========== Exchanges ==========
    public static final String ORDER_EXCHANGE = "order.exchange";
    public static final String ORDER_DLX = "order.dlx";

    // ========== Queues ==========
    public static final String ORDER_CREATED_QUEUE = "order.created.queue";
    public static final String ORDER_CREATED_DLQ = "order.created.dlq";
    public static final String ORDER_STATUS_CHANGED_QUEUE = "order.status.changed.queue";

    // ========== Routing Keys ==========
    public static final String ORDER_CREATED_ROUTING_KEY = "order.created";
    public static final String ORDER_STATUS_CHANGED_ROUTING_KEY = "order.status.changed";

    // ========== Configuration Constants ==========
    private static final int MESSAGE_TTL_MS = 60000; // 1 minuto
    

    /**
     * Exchange principal para eventos de pedidos.
     */
    @Bean
    public TopicExchange orderExchange() {
        log.info("Criando exchange: {}", ORDER_EXCHANGE);
        return ExchangeBuilder
                .topicExchange(ORDER_EXCHANGE)
                .durable(true)
                .build();
    }

    /**
     * Dead Letter Exchange para mensagens que falham no processamento.
     */
    @Bean
    public DirectExchange deadLetterExchange() {
        log.info("Criando Dead Letter Exchange: {}", ORDER_DLX);
        return ExchangeBuilder
                .directExchange(ORDER_DLX)
                .durable(true)
                .build();
    }

    /**
     * Queue para eventos de criação de pedidos (Produto Externo A).
     */
    @Bean
    public Queue orderCreatedQueue() {
        log.info("Criando queue: {}", ORDER_CREATED_QUEUE);
        return QueueBuilder
                .durable(ORDER_CREATED_QUEUE)
                .withArgument("x-dead-letter-exchange", ORDER_DLX)
                .withArgument("x-dead-letter-routing-key", ORDER_CREATED_DLQ)
                .withArgument("x-message-ttl", MESSAGE_TTL_MS)
                .build();
    }

    /**
     * Dead Letter Queue para mensagens de criação de pedidos que falharam.
     */
    @Bean
    public Queue orderCreatedDeadLetterQueue() {
        log.info("Criando Dead Letter Queue: {}", ORDER_CREATED_DLQ);
        return QueueBuilder
                .durable(ORDER_CREATED_DLQ)
                .build();
    }

    /**
     * Queue para eventos de mudança de status de pedidos.
     */
    @Bean
    public Queue orderStatusChangedQueue() {
        log.info("Criando queue: {}", ORDER_STATUS_CHANGED_QUEUE);
        return QueueBuilder
                .durable(ORDER_STATUS_CHANGED_QUEUE)
                .build();
    }

    /**
     * Binding entre exchange principal e queue de criação de pedidos.
     */
    @Bean
    public Binding orderCreatedBinding(Queue orderCreatedQueue, TopicExchange orderExchange) {
        log.info("Criando binding: {} -> {} com routing key: {}", 
                ORDER_EXCHANGE, ORDER_CREATED_QUEUE, ORDER_CREATED_ROUTING_KEY);
        return BindingBuilder
                .bind(orderCreatedQueue)
                .to(orderExchange)
                .with(ORDER_CREATED_ROUTING_KEY);
    }

    /**
     * Binding entre DLX e Dead Letter Queue.
     */
    @Bean
    public Binding deadLetterBinding(Queue orderCreatedDeadLetterQueue, DirectExchange deadLetterExchange) {
        log.info("Criando binding DLQ: {} -> {}", ORDER_DLX, ORDER_CREATED_DLQ);
        return BindingBuilder
                .bind(orderCreatedDeadLetterQueue)
                .to(deadLetterExchange)
                .with(ORDER_CREATED_DLQ);
    }

    /**
     * Binding entre exchange principal e queue de mudança de status.
     */
    @Bean
    public Binding orderStatusChangedBinding(Queue orderStatusChangedQueue, TopicExchange orderExchange) {
        log.info("Criando binding: {} -> {} com routing key: {}", 
                ORDER_EXCHANGE, ORDER_STATUS_CHANGED_QUEUE, ORDER_STATUS_CHANGED_ROUTING_KEY);
        return BindingBuilder
                .bind(orderStatusChangedQueue)
                .to(orderExchange)
                .with(ORDER_STATUS_CHANGED_ROUTING_KEY);
    }

    /**
     * Conversor de mensagens JSON usando Jackson.
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Template configurado para envio de mensagens.
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        template.setMandatory(true); // Garante que mensagens não roteadas retornam erro
        template.setReturnsCallback(returned -> 
            log.error("Mensagem não roteada: {} - Routing Key: {}", 
                returned.getMessage(), returned.getRoutingKey())
        );
        return template;
    }

    /**
     * Container factory para listeners com retry e DLQ.
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {
        
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setDefaultRequeueRejected(false); // Não reprocessa mensagens rejeitadas
        factory.setPrefetchCount(10); // Quantidade de mensagens pré-carregadas
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        
        log.info("RabbitMQ Listener Container Factory configurado com prefetch={}", 10);
        
        return factory;
    }
}
