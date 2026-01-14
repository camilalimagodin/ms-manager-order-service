package io.github.douglasdreer.order.domain.valueobject;

import io.github.douglasdreer.order.domain.exception.InvalidMoneyException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/**
 * Value Object que representa um valor monetário.
 * 
 * Características:
 * - Imutável
 * - Sempre com 2 casas decimais
 * - Operações retornam novas instâncias
 * - Validação de valores positivos
 */
public final class Money {
    
    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    
    public static final Currency DEFAULT_CURRENCY = Currency.getInstance("BRL");
    public static final Money ZERO = new Money(BigDecimal.ZERO, DEFAULT_CURRENCY);
    
    private final BigDecimal amount;
    private final Currency currency;
    
    private Money(BigDecimal amount, Currency currency) {
        this.amount = amount.setScale(SCALE, ROUNDING_MODE);
        this.currency = currency;
    }
    
    /**
     * Cria uma instância de Money com a moeda padrão (BRL).
     * 
     * @param amount valor
     * @return instância de Money
     * @throws InvalidMoneyException se o valor for nulo ou negativo
     */
    public static Money of(BigDecimal amount) {
        return of(amount, DEFAULT_CURRENCY);
    }
    
    /**
     * Cria uma instância de Money.
     * 
     * @param amount valor
     * @param currency moeda
     * @return instância de Money
     * @throws InvalidMoneyException se o valor for nulo ou negativo
     */
    public static Money of(BigDecimal amount, Currency currency) {
        validate(amount);
        return new Money(amount, currency);
    }
    
    /**
     * Cria uma instância de Money a partir de um double.
     * 
     * @param amount valor
     * @return instância de Money
     */
    public static Money of(double amount) {
        return of(BigDecimal.valueOf(amount));
    }
    
    /**
     * Cria uma instância de Money a partir de uma String.
     * 
     * @param amount valor como string
     * @return instância de Money
     */
    public static Money of(String amount) {
        return of(new BigDecimal(amount));
    }
    
    private static void validate(BigDecimal amount) {
        if (amount == null) {
            throw new InvalidMoneyException("O valor não pode ser nulo");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidMoneyException("O valor não pode ser negativo: " + amount);
        }
    }
    
    /**
     * Soma dois valores monetários.
     * 
     * @param other outro Money
     * @return novo Money com a soma
     */
    public Money add(Money other) {
        validateSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }
    
    /**
     * Subtrai dois valores monetários.
     * 
     * @param other outro Money
     * @return novo Money com a subtração
     */
    public Money subtract(Money other) {
        validateSameCurrency(other);
        BigDecimal result = this.amount.subtract(other.amount);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidMoneyException("Resultado da subtração seria negativo");
        }
        return new Money(result, this.currency);
    }
    
    /**
     * Multiplica o valor por uma quantidade.
     * 
     * @param quantity quantidade (deve ser positiva)
     * @return novo Money com o valor multiplicado
     */
    public Money multiply(int quantity) {
        if (quantity < 0) {
            throw new InvalidMoneyException("Quantidade não pode ser negativa: " + quantity);
        }
        return new Money(this.amount.multiply(BigDecimal.valueOf(quantity)), this.currency);
    }
    
    /**
     * Multiplica o valor por um fator decimal.
     * 
     * @param factor fator de multiplicação
     * @return novo Money com o valor multiplicado
     */
    public Money multiply(BigDecimal factor) {
        if (factor.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidMoneyException("Fator não pode ser negativo: " + factor);
        }
        return new Money(this.amount.multiply(factor), this.currency);
    }
    
    private void validateSameCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new InvalidMoneyException(
                "Moedas diferentes: %s e %s".formatted(this.currency, other.currency)
            );
        }
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public Currency getCurrency() {
        return currency;
    }
    
    public String getCurrencyCode() {
        return currency.getCurrencyCode();
    }
    
    /**
     * Verifica se o valor é zero.
     */
    public boolean isZero() {
        return amount.compareTo(BigDecimal.ZERO) == 0;
    }
    
    /**
     * Verifica se o valor é positivo (maior que zero).
     */
    public boolean isPositive() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }
    
    /**
     * Compara se este Money é maior que outro.
     */
    public boolean isGreaterThan(Money other) {
        validateSameCurrency(other);
        return this.amount.compareTo(other.amount) > 0;
    }
    
    /**
     * Compara se este Money é menor que outro.
     */
    public boolean isLessThan(Money other) {
        validateSameCurrency(other);
        return this.amount.compareTo(other.amount) < 0;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return amount.compareTo(money.amount) == 0 && 
               Objects.equals(currency, money.currency);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(amount.stripTrailingZeros(), currency);
    }
    
    @Override
    public String toString() {
        return "%s %s".formatted(currency.getCurrencyCode(), amount.toPlainString());
    }
}
