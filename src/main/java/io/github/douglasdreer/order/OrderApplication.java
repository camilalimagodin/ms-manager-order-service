package io.github.douglasdreer.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicação principal do serviço de gerenciamento de pedidos.
 * 
 * Microserviço responsável por:
 * - Receber pedidos do Produto Externo A via RabbitMQ
 * - Calcular totais dos pedidos
 * - Expor consultas para o Produto Externo B via REST API
 * 
 * @author Order Team
 * @version 1.0.0
 */
@SpringBootApplication
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
