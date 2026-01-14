package io.github.douglasdreer.order.adapter.input.web.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuração da documentação OpenAPI/Swagger.
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI orderServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Order Management Service API")
                        .description("""
                                API REST para gerenciamento de pedidos de alta volumetria.
                                
                                **Funcionalidades:**
                                - Criação e processamento de pedidos
                                - Consulta de pedidos por diversos critérios
                                - Cálculo automático de totais
                                - Controle de status do ciclo de vida
                                
                                **Arquitetura:**
                                - Hexagonal/Ports & Adapters
                                - DDD (Domain-Driven Design)
                                - Eventos assíncronos com RabbitMQ
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Douglas Dreer")
                                .email("douglas.dreer@example.com")
                                .url("https://github.com/douglasdreer"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Servidor Local"),
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor Desenvolvimento")
                ));
    }
}
