package io.github.douglasdreer.order.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ExternalOrderId - Testes do Value Object de identificador externo de pedido")
class ExternalOrderIdTest {

    @Nested
    @DisplayName("Testes de criação")
    class CreationTests {

        @Test
        @DisplayName("Deve criar ExternalOrderId válido")
        void shouldCreateValidExternalOrderId() {
            // Act
            ExternalOrderId id = ExternalOrderId.of("EXT-12345");

            // Assert
            assertThat(id.getValue()).isEqualTo("EXT-12345");
        }

        @Test
        @DisplayName("Deve lançar exceção quando valor é null")
        void shouldThrowExceptionWhenValueIsNull() {
            // Act & Assert
            assertThatThrownBy(() -> ExternalOrderId.of(null))
                    .hasMessageContaining("não pode ser nulo");
        }

        @Test
        @DisplayName("Deve lançar exceção quando valor é vazio")
        void shouldThrowExceptionWhenValueIsEmpty() {
            // Act & Assert
            assertThatThrownBy(() -> ExternalOrderId.of(""))
                    .hasMessageContaining("não pode ser nulo ou vazio");
        }

        @Test
        @DisplayName("Deve lançar exceção quando valor é blank")
        void shouldThrowExceptionWhenValueIsBlank() {
            // Act & Assert
            assertThatThrownBy(() -> ExternalOrderId.of("   "))
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
            ExternalOrderId id1 = ExternalOrderId.of("EXT-001");
            ExternalOrderId id2 = ExternalOrderId.of("EXT-001");

            // Act & Assert
            assertThat(id1).isEqualTo(id2)
                    .hasSameHashCodeAs(id2);
        }

        @Test
        @DisplayName("Não deve ser igual quando valores são diferentes")
        void shouldNotBeEqualWhenValuesAreDifferent() {
            // Arrange
            ExternalOrderId id1 = ExternalOrderId.of("EXT-001");
            ExternalOrderId id2 = ExternalOrderId.of("EXT-002");

            // Act & Assert
            assertThat(id1).isNotEqualTo(id2);
        }

        @Test
        @DisplayName("Não deve ser igual a null")
        void shouldNotBeEqualToNull() {
            // Arrange
            ExternalOrderId id = ExternalOrderId.of("EXT-001");

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
            ExternalOrderId id = ExternalOrderId.of("EXT-001");

            // Act
            String result = id.toString();

            // Assert
            assertThat(result).contains("EXT-001");
        }
    }
}
