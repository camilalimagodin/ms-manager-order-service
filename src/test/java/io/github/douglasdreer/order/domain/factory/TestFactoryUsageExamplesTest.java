package io.github.douglasdreer.order.domain.factory;

import io.github.douglasdreer.order.domain.entity.Order;
import io.github.douglasdreer.order.domain.entity.OrderItem;
import io.github.douglasdreer.order.domain.valueobject.OrderStatus;
import io.github.douglasdreer.order.domain.valueobject.ExternalOrderId;
import io.github.douglasdreer.order.domain.valueobject.Money;
import io.github.douglasdreer.order.domain.valueobject.ProductId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Exemplos de uso dos TestFactories
 * Demonstra diferentes formas de criar objetos para teste seguindo o padrão Kotlin
 */
@DisplayName("Exemplos de uso dos TestFactories")
class TestFactoryUsageExamplesTest {

    @Test
    @DisplayName("Exemplo 1: Criar Money com valores padrão")
    void example1_createMoneyWithDefaults() {
        // Given/When - Cria Money com valores padrão (BRL 100.00)
        Money money = MoneyTestFactory.create();

        // Then
        assertThat(money.getAmount()).isEqualByComparingTo("100.00");
        assertThat(money.getCurrency().getCurrencyCode()).isEqualTo("BRL");
    }

    @Test
    @DisplayName("Exemplo 2: Criar Money customizado")
    void example2_createCustomMoney() {
        // Given/When - Diferentes formas de criar Money
        Money brl = MoneyTestFactory.create("250.50");
        Money usd = MoneyTestFactory.usd("100.00");
        Money eur = MoneyTestFactory.eur("150.00");
        Money zero = MoneyTestFactory.zero();

        // Then
        assertThat(brl.getAmount()).isEqualByComparingTo("250.50");
        assertThat(usd.getCurrency().getCurrencyCode()).isEqualTo("USD");
        assertThat(eur.getCurrency().getCurrencyCode()).isEqualTo("EUR");
        assertThat(zero.getAmount()).isEqualByComparingTo("0.00");
    }

    @Test
    @DisplayName("Exemplo 3: Criar ProductId único")
    void example3_createUniqueProductId() {
        // Given - Reseta contador para garantir comportamento determinístico
        ProductIdTestFactory.resetCounter();

        // When
        ProductId product1 = ProductIdTestFactory.unique();
        ProductId product2 = ProductIdTestFactory.unique();
        ProductId product3 = ProductIdTestFactory.unique();

        // Then
        assertThat(product1.getValue()).isEqualTo("PROD-001");
        assertThat(product2.getValue()).isEqualTo("PROD-002");
        assertThat(product3.getValue()).isEqualTo("PROD-003");
    }

    @Test
    @DisplayName("Exemplo 4: Criar OrderItem com valores padrão")
    void example4_createOrderItemWithDefaults() {
        // Given/When - Cria OrderItem com valores padrão
        OrderItem item = OrderItemTestFactory.create();

        // Then
        assertThat(item).isNotNull();
        assertThat(item.getProductId().getValue()).isEqualTo("PROD-001");
        assertThat(item.getProductName()).isEqualTo("Produto Teste");
        assertThat(item.getQuantity()).isEqualTo(1);
        assertThat(item.getUnitPrice().getAmount()).isEqualByComparingTo("100.00");
    }

    @Test
    @DisplayName("Exemplo 5: Criar OrderItem com Builder")
    void example5_createOrderItemWithBuilder() {
        // Given/When - Usa builder para customização fluente
        OrderItem item = OrderItemTestFactory.builder()
                .productId("LAPTOP-001")
                .productName("Notebook Dell XPS")
                .quantity(2)
                .unitPrice("3500.00")
                .build();

        // Then
        assertThat(item.getProductId().getValue()).isEqualTo("LAPTOP-001");
        assertThat(item.getProductName()).isEqualTo("Notebook Dell XPS");
        assertThat(item.getQuantity()).isEqualTo(2);
        assertThat(item.getUnitPrice().getAmount()).isEqualByComparingTo("3500.00");
    }

