package com.order.domain.valueobject;

import com.order.domain.exception.InvalidProductIdException;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object que representa o identificador de um produto.
 */
public final class ProductId {
    
    private static final int MAX_LENGTH = 100;
    private static final Pattern VALID_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$");
    
    private final String value;
    
    private ProductId(String value) {
        this.value = value;
    }
    
    public static ProductId of(String value) {
        validate(value);
        return new ProductId(value.trim());
    }
    
    private static void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidProductIdException("ProductId não pode ser nulo ou vazio");
        }
        
        String trimmedValue = value.trim();
        
        if (trimmedValue.length() > MAX_LENGTH) {
            throw new InvalidProductIdException(
                String.format("ProductId não pode exceder %d caracteres", MAX_LENGTH)
            );
        }
        
        if (!VALID_PATTERN.matcher(trimmedValue).matches()) {
            throw new InvalidProductIdException(
                "ProductId deve conter apenas caracteres alfanuméricos, hífen e underscore"
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
        ProductId productId = (ProductId) o;
        return Objects.equals(value, productId.value);
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
