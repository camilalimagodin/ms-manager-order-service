-- V3: Create indexes for performance optimization
-- Índices para consultas frequentes

-- Index for status queries (Produto Externo B filters by status)
CREATE INDEX idx_orders_status ON orders(status);

-- Index for date range queries
CREATE INDEX idx_orders_created_at ON orders(created_at);

-- Composite index for common query pattern: status + date
CREATE INDEX idx_orders_status_created_at ON orders(status, created_at DESC);

-- Index for order items lookup by order
CREATE INDEX idx_order_items_order_id ON order_items(order_id);

-- Index for potential product analysis
CREATE INDEX idx_order_items_product_id ON order_items(product_id);

-- Comments
COMMENT ON INDEX idx_orders_status IS 'Otimiza consultas por status do Produto B';
COMMENT ON INDEX idx_orders_status_created_at IS 'Otimiza listagem paginada com filtro de status';