    @Test
    @DisplayName("Exemplo 6: Criar Order com valores padrão")
    void example6_createOrderWithDefaults() {
        // Given/When - Cria Order com um item padrão
        Order order = OrderTestFactory.create();

        // Then
        assertThat(order).isNotNull();
        assertThat(order.getExternalOrderId().getValue()).isEqualTo("EXT-001");
        assertThat(order.getStatus()).isEqualTo(OrderStatus.RECEIVED);
        assertThat(order.getItems()).hasSize(1);
    }

    @Test
    @DisplayName("Exemplo 7: Criar Order com múltiplos itens")
    void example7_createOrderWithMultipleItems() {
        // Given/When - Cria Order com 3 items
        Order order = OrderTestFactory.withItems(3);
        order.calculateTotal();

        // Then
        assertThat(order.getItems()).hasSize(3);
        assertThat(order.getTotalAmount().getAmount()).isGreaterThan(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Exemplo 8: Criar Order com Builder customizado")
    void example8_createOrderWithBuilder() {
        // Given
        ExternalOrderId customId = ExternalOrderId.of("ORDER-2024-001");
        OrderItem item1 = OrderItemTestFactory.builder()
                .productName("Mouse Gamer")
                .quantity(2)
                .unitPrice("75.00")
                .build();
        OrderItem item2 = OrderItemTestFactory.builder()
                .productName("Teclado Mecânico")
                .quantity(1)
                .unitPrice("150.00")
                .build();

        // When
        Order order = OrderTestFactory.builder()
                .externalOrderId(customId)
                .addItem(item1)
                .addItem(item2)
                .build();
        order.calculateTotal();

        // Then
        assertThat(order.getExternalOrderId()).isEqualTo(customId);
        assertThat(order.getItems()).hasSize(2);
        // Total: (2 * 75) + (1 * 150) = 300.00
        assertThat(order.getTotalAmount().getAmount()).isEqualByComparingTo("300.00");
    }

    @Test
    @DisplayName("Exemplo 9: Criar Order em diferentes estados")
    void example9_createOrderWithDifferentStates() {
        // Given/When - Cria Orders em diferentes estados
        Order receivedOrder = OrderTestFactory.create();
        Order calculatedOrder = OrderTestFactory.calculated();
        Order failedOrder = OrderTestFactory.failed();
        Order availableOrder = OrderTestFactory.available();

        // Then
        assertThat(receivedOrder.getStatus()).isEqualTo(OrderStatus.RECEIVED);
        assertThat(calculatedOrder.getStatus()).isEqualTo(OrderStatus.CALCULATED);
        assertThat(failedOrder.getStatus()).isEqualTo(OrderStatus.FAILED);
        assertThat(availableOrder.getStatus()).isEqualTo(OrderStatus.AVAILABLE);
    }

    @Test
    @DisplayName("Exemplo 10: Combinar factories para cenários complexos")
    void example10_combineFactoriesForComplexScenarios() {
        // Given - Prepara componentes do pedido
        ExternalOrderId externalId = ExternalOrderIdTestFactory.create("ORD-2026-001");
        ProductId laptopId = ProductIdTestFactory.withPrefix("LAPTOP");
        ProductId mouseId = ProductIdTestFactory.withPrefix("MOUSE");

        OrderItem laptopItem = OrderItemTestFactory.builder()
                .productId(laptopId)
                .productName("Dell XPS 13")
                .quantity(1)
                .unitPrice("5000.00")
                .build();

        OrderItem mouseItem = OrderItemTestFactory.builder()
                .productId(mouseId)
                .productName("Logitech MX Master")
                .quantity(2)
                .unitPrice("250.00")
                .build();

        // When - Cria o pedido completo
        Order order = OrderTestFactory.builder()
                .externalOrderId(externalId)
                .addItem(laptopItem)
                .addItem(mouseItem)
                .build();
        order.calculateTotal();

        // Then - Verifica o pedido
        assertThat(order.getExternalOrderId().getValue()).isEqualTo("ORD-2026-001");
        assertThat(order.getItems()).hasSize(2);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CALCULATED);
        // Total: (1 * 5000) + (2 * 250) = 5500.00
        assertThat(order.getTotalAmount().getAmount()).isEqualByComparingTo("5500.00");
    }
}
