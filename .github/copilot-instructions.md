# Copilot Instructions - Order Management Service

## Visão Geral
Microserviço de **gerenciamento de pedidos** de alta volumetria (150k-200k pedidos/dia) construído com Java Spring Boot. Recebe pedidos do Produto Externo A via RabbitMQ, calcula totais e expõe consultas para o Produto Externo B via REST.

## Arquitetura

### Clean Architecture
```
src/main/java/com/order/
├── domain/              # Entidades, Value Objects, Domain Services, Repository Ports
├── application/         # Use Cases, Input/Output Ports, DTOs internos
├── infrastructure/      # JPA, RabbitMQ, Configurações Spring
└── interfaces/          # Controllers REST, Exception Handlers
```

**Regra de dependência**: Domain ← Application ← Infrastructure/Interfaces

### Fluxo de Dados
```
Produto A → RabbitMQ → Consumer → UseCase → Domain → Repository → PostgreSQL
                                                                      ↓
Produto B ← REST API ← Controller ← UseCase ← Domain ←─────────────────┘
```

## Convenções de Código

### Entidades de Domínio
- Entidades ricas com comportamento (`Order.calculateTotal()`)
- Value Objects imutáveis (`Money`, `ExternalOrderId`)
- Status como enum: `RECEIVED → PROCESSING → CALCULATED → AVAILABLE`

### Persistência
- Entidades JPA em `infrastructure/persistence/entity/`
- `@Version` para optimistic locking
- `external_order_id` com constraint UNIQUE para idempotência
- `BigDecimal(19,2)` para valores monetários

### Cálculo de Totais
```java
// SEMPRE usar BigDecimal, NUNCA double
totalAmount = items.stream()
    .map(item -> item.getUnitPrice().multiply(item.getQuantity()))
    .reduce(Money.ZERO, Money::add);
```

## Integração RabbitMQ

### Topologia
- Exchange: `order.exchange` (topic)
- Queue: `order.created.queue`
- Retry Queues: `order.retry.queue.5s`, `order.retry.queue.30s`, `order.retry.queue.5min`
- DLQ: `order.created.dlq`
- Parking Queue: `order.parking.queue`
- Routing Key: `order.created`

### Consumer Pattern com Circuit Breaker
```java
@RabbitListener(queues = "order.created.queue")
public void consume(Message message, Channel channel) {
    // 1. Verificar Circuit Breaker - se OPEN, NACK com requeue
    // 2. Manual ACK apenas após persistência
    // 3. Idempotência via external_order_id
    // 4. Erros transientes → Retry Queue com backoff (5s → 30s → 5min)
    // 5. Erros de validação → DLQ direto (não adianta retry)
    // 6. Após 3 retries → DLQ
}
```

### Circuit Breaker (Resilience4j)
```yaml
resilience4j.circuitbreaker.instances.rabbitMQConsumer:
  failureRateThreshold: 50      # Abre após 50% de falhas
  slidingWindowSize: 10         # Janela de 10 chamadas
  waitDurationInOpenState: 30s  # Tempo em OPEN antes de HALF_OPEN
```

### DLQ Avançada - Retry Progressivo
```
Tentativa 1 → Erro → Retry Queue 5s   → volta para main queue
Tentativa 2 → Erro → Retry Queue 30s  → volta para main queue
Tentativa 3 → Erro → Retry Queue 5min → volta para main queue
Tentativa 4 → Erro → DLQ (análise manual)
```

### Boas Práticas Obrigatórias
- `acknowledge-mode: manual` - Controle total do ACK
- `prefetch: 10` - Limitar mensagens em memória
- `publisher-confirm-type: correlated` - Garantir entrega ao broker
- Message deduplication via `messageId` + Redis/cache
- Graceful shutdown - Parar consumers antes de desligar

## API REST (Produto B)

| Endpoint | Descrição |
|----------|-----------|
| `GET /api/v1/orders?status=AVAILABLE` | Lista com paginação |
| `GET /api/v1/orders/{id}` | Detalhe por ID |
| `GET /api/v1/orders/external/{externalOrderId}` | Busca por ID externo |

## Testes

### Estrutura
- **Unitários**: `src/test/java/**/unit/` - Domínio e Use Cases com Mockito
- **Integração**: `src/test/java/**/integration/` - Testcontainers (PostgreSQL, RabbitMQ)
- **Arquitetura**: ArchUnit para validar Clean Architecture

### Comandos
```bash
./mvnw test                              # Testes unitários
./mvnw verify                            # Testes de integração
./mvnw verify jacoco:report              # Com cobertura
./mvnw sonar:sonar -Dsonar.token=XXX     # SonarQube
```

### Metas de Qualidade
- Cobertura: >80% domínio e use cases
- SonarQube: 0 bugs, 0 vulnerabilidades

## Docker

```bash
docker-compose up -d                     # PostgreSQL, RabbitMQ, SonarQube
./mvnw spring-boot:run -Dspring.profiles.active=local
```

## Documentação
Toda documentação técnica em `docs/`:
- [arquitetura.md](docs/arquitetura.md) - Clean Architecture, ADRs, SOLID
- [integracao.md](docs/integracao.md) - RabbitMQ, REST API, contratos
- [persistencia.md](docs/persistencia.md) - DER, migrations, otimizações
- [testes.md](docs/testes.md) - Estratégia, Testcontainers, SonarQube
- [observabilidade.md](docs/observabilidade.md) - Logs, métricas, resiliência

## Padrões Importantes

### Idempotência
Duplicatas são tratadas via `external_order_id` único - retorna pedido existente sem erro.

### Resiliência
- Retry com backoff exponencial para erros transientes
- Circuit breaker para dependências externas
- DLQ para mensagens que falharam após max retries

### Observabilidade
- Logs estruturados JSON com `correlationId`
- Métricas Prometheus: `orders.processed.total`, `orders.processing.duration`
- Health checks: `/actuator/health/liveness`, `/actuator/health/readiness`
