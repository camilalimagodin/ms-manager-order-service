package io.github.douglasdreer.order.domain.valueobject;

import io.github.douglasdreer.order.domain.exception.InvalidMoneyException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Money - Testes do Value Object de valores monetários")
class MoneyTest {

    @Nested
    @DisplayName("Testes de criação")
    class CreationTests {

        @Test
        @DisplayName("Deve criar Money com valor e moeda")
        void shouldCreateMoneyWithAmountAndCurrency() {
            // Act
            Money money = Money.of(BigDecimal.valueOf(100), Currency.getInstance("BRL"));

            // Assert
            assertThat(money.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(100.00));
            assertThat(money.getCurrency().getCurrencyCode()).isEqualTo("BRL");
        }

        @Test
        @DisplayName("Deve criar Money com valor usando moeda padrão")
        void shouldCreateMoneyWithDefaultCurrency() {
            // Act
            Money money = Money.of(BigDecimal.valueOf(50));

            // Assert
            assertThat(money.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(50.00));
            assertThat(money.getCurrency().getCurrencyCode()).isEqualTo("BRL");
        }

        @Test
        @DisplayName("Deve criar Money a partir de double")
        void shouldCreateMoneyFromDouble() {
            // Act
            Money money = Money.of(75.50);

            // Assert
            assertThat(money.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(75.50));
        }

        @Test
        @DisplayName("Deve criar Money a partir de String")
        void shouldCreateMoneyFromString() {
            // Act
            Money money = Money.of("125.75");

            // Assert
            assertThat(money.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(125.75));
        }

        @Test
        @DisplayName("Deve criar Money zero")
        void shouldCreateZeroMoney() {
            // Act
            Money zero = Money.ZERO;

            // Assert
            assertThat(zero.getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(zero.isZero()).isTrue();
        }

        @Test
        @DisplayName("Deve arredondar para 2 casas decimais")
        void shouldRoundToTwoDecimals() {
            // Act
            Money money = Money.of(BigDecimal.valueOf(10.999));

            // Assert
            assertThat(money.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(11.00));
        }

        @Test
        @DisplayName("Deve lançar exceção quando valor é null")
        void shouldThrowExceptionWhenAmountIsNull() {
            // Act & Assert
            assertThatThrownBy(() -> Money.of((BigDecimal) null))
                    .isInstanceOf(InvalidMoneyException.class)
                    .hasMessageContaining("O valor não pode ser nulo");
        }

        @Test
        @DisplayName("Deve lançar exceção quando valor é negativo")
        void shouldThrowExceptionWhenAmountIsNegative() {
            // Arrange
            BigDecimal negativeAmount = BigDecimal.valueOf(-10);
            
            // Act & Assert
            assertThatThrownBy(() -> Money.of(negativeAmount))
                    .isInstanceOf(InvalidMoneyException.class)
                    .hasMessageContaining("O valor não pode ser negativo");
        }
    }

    @Nested
    @DisplayName("Testes de operações aritméticas")
    class ArithmeticOperationsTests {

        @Test
        @DisplayName("Deve somar dois valores monetários")
        void shouldAddTwoMoneyValues() {
            // Arrange
            Money money1 = Money.of(BigDecimal.valueOf(100));
            Money money2 = Money.of(BigDecimal.valueOf(50));

            // Act
            Money result = money1.add(money2);

            // Assert
            assertThat(result.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(150.00));
        }

        @Test
        @DisplayName("Deve subtrair dois valores monetários")
        void shouldSubtractTwoMoneyValues() {
            // Arrange
            Money money1 = Money.of(BigDecimal.valueOf(100));
            Money money2 = Money.of(BigDecimal.valueOf(30));

            // Act
            Money result = money1.subtract(money2);

            // Assert
            assertThat(result.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(70.00));
        }

        @Test
        @DisplayName("Deve multiplicar por quantidade")
        void shouldMultiplyByQuantity() {
            // Arrange
            Money money = Money.of(BigDecimal.valueOf(50));

            // Act
            Money result = money.multiply(3);

            // Assert
            assertThat(result.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(150.00));
        }

