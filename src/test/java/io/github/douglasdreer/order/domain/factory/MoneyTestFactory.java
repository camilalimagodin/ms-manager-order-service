package io.github.douglasdreer.order.domain.factory;

import io.github.douglasdreer.order.domain.valueobject.Money;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Factory para criar instâncias de Money para testes
 * Padrão inspirado em Kotlin com valores padrão
 */
public class MoneyTestFactory {

    private static final Currency DEFAULT_CURRENCY = Currency.getInstance("BRL");
    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal("100.00");

    private MoneyTestFactory() {
        // Utility class
    }

    /**
     * Cria Money com valores padrão (BRL 100.00)
     */
    public static Money create() {
        return Money.of(DEFAULT_AMOUNT, DEFAULT_CURRENCY);
    }

    /**
     * Cria Money com amount customizado e moeda padrão (BRL)
     */
    public static Money create(BigDecimal amount) {
        return Money.of(amount, DEFAULT_CURRENCY);
    }

    /**
     * Cria Money com amount e currency customizados
     */
    public static Money create(BigDecimal amount, Currency currency) {
        return Money.of(amount, currency);
    }

    /**
     * Cria Money com amount customizado (String) e moeda padrão (BRL)
     */
    public static Money create(String amount) {
        return Money.of(new BigDecimal(amount), DEFAULT_CURRENCY);
    }

    /**
     * Cria Money com amount (String) e currency customizados
     */
    public static Money create(String amount, String currencyCode) {
        return Money.of(new BigDecimal(amount), Currency.getInstance(currencyCode));
    }

    /**
     * Cria Money com valor zero e moeda padrão
     */
    public static Money zero() {
        return Money.of(BigDecimal.ZERO, DEFAULT_CURRENCY);
    }

    /**
     * Cria Money com valor zero e moeda customizada
     */
    public static Money zero(Currency currency) {
        return Money.of(BigDecimal.ZERO, currency);
    }

    /**
     * Cria Money em USD com amount customizado
     */
    public static Money usd(String amount) {
        return Money.of(new BigDecimal(amount), Currency.getInstance("USD"));
    }

    /**
     * Cria Money em EUR com amount customizado
     */
    public static Money eur(String amount) {
        return Money.of(new BigDecimal(amount), Currency.getInstance("EUR"));
    }

    /**
     * Cria Money em BRL com amount customizado
     */
    public static Money brl(String amount) {
        return Money.of(new BigDecimal(amount), Currency.getInstance("BRL"));
    }
}
