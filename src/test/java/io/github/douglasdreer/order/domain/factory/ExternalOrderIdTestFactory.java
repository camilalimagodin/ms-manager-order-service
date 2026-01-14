package io.github.douglasdreer.order.domain.factory;

import io.github.douglasdreer.order.domain.valueobject.ExternalOrderId;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Factory para criar instâncias de ExternalOrderId para testes
 * Padrão inspirado em Kotlin com valores padrão
 */
public class ExternalOrderIdTestFactory {

    private static final String DEFAULT_EXTERNAL_ORDER_ID = "EXT-001";
    private static final AtomicInteger counter = new AtomicInteger(1);

    private ExternalOrderIdTestFactory() {
        // Utility class
    }

    /**
     * Cria ExternalOrderId com valor padrão (EXT-001)
     */
    public static ExternalOrderId create() {
        return ExternalOrderId.of(DEFAULT_EXTERNAL_ORDER_ID);
    }

    /**
     * Cria ExternalOrderId com valor customizado
     */
    public static ExternalOrderId create(String value) {
        return ExternalOrderId.of(value);
    }

    /**
     * Cria ExternalOrderId único usando um contador incremental
     * Útil para testes que precisam de múltiplos pedidos diferentes
     */
    public static ExternalOrderId unique() {
        return ExternalOrderId.of("EXT-" + String.format("%03d", counter.getAndIncrement()));
    }

    /**
     * Cria ExternalOrderId com prefixo customizado e contador
     */
    public static ExternalOrderId withPrefix(String prefix) {
        return ExternalOrderId.of(prefix + "-" + String.format("%03d", counter.getAndIncrement()));
    }

    /**
     * Cria ExternalOrderId aleatório usando UUID
     */
    public static ExternalOrderId random() {
        return ExternalOrderId.of("EXT-" + UUID.randomUUID().toString());
    }

    /**
     * Reseta o contador (útil para testes que precisam de comportamento determinístico)
     */
    public static void resetCounter() {
        counter.set(1);
    }
}