        @Test
        @DisplayName("Deve multiplicar por BigDecimal")
        void shouldMultiplyByBigDecimal() {
            // Arrange
            Money money = Money.of(BigDecimal.valueOf(100));

            // Act
            Money result = money.multiply(BigDecimal.valueOf(1.5));

            // Assert
            assertThat(result.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(150.00));
        }

        @Test
        @DisplayName("Deve lançar exceção ao somar moedas diferentes")
        void shouldThrowExceptionWhenAddingDifferentCurrencies() {
            // Arrange
            Money brl = Money.of(BigDecimal.valueOf(100), Currency.getInstance("BRL"));
            Money usd = Money.of(BigDecimal.valueOf(50), Currency.getInstance("USD"));

            // Act & Assert
            assertThatThrownBy(() -> brl.add(usd))
                    .isInstanceOf(InvalidMoneyException.class)
                    .hasMessageContaining("Moedas diferentes");
        }
    }

    @Nested
    @DisplayName("Testes de comparação")
    class ComparisonTests {

        @Test
        @DisplayName("Deve verificar se é maior que outro valor")
        void shouldCheckIfGreaterThan() {
            // Arrange
            Money money1 = Money.of(BigDecimal.valueOf(100));
            Money money2 = Money.of(BigDecimal.valueOf(50));

            // Act & Assert
            assertThat(money1.isGreaterThan(money2)).isTrue();
            assertThat(money2.isGreaterThan(money1)).isFalse();
        }

        @Test
        @DisplayName("Deve verificar se é menor que outro valor")
        void shouldCheckIfLessThan() {
            // Arrange
            Money money1 = Money.of(BigDecimal.valueOf(50));
            Money money2 = Money.of(BigDecimal.valueOf(100));

            // Act & Assert
            assertThat(money1.isLessThan(money2)).isTrue();
            assertThat(money2.isLessThan(money1)).isFalse();
        }

        @Test
        @DisplayName("Deve verificar se é zero")
        void shouldCheckIfZero() {
            // Arrange
            Money zero = Money.ZERO;
            Money nonZero = Money.of(BigDecimal.valueOf(10));

            // Act & Assert
            assertThat(zero.isZero()).isTrue();
            assertThat(nonZero.isZero()).isFalse();
        }

        @Test
        @DisplayName("Deve verificar se é positivo")
        void shouldCheckIfPositive() {
            // Arrange
            Money positive = Money.of(BigDecimal.valueOf(100));
            Money zero = Money.ZERO;

            // Act & Assert
            assertThat(positive.isPositive()).isTrue();
            assertThat(zero.isPositive()).isFalse();
        }
    }

    @Nested
    @DisplayName("Testes de equals e hashCode")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("Deve ser igual quando valores e moedas são iguais")
        void shouldBeEqualWhenAmountAndCurrencyAreEqual() {
            // Arrange
            Money money1 = Money.of(BigDecimal.valueOf(100));
            Money money2 = Money.of(BigDecimal.valueOf(100));

            // Act & Assert
            assertThat(money1).isEqualTo(money2)
                    .hasSameHashCodeAs(money2);
        }

        @Test
        @DisplayName("Não deve ser igual quando valores são diferentes")
        void shouldNotBeEqualWhenAmountsAreDifferent() {
            // Arrange
            Money money1 = Money.of(BigDecimal.valueOf(100));
            Money money2 = Money.of(BigDecimal.valueOf(50));

            // Act & Assert
            assertThat(money1).isNotEqualTo(money2);
        }

        @Test
        @DisplayName("Não deve ser igual quando moedas são diferentes")
        void shouldNotBeEqualWhenCurrenciesAreDifferent() {
            // Arrange
            Money brl = Money.of(BigDecimal.valueOf(100), Currency.getInstance("BRL"));
            Money usd = Money.of(BigDecimal.valueOf(100), Currency.getInstance("USD"));

            // Act & Assert
            assertThat(brl).isNotEqualTo(usd);
        }
    }

    @Nested
    @DisplayName("Testes de formatação")
    class FormattingTests {

        @Test
        @DisplayName("Deve formatar como string com símbolo da moeda")
        void shouldFormatAsStringWithCurrencySymbol() {
            // Arrange
            Money money = Money.of(BigDecimal.valueOf(100));

            // Act
            String formatted = money.toString();

            // Assert
            assertThat(formatted).contains("100")
                    .contains("BRL");
        }
    }
}
