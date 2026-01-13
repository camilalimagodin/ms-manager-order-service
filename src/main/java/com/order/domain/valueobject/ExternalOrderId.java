package com.order.domain.valueobject;

import com.order.domain.exception.InvalidExternalOrderIdException;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object que representa o identificador externo de um pedido.
 * 
 * Validações:
 * - Não pode ser nulo ou vazio
 * - Tamanho máximo: 100 caracteres
 * - Formato alfanumérico com hífen e underscore permitidos
 */
public final class ExternalOrderId {
    
    private static final int MAX_LENGTH = 100;
    private static final Pattern VALID_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$");
    
    private final String value;
    
    private ExternalOrderId(String value) {
        this.value = value;
    }
    
    /**
     * Cria uma instância de ExternalOrderId.
     * 
     * @param value valor do ID externo
     * @return instância validada
     * @throws InvalidExternalOrderIdException se o valor for inválido
     */
    public static ExternalOrderId of(String value) {
        validate(value);
        return new ExternalOrderId(value.trim());
    }
    
    private static void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidExternalOrderIdException("ExternalOrderId não pode ser nulo ou vazio");
        }
        
        String trimmedValue = value.trim();
        
        if (trimmedValue.length() > MAX_LENGTH) {
            throw new InvalidExternalOrderIdException(
                "ExternalOrderId não pode exceder %d caracteres: %d".formatted(
                    MAX_LENGTH, trimmedValue.length())
            );
        }
        
        if (!VALID_PATTERN.matcher(trimmedValue).matches()) {
            throw new InvalidExternalOrderIdException(
                "ExternalOrderId deve conter apenas caracteres alfanuméricos, hífen e underscore: " + trimmedValue
            );
        }
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExternalOrderId that = (ExternalOrderId) o;
        return Objects.equals(value, that.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return value;
    }
}
