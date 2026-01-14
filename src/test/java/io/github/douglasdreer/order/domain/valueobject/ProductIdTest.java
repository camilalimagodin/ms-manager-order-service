package io.github.douglasdreer.order.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ProductId - Testes do Value Object de identificador de produto")
class ProductIdTest {

    @Nested
    @DisplayName("Testes de criação")
    class CreationTests {

        @Test
        @DisplayName("Deve criar ProductId válido")
        void shouldCreateValidProductId() {
            // Act
            ProductId productId = ProductId.of("PROD-001");

            // Assert
            assertThat(productId.getValue()).isEqualTo("PROD-001");
        }

        @Test
        @DisplayName("Deve lançar exceção quando valor é null")
        void shouldThrowExceptionWhenValueIsNull() {
            // Act & Assert
            assertThatThrownBy(() -> ProductId.of(null))
                    .hasMessageContaining("não pode ser nulo");
        }

        @Test
        @DisplayName("Deve lançar exceção quando valor é vazio")
        void shouldThrowExceptionWhenValueIsEmpty() {
            // Act & Assert
            assertThatThrownBy(() -> ProductId.of(""))
                    .hasMessageContaining("não pode ser nulo ou vazio");
        }

        @Test
        @DisplayName("Deve lançar exceção quando valor é blank")
        void shouldThrowExceptionWhenValueIsBlank() {
            // Act & Assert
            assertThatThrownBy(() -> ProductId.of("   "))
                    .hasMessageContaining("não pode ser nulo ou vazio");
        }
    }

    @Nested
    @DisplayName("Testes de equals e hashCode")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("Deve ser igual quando valores são iguais")
        void shouldBeEqualWhenValuesAreEqual() {
            // Arrange
            ProductId id1 = ProductId.of("PROD-001");
            ProductId id2 = ProductId.of("PROD-001");

            // Act & Assert
            assertThat(id1).isEqualTo(id2)
                    .hasSameHashCodeAs(id2);
        }

        @Test
        @DisplayName("Não deve ser igual quando valores são diferentes")
        void shouldNotBeEqualWhenValuesAreDifferent() {
            // Arrange
            ProductId id1 = ProductId.of("PROD-001");
            ProductId id2 = ProductId.of("PROD-002");

            // Act & Assert
            assertThat(id1).isNotEqualTo(id2);
        }

        @Test
        @DisplayName("Não deve ser igual a null")
        void shouldNotBeEqualToNull() {
            // Arrange
            ProductId id = ProductId.of("PROD-001");

            // Act & Assert
            assertThat(id).isNotNull();
        }


    }

    @Nested
    @DisplayName("Testes de toString")
    class ToStringTests {

        @Test
        @DisplayName("Deve retornar representação em string")
        void shouldReturnStringRepresentation() {
            // Arrange
            ProductId id = ProductId.of("PROD-001");

            // Act
            String result = id.toString();

            // Assert
            assertThat(result).contains("PROD-001");
        }
    }
}
