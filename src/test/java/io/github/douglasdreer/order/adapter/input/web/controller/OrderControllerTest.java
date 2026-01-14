package io.github.douglasdreer.order.adapter.input.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.douglasdreer.order.application.dto.CreateOrderCommand;
import io.github.douglasdreer.order.application.dto.OrderResponse;
import io.github.douglasdreer.order.application.port.input.CreateOrderUseCase;
import io.github.douglasdreer.order.application.port.input.GetOrderUseCase;
import io.github.douglasdreer.order.application.port.input.ProcessOrderUseCase;
import io.github.douglasdreer.order.domain.exception.DuplicateOrderException;
import io.github.douglasdreer.order.domain.exception.OrderNotFoundException;
import io.github.douglasdreer.order.domain.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@DisplayName("OrderController")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateOrderUseCase createOrderUseCase;

    @MockBean
    private GetOrderUseCase getOrderUseCase;

    @MockBean
    private ProcessOrderUseCase processOrderUseCase;

    private CreateOrderCommand validCommand;
    private OrderResponse orderResponse;
    private UUID orderId;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();

        validCommand = CreateOrderCommand.builder()
                .externalOrderId("EXT-001")
                .items(List.of(
                        CreateOrderCommand.OrderItemCommand.builder()
                                .productId("PROD-001")
                                .productName("Produto Teste")
                                .unitPrice(new BigDecimal("100.00"))
                                .quantity(2)
                                .currency("BRL")
                                .build()
                ))
                .build();

        orderResponse = OrderResponse.builder()
                .id(orderId)
                .externalOrderId("EXT-001")
                .totalAmount(new BigDecimal("200.00"))
                .currency("BRL")
                .status("CALCULATED")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .items(List.of(
                        OrderResponse.OrderItemResponse.builder()
                                .id(UUID.randomUUID())
                                .productId("PROD-001")
                                .productName("Produto Teste")
                                .unitPrice(new BigDecimal("100.00"))
                                .quantity(2)
                                .subtotal(new BigDecimal("200.00"))
                                .currency("BRL")
                                .build()
                ))
                .build();
    }

    @Nested
    @DisplayName("POST /api/v1/orders")
    class CreateOrderTests {

        @Test
        @DisplayName("deve criar pedido com sucesso")
        void shouldCreateOrderSuccessfully() throws Exception {
            // Arrange
            when(createOrderUseCase.execute(any(CreateOrderCommand.class))).thenReturn(orderResponse);

            // Act & Assert
            mockMvc.perform(post("/api/v1/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validCommand)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(orderId.toString()))
                    .andExpect(jsonPath("$.externalOrderId").value("EXT-001"))
                    .andExpect(jsonPath("$.totalAmount").value(200.00))
                    .andExpect(jsonPath("$.status").value("CALCULATED"))
                    .andExpect(jsonPath("$.items").isArray())
                    .andExpect(jsonPath("$.items", hasSize(1)));

            verify(createOrderUseCase).execute(any(CreateOrderCommand.class));
        }

        @Test
        @DisplayName("deve retornar 400 para comando inválido")
        void shouldReturn400ForInvalidCommand() throws Exception {
            // Arrange
            when(createOrderUseCase.execute(any())).thenThrow(new ValidationException("Dados inválidos"));

            // Act & Assert
            mockMvc.perform(post("/api/v1/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validCommand)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Erro de Validação"));
        }

        @Test
        @DisplayName("deve retornar 409 para pedido duplicado")
        void shouldReturn409ForDuplicateOrder() throws Exception {
            // Arrange
            when(createOrderUseCase.execute(any())).thenThrow(new DuplicateOrderException("EXT-001"));

            // Act & Assert
            mockMvc.perform(post("/api/v1/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validCommand)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.title").value("Pedido Duplicado"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/orders/{id}")
    class GetOrderByIdTests {

        @Test
        @DisplayName("deve retornar pedido quando encontrado")
        void shouldReturnOrderWhenFound() throws Exception {
            // Arrange
            when(getOrderUseCase.findById(orderId)).thenReturn(Optional.of(orderResponse));

            // Act & Assert
            mockMvc.perform(get("/api/v1/orders/{id}", orderId)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(orderId.toString()))
                    .andExpect(jsonPath("$.externalOrderId").value("EXT-001"));

            verify(getOrderUseCase).findById(orderId);
        }

        @Test
        @DisplayName("deve retornar 404 quando pedido não encontrado")
        void shouldReturn404WhenNotFound() throws Exception {
            // Arrange
            UUID unknownId = UUID.randomUUID();
            when(getOrderUseCase.findById(unknownId)).thenReturn(Optional.empty());

            // Act & Assert
            mockMvc.perform(get("/api/v1/orders/{id}", unknownId)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/orders/external/{externalOrderId}")
    class GetOrderByExternalIdTests {

        @Test
        @DisplayName("deve retornar pedido por ID externo")
        void shouldReturnOrderByExternalId() throws Exception {
            // Arrange
            when(getOrderUseCase.findByExternalOrderId("EXT-001")).thenReturn(Optional.of(orderResponse));

            // Act & Assert
            mockMvc.perform(get("/api/v1/orders/external/{externalOrderId}", "EXT-001")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.externalOrderId").value("EXT-001"));

            verify(getOrderUseCase).findByExternalOrderId("EXT-001");
        }

        @Test
        @DisplayName("deve retornar 404 quando ID externo não encontrado")
        void shouldReturn404WhenExternalIdNotFound() throws Exception {
            // Arrange
            when(getOrderUseCase.findByExternalOrderId(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            mockMvc.perform(get("/api/v1/orders/external/{externalOrderId}", "UNKNOWN")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/orders/status/{status}")
    class GetOrdersByStatusTests {

        @Test
        @DisplayName("deve retornar lista de pedidos por status")
        void shouldReturnOrdersByStatus() throws Exception {
            // Arrange
            when(getOrderUseCase.findByStatus("CALCULATED")).thenReturn(List.of(orderResponse));

            // Act & Assert
            mockMvc.perform(get("/api/v1/orders/status/{status}", "CALCULATED")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].status").value("CALCULATED"));

            verify(getOrderUseCase).findByStatus("CALCULATED");
        }

        @Test
        @DisplayName("deve retornar lista vazia quando nenhum pedido com status")
        void shouldReturnEmptyListWhenNoOrders() throws Exception {
            // Arrange
            when(getOrderUseCase.findByStatus("FAILED")).thenReturn(List.of());

            // Act & Assert
            mockMvc.perform(get("/api/v1/orders/status/{status}", "FAILED")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/orders")
    class GetAllOrdersTests {

        @Test
        @DisplayName("deve retornar todos os pedidos")
        void shouldReturnAllOrders() throws Exception {
            // Arrange
            when(getOrderUseCase.findAll()).thenReturn(List.of(orderResponse));

            // Act & Assert
            mockMvc.perform(get("/api/v1/orders")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));

            verify(getOrderUseCase).findAll();
        }
    }

    @Nested
    @DisplayName("POST /api/v1/orders/{id}/process")
    class ProcessOrderTests {

        @Test
        @DisplayName("deve processar pedido com sucesso")
        void shouldProcessOrderSuccessfully() throws Exception {
            // Arrange
            when(processOrderUseCase.process(orderId)).thenReturn(orderResponse);

            // Act & Assert
            mockMvc.perform(post("/api/v1/orders/{id}/process", orderId)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(orderId.toString()));

            verify(processOrderUseCase).process(orderId);
        }

        @Test
        @DisplayName("deve retornar 404 quando pedido não encontrado")
        void shouldReturn404WhenOrderNotFound() throws Exception {
            // Arrange
            UUID unknownId = UUID.randomUUID();
            when(processOrderUseCase.process(unknownId))
                    .thenThrow(OrderNotFoundException.byId(unknownId.toString()));

            // Act & Assert
            mockMvc.perform(post("/api/v1/orders/{id}/process", unknownId)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/orders/{id}/available")
    class MarkAsAvailableTests {

        @Test
        @DisplayName("deve marcar pedido como disponível")
        void shouldMarkOrderAsAvailable() throws Exception {
            // Arrange
            OrderResponse availableResponse = OrderResponse.builder()
                    .id(orderId)
                    .externalOrderId("EXT-001")
                    .status("AVAILABLE")
                    .totalAmount(new BigDecimal("200.00"))
                    .currency("BRL")
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .items(orderResponse.getItems())
                    .build();

            when(processOrderUseCase.markAsAvailable(orderId)).thenReturn(availableResponse);

            // Act & Assert
            mockMvc.perform(patch("/api/v1/orders/{id}/available", orderId)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("AVAILABLE"));

            verify(processOrderUseCase).markAsAvailable(orderId);
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/orders/{id}/failed")
    class MarkAsFailedTests {

        @Test
        @DisplayName("deve marcar pedido como falha")
        void shouldMarkOrderAsFailed() throws Exception {
            // Arrange
            OrderResponse failedResponse = OrderResponse.builder()
                    .id(orderId)
                    .externalOrderId("EXT-001")
                    .status("FAILED")
                    .totalAmount(new BigDecimal("200.00"))
                    .currency("BRL")
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .items(orderResponse.getItems())
                    .build();

            when(processOrderUseCase.markAsFailed(eq(orderId), anyString())).thenReturn(failedResponse);

            // Act & Assert
            mockMvc.perform(patch("/api/v1/orders/{id}/failed", orderId)
                            .param("reason", "Erro de teste")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("FAILED"));

            verify(processOrderUseCase).markAsFailed(eq(orderId), anyString());
        }
    }
}
