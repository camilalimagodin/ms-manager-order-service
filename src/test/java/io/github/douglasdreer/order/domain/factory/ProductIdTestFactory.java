package io.github.douglasdreer.order.domain.factory;

import io.github.douglasdreer.order.domain.valueobject.ProductId;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Factory para criar instâncias de ProductId para testes
 * Padrão inspirado em Kotlin com valores padrão
 */
public class ProductIdTestFactory {

    private static final String DEFAULT_PRODUCT_ID = "PROD-001";
    private static final AtomicInteger counter = new AtomicInteger(1);

    private ProductIdTestFactory() {
        // Utility class
    }

    /**
     * Cria ProductId com valor padrão (PROD-001)
     */
    public static ProductId create() {
        return ProductId.of(DEFAULT_PRODUCT_ID);
    }

    /**
     * Cria ProductId com valor customizado
     */
    public static ProductId create(String value) {
        return ProductId.of(value);
    }

    /**
     * Cria ProductId único usando um contador incremental
     * Útil para testes que precisam de múltiplos produtos diferentes
     */
    public static ProductId unique() {
        return ProductId.of("PROD-" + String.format("%03d", counter.getAndIncrement()));
    }

    /**
     * Cria ProductId com prefixo customizado e contador
     */
    public static ProductId withPrefix(String prefix) {
        return ProductId.of(prefix + "-" + String.format("%03d", counter.getAndIncrement()));
    }

    /**
     * Reseta o contador (útil para testes que precisam de comportamento determinístico)
     */
    public static void resetCounter() {
        counter.set(1);
    }
}
