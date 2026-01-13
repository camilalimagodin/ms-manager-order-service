-- ============================================================================
-- V2__create_order_items_table.sql
-- Criação da tabela de itens do pedido
-- ============================================================================

CREATE TABLE IF NOT EXISTS order_items (
    -- Identificador único interno (UUID)
    id UUID PRIMARY KEY,
    
    -- Referência ao pedido (FK)
    order_id UUID NOT NULL,
    
    -- Identificador do produto
    product_id VARCHAR(100) NOT NULL,
    
    -- Nome do produto
    product_name VARCHAR(255) NOT NULL,
    
    -- Preço unitário
    unit_price DECIMAL(19, 4) NOT NULL,
    
    -- Moeda do preço unitário (ISO 4217)
    unit_currency VARCHAR(3) NOT NULL DEFAULT 'BRL',
    
    -- Quantidade
    quantity INTEGER NOT NULL,
    
    -- Subtotal (unit_price * quantity)
    subtotal DECIMAL(19, 4) NOT NULL,
    
    -- Moeda do subtotal (ISO 4217)
    subtotal_currency VARCHAR(3) NOT NULL DEFAULT 'BRL',
    
    -- Timestamp de criação
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign Key
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) 
        REFERENCES orders (id) ON DELETE CASCADE,
    
    -- Constraints de validação
    CONSTRAINT chk_order_items_quantity CHECK (quantity > 0),
    CONSTRAINT chk_order_items_unit_price CHECK (unit_price >= 0),
    CONSTRAINT chk_order_items_subtotal CHECK (subtotal >= 0)
);

-- Índices para otimização de consultas
CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON order_items (order_id);
CREATE INDEX IF NOT EXISTS idx_order_items_product_id ON order_items (product_id);

-- Comentários na tabela
COMMENT ON TABLE order_items IS 'Tabela de itens dos pedidos';
COMMENT ON COLUMN order_items.id IS 'Identificador único interno (UUID)';
COMMENT ON COLUMN order_items.order_id IS 'Referência ao pedido';
COMMENT ON COLUMN order_items.product_id IS 'Identificador do produto';
COMMENT ON COLUMN order_items.product_name IS 'Nome do produto';
COMMENT ON COLUMN order_items.unit_price IS 'Preço unitário do produto';
COMMENT ON COLUMN order_items.unit_currency IS 'Código da moeda do preço unitário';
COMMENT ON COLUMN order_items.quantity IS 'Quantidade do item';
COMMENT ON COLUMN order_items.subtotal IS 'Subtotal do item (preço * quantidade)';
COMMENT ON COLUMN order_items.subtotal_currency IS 'Código da moeda do subtotal';
COMMENT ON COLUMN order_items.created_at IS 'Data/hora de criação do registro';
