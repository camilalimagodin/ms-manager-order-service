-- V4: Create table for message deduplication
-- Cache de mensagens processadas para garantir idempotência

CREATE TABLE processed_messages (
    message_id VARCHAR(255) PRIMARY KEY,
    external_order_id VARCHAR(100) NOT NULL,
    processed_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Index for cleanup job
    CONSTRAINT idx_processed_messages_processed_at 
        CHECK (processed_at IS NOT NULL)
);

-- Index for TTL cleanup
CREATE INDEX idx_processed_messages_processed_at ON processed_messages(processed_at);

-- Comment
COMMENT ON TABLE processed_messages IS 'Cache de mensagens para deduplicação - TTL de 24h';
