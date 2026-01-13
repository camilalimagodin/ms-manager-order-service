-- ============================================================================
-- V1__create_orders_table.sql
-- Criação da tabela de pedidos
-- ============================================================================

CREATE TABLE IF NOT EXISTS orders (
    -- Identificador único interno (UUID)
    id UUID PRIMARY KEY,
    
    -- Identificador externo do pedido (vem do sistema origem)
    external_order_id VARCHAR(100) NOT NULL,
    
    -- Valor total do pedido
    total_amount DECIMAL(19, 4) NOT NULL DEFAULT 0,
    
    -- Moeda do valor total (ISO 4217)
    total_currency VARCHAR(3) NOT NULL DEFAULT 'BRL',
    
    -- Status do pedido
    status VARCHAR(20) NOT NULL DEFAULT 'RECEIVED',
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Controle de concorrência otimista
    version BIGINT NOT NULL DEFAULT 0,
    
    -- Constraints
    CONSTRAINT uq_orders_external_id UNIQUE (external_order_id),
    CONSTRAINT chk_orders_status CHECK (status IN ('RECEIVED', 'PROCESSING', 'CALCULATED', 'AVAILABLE', 'FAILED'))
);

-- Índices para otimização de consultas
CREATE INDEX IF NOT EXISTS idx_orders_external_id ON orders (external_order_id);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders (status);
CREATE INDEX IF NOT EXISTS idx_orders_created_at ON orders (created_at);

-- Comentários na tabela
COMMENT ON TABLE orders IS 'Tabela de pedidos do sistema';
COMMENT ON COLUMN orders.id IS 'Identificador único interno (UUID)';
COMMENT ON COLUMN orders.external_order_id IS 'Identificador externo do pedido vindo do sistema origem';
COMMENT ON COLUMN orders.total_amount IS 'Valor total do pedido';
COMMENT ON COLUMN orders.total_currency IS 'Código da moeda (ISO 4217)';
COMMENT ON COLUMN orders.status IS 'Status atual do pedido';
COMMENT ON COLUMN orders.created_at IS 'Data/hora de criação do registro';
COMMENT ON COLUMN orders.updated_at IS 'Data/hora da última atualização';
COMMENT ON COLUMN orders.version IS 'Versão para controle de concorrência otimista';
