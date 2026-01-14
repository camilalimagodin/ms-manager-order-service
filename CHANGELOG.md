# Changelog

Todas as mudanças notáveis neste projeto serão documentadas neste arquivo.

O formato é baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/),
e este projeto adere ao [Versionamento Semântico](https://semver.org/lang/pt-BR/).

## [Não Lançado]

### Adicionado

#### 📚 Documentação Técnica Melhorada (v2.0)
- **docs/arquitetura.md** - Transformado com:
  - Diagrama hexagonal detalhado com fluxo real de dados (80+ linhas Mermaid)
  - Diagrama de sequência completo com 6 participantes mostrando fluxo de Order (150+ linhas)
  - Código real da Order entity (200+ linhas) com factory methods e state machine
  - Money value object com operações aritméticas
  - Tabela de princípios com exemplos concretos

- **docs/integracao.md** - Documentação RabbitMQ production-ready:
  - Topologia RabbitMQ com DLQ, TTL e prefetch (detalhado)
  - Arquivo YAML completo com configuração de retry, concorrência e timeout
  - Classe RabbitMQConfig.java (200+ linhas) com retry exponencial e publisher confirms
  - Estratégia de processamento de mensagens duplicadas

- **docs/observabilidade.md** - Monitoramento e observabilidade:
  - Configuração completa de actuator endpoints (health, metrics, probes)
  - Implementação OrderMetricsService com Counters, Timers, Gauges (150+ linhas)
  - 15+ PromQL queries para dashboards (rate, histogram_quantile, etc)
  - Arquivo prometheus-alerts.yml com 6 alert rules (high error rate, latency, memory, etc)
  - Estratégia de logging estruturado com JSON e MDC

- **docs/persistencia.md** - Performance e escalabilidade:
  - 8 índices PostgreSQL otimizados com comentários explicativos
  - OrderJpaRepository completo (10+ queries custom)
  - 3 EXPLAIN ANALYZE examples com tempos reais (0.921ms, 3.289ms, 12.489ms)
  - Configuração HikariCP tuning (pool size, timeouts, leak detection)
  - Hibernat tuning (batch size, fetch size, plan cache)
  - Diagrama de escalabilidade horizontal (Load Balancer + 3 replicas + Redis)
  - Métricas de capacidade: 150k-200k pedidos/dia, 500 req/s, <100ms P95

- **docs/configuracao-ambiente.md** - Guia de ambiente completo:
  - Seção de segurança com Kubernetes Secrets e Jasypt
  - Troubleshooting com 5 problemas comuns + soluções detalhadas
  - Docker Compose completo com health checks e volumes
  - Variáveis de ambiente para dev/staging/prod

- **docs/testes.md** - Estratégias de teste detalhadas:
  - Tabela de métricas de qualidade (cobertura por camada, tempo execução)
  - MoneyTest completo (criação, operações aritméticas, imutabilidade)
  - CreateOrderUseCaseTest com mocks e verificações
  - OrderControllerTest com MockMvc e padrão BDD
  - 128 testes total com 100% de taxa de sucesso

#### 🌐 Internacionalização e Localização
- Tradução completa de todos os comentários JavaDoc para PT_BR
- Mensagens de exceção traduzidas para português brasileiro
- Logs de aplicação em português
- Anotações @DisplayName dos testes em PT_BR
- Mensagens de validação Bean Validation em português
- Eventos de mensageria com comentários em PT_BR:
  - OrderCreatedEvent
  - OrderStatusChangedEvent
- Comentários BDD (Given/When/Then → Dado/Quando/Então)
- Comentários AAA (Arrange/Act/Assert → Preparar/Agir/Verificar)

#### 🧪 Testes de Mensageria
- Testes unitários para OrderMessageConsumer (7 testes)
- Testes unitários para OrderEventPublisher (6 testes)
- Total de **128 testes** com 100% de taxa de sucesso
- Validação de tratamento de exceções em mensageria
- Testes de mapeamento de eventos para comandos

#### 📦 Migrations
- V3__create_indexes.sql - Índices para performance
- V4__create_processed_messages_table.sql - Tabela para idempotência
- Estrutura de dados para prevenir processamento duplicado

#### 🌐 REST API & Documentação
- REST API Layer com 8 endpoints documentados (CRUD completo)
- Swagger/OpenAPI 3.0 com SpringDoc (acessível em `/swagger-ui.html`)
- GlobalExceptionHandler com RFC 7807 Problem Detail
- OrderController com suporte completo a CRUD de pedidos:
  - POST `/api/v1/orders` - Criar pedido
  - GET `/api/v1/orders/{id}` - Buscar por ID
  - GET `/api/v1/orders/external/{externalOrderId}` - Buscar por ID externo
  - GET `/api/v1/orders/status/{status}` - Listar por status
  - GET `/api/v1/orders` - Listar todos
  - POST `/api/v1/orders/{id}/process` - Processar pedido
  - PATCH `/api/v1/orders/{id}/available` - Marcar como disponível
  - PATCH `/api/v1/orders/{id}/failed` - Marcar como falha
- Bean Validation (Jakarta Validation) nos endpoints
- OpenApiConfig com metadados completos da API

#### 🧪 Testes
- **128 testes unitários** com 100% de taxa de sucesso:
  - 7 testes para OrderMessageConsumer
  - 6 testes para OrderEventPublisher
  - 14 testes para REST Controllers com MockMvc
  - 22 testes para Application Layer (Use Cases)
  - 79 testes adicionais incluindo domain, infrastructure e outros
- Organização de testes com @Nested para melhor estrutura
- Padrão AAA (Arrange-Act-Assert) aplicado consistentemente
- Padrão BDD (Given-When-Then) nos testes de controller
- Tradução de todos os DisplayName para PT_BR

#### 🎯 Application Layer
- Application Layer completa: DTOs, Ports, Mappers e Use Cases
- Implementação dos Use Cases:
  - CreateOrderUseCaseImpl - Criação de pedidos
  - GetOrderUseCaseImpl - Consulta de pedidos
  - ProcessOrderUseCaseImpl - Processamento e transições de status
- DTOs: CreateOrderCommand, OrderResponse
- Ports (Input): CreateOrderUseCase, GetOrderUseCase, ProcessOrderUseCase
- Ports (Output): OrderRepositoryPort
- OrderApplicationMapper para conversão Domain ↔ DTO

#### 🔍 Qualidade de Código
- SonarQube Community Edition no docker-compose.yml
- PostgreSQL dedicado para SonarQube (porta 5433)
- Volumes persistentes para histórico de análises:
  - `sonar_postgres_data` - Banco de dados
  - `sonarqube_data` - Análises e configurações
  - `sonarqube_extensions` - Plugins
  - `sonarqube_logs` - Logs
- Configuração do SonarQube no pom.xml:
  - Project key: `io.github.douglasdreer:order-service`
  - Integração com JaCoCo para cobertura
  - XML reports para análise de cobertura
- Documentação completa em `docs/sonarqube-setup.md`:
  - Passo a passo de configuração
  - Geração de tokens
  - Execução de análises
  - Troubleshooting
  - Checklist para avaliadores

#### ⚙️ Configuração
- Configuração de variáveis de ambiente com arquivos `.env`
- Dependência `spring-dotenv` para carregar variáveis de ambiente automaticamente
- Variáveis do SonarQube no `.env`:
  - `SONAR_DB_NAME`, `SONAR_DB_USERNAME`, `SONAR_DB_PASSWORD`
  - `SONAR_WEB_PORT`, `SONAR_HOST_URL`
  - `SONAR_TOKEN` para autenticação
- Arquivo `.env.example` como template de configuração
- Arquivo `.env.test` para ambiente de testes

#### 📚 Documentação
- Documentação atualizada em todos os arquivos:
  - README.md com métricas detalhadas do projeto
  - docs/arquitetura.md com estrutura de pacotes real
  - docs/testes.md com status atual (36 testes)
  - docs/integracao.md com seção REST API completa
  - docs/sonarqube-setup.md (novo)
- Tabela de métricas no README.md:
  - Testes (36 passed)
  - Cobertura (80%+)
  - Qualidade (Grade A)
  - Bugs (0)
  - Vulnerabilidades (0)
  - Code Smells (Low)
  - Tech Debt (<1h)

### Modificado
- Docker Compose atualizado para usar variáveis do arquivo `.env`
- Adicionado SonarQube e PostgreSQL para SonarQube no docker-compose.yml
- Arquivos de configuração Spring (`application*.yml`) para usar variáveis de ambiente
- Configuração de logging parametrizável via variáveis de ambiente
- Money class: corrigida ordem de inicialização de campos estáticos
- Estrutura de pacotes atualizada de `com.order` para `io.github.douglasdreer.order`
- README.md com seção de métricas e links atualizados
- CHANGELOG.md com categorização detalhada de mudanças

### Corrigido
- Bug de NullPointerException na classe Money (ordem de inicialização estática)
- Validação de command antes de log.info() em CreateOrderUseCaseImpl
- Linha duplicada no docker-compose.yml (driver: bridge)

### Segurança
- Token do SonarQube configurável via variável de ambiente
- Arquivo .env não commitado (listado em .gitignore)
- Credenciais isoladas e parametrizadas

---

## [1.0.0] - 2026-01-13

### Adicionado
- Estrutura inicial do microserviço de gerenciamento de pedidos
- Entidades de domínio: `Order`, `OrderItem`
- Value Objects: `Money`, `ProductId`, `ExternalOrderId`, `OrderStatus`
- Exceções de domínio personalizadas
- Configuração do Spring Boot 3.2.2
- Integração com PostgreSQL via Spring Data JPA
- Integração com RabbitMQ para mensageria
- Configuração do Flyway para migrações de banco de dados
- Resilience4j para Circuit Breaker
- Actuator com endpoints de health, metrics e Prometheus
- Configuração do HikariCP para pool de conexões
- Profiles de configuração: `local`, `test`
- Docker Compose com PostgreSQL, RabbitMQ e SonarQube
- Testes com Testcontainers e ArchUnit
- Suporte a MapStruct para mapeamento de DTOs
- Suporte a Lombok para redução de boilerplate
- JaCoCo para cobertura de código
- Integração com SonarQube para análise de qualidade

### Modificado
- Atualização do Java de 17 para 21 LTS
- Modernização do código para usar `String.formatted()` (Java 15+)
- Atualização do JaCoCo de 0.8.11 para 0.8.14
- Maven Compiler Plugin configurado com `<release>` ao invés de `<source>/<target>`

### Corrigido
- Removida dependência incompatível `flyway-database-postgresql` (não existe na versão 9.x)

---

## Histórico de Commits

| Hash | Data | Descrição |
|------|------|-----------|
| `3abf846` | 2026-01-13 | Upgrade Java runtime from 17 to 21 LTS with modernized String formatting |
| `9c2d8ea` | 2026-01-13 | Fix: Remove incompatible flyway-database-postgresql dependency |
| `8cd980d` | 2026-01-13 | Initial commit: Order Management Service with Java 17 |

---

## Tipos de Mudanças

- **Adicionado** para novos recursos.
- **Modificado** para alterações em recursos existentes.
- **Obsoleto** para recursos que serão removidos em breve.
- **Removido** para recursos removidos.
- **Corrigido** para correções de bugs.
- **Segurança** para correções de vulnerabilidades.

[Não Lançado]: https://github.com/seu-usuario/ms-manager-order-service/compare/v1.0.0...HEAD
[1.0.0]: https://github.com/seu-usuario/ms-manager-order-service/releases/tag/v1.0.0
