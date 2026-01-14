package io.github.douglasdreer.order.application.usecase;

import io.github.douglasdreer.order.application.dto.CreateOrderCommand;
import io.github.douglasdreer.order.application.dto.OrderResponse;
import io.github.douglasdreer.order.application.mapper.OrderApplicationMapper;
import io.github.douglasdreer.order.application.port.output.OrderRepositoryPort;
import io.github.douglasdreer.order.domain.entity.Order;
import io.github.douglasdreer.order.domain.exception.DuplicateOrderException;
import io.github.douglasdreer.order.domain.exception.ValidationException;
import io.github.douglasdreer.order.domain.valueobject.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateOrderUseCaseImpl")
class CreateOrderUseCaseImplTest {

    @Mock
    private OrderRepositoryPort orderRepository;

    @Spy
    private OrderApplicationMapper mapper = new OrderApplicationMapper();

    @InjectMocks
    private CreateOrderUseCaseImpl useCase;

    private CreateOrderCommand validCommand;

    @BeforeEach
    void setUp() {
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
    }

    @Nested
    @DisplayName("execute()")
    class ExecuteTests {

        @Test
        @DisplayName("deve criar pedido com sucesso")
        void shouldCreateOrderSuccessfully() {
            // Preparar
            when(orderRepository.existsByExternalOrderId(anyString())).thenReturn(false);
            when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Agir
            OrderResponse response = useCase.execute(validCommand);

            // Verificar
            assertThat(response).isNotNull();
            assertThat(response.getExternalOrderId()).isEqualTo("EXT-001");
            assertThat(response.getStatus()).isEqualTo(OrderStatus.CALCULATED.name());
            assertThat(response.getTotalAmount()).isEqualByComparingTo(new BigDecimal("200.00"));
            assertThat(response.getItems()).hasSize(1);

            verify(orderRepository).existsByExternalOrderId("EXT-001");
            verify(orderRepository).save(any(Order.class));
        }

        @Test
        @DisplayName("deve calcular total corretamente com múltiplos itens")
        void shouldCalculateTotalCorrectly() {
            // Preparar
            CreateOrderCommand commandMultipleItems = CreateOrderCommand.builder()
                    .externalOrderId("EXT-002")
                    .items(List.of(
                            CreateOrderCommand.OrderItemCommand.builder()
                                    .productId("PROD-001")
                                    .productName("Produto A")
                                    .unitPrice(new BigDecimal("50.00"))
                                    .quantity(2)
                                    .currency("BRL")
                                    .build(),
                            CreateOrderCommand.OrderItemCommand.builder()
                                    .productId("PROD-002")
                                    .productName("Produto B")
                                    .unitPrice(new BigDecimal("30.00"))
                                    .quantity(3)
                                    .currency("BRL")
                                    .build()
                    ))
                    .build();

            when(orderRepository.existsByExternalOrderId(anyString())).thenReturn(false);
            when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Agir
            OrderResponse response = useCase.execute(commandMultipleItems);

            // Verificar
            // 50*2 + 30*3 = 100 + 90 = 190
            assertThat(response.getTotalAmount()).isEqualByComparingTo(new BigDecimal("190.00"));
            assertThat(response.getItems()).hasSize(2);
        }

        @Test
        @DisplayName("deve lançar exceção para pedido duplicado")
        void shouldThrowExceptionForDuplicateOrder() {
            // Preparar
            when(orderRepository.existsByExternalOrderId("EXT-001")).thenReturn(true);

            // Agir & Assert
            assertThatThrownBy(() -> useCase.execute(validCommand))
                    .isInstanceOf(DuplicateOrderException.class)
                    .hasMessageContaining("EXT-001");

            verify(orderRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Validação de comando")
    class ValidationTests {

        @Test
        @DisplayName("deve lançar exceção para comando nulo")
        void shouldThrowExceptionForNullCommand() {
            assertThatThrownBy(() -> useCase.execute(null))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("nulo");
        }

        @Test
        @DisplayName("deve lançar exceção para externalOrderId vazio")
        void shouldThrowExceptionForEmptyExternalOrderId() {
            CreateOrderCommand command = CreateOrderCommand.builder()
                    .externalOrderId("")
                    .items(validCommand.getItems())
                    .build();

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("ID externo");
        }

        @Test
        @DisplayName("deve lançar exceção para lista de itens vazia")
        void shouldThrowExceptionForEmptyItems() {
            CreateOrderCommand command = CreateOrderCommand.builder()
                    .externalOrderId("EXT-001")
                    .items(List.of())
                    .build();

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("item");
        }

        @Test
        @DisplayName("deve lançar exceção para productId vazio")
        void shouldThrowExceptionForEmptyProductId() {
            CreateOrderCommand command = CreateOrderCommand.builder()
                    .externalOrderId("EXT-001")
                    .items(List.of(
                            CreateOrderCommand.OrderItemCommand.builder()
                                    .productId("")
                                    .productName("Produto")
                                    .unitPrice(new BigDecimal("10.00"))
                                    .quantity(1)
                                    .build()
                    ))
                    .build();

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("ID do produto");
        }

        @Test
        @DisplayName("deve lançar exceção para preço negativo")
        void shouldThrowExceptionForNegativePrice() {
            CreateOrderCommand command = CreateOrderCommand.builder()
                    .externalOrderId("EXT-001")
                    .items(List.of(
                            CreateOrderCommand.OrderItemCommand.builder()
                                    .productId("PROD-001")
                                    .productName("Produto")
                                    .unitPrice(new BigDecimal("-10.00"))
                                    .quantity(1)
                                    .build()
                    ))
                    .build();

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Preço unitário");
        }

        @Test
        @DisplayName("deve lançar exceção para quantidade zero")
        void shouldThrowExceptionForZeroQuantity() {
            CreateOrderCommand command = CreateOrderCommand.builder()
                    .externalOrderId("EXT-001")
                    .items(List.of(
                            CreateOrderCommand.OrderItemCommand.builder()
                                    .productId("PROD-001")
                                    .productName("Produto")
                                    .unitPrice(new BigDecimal("10.00"))
                                    .quantity(0)
                                    .build()
                    ))
                    .build();

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Quantidade");
        }
    }
}
