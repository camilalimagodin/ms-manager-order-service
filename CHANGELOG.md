# Changelog

Todas as mudanças notáveis neste projeto serão documentadas neste arquivo.

O formato é baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/),
e este projeto adere ao [Versionamento Semântico](https://semver.org/lang/pt-BR/).

## [Não Lançado]

### Adicionado
- Configuração de variáveis de ambiente com arquivos `.env`
- Dependência `spring-dotenv` para carregar variáveis de ambiente automaticamente
- Arquivo `.env.example` como template de configuração
- Arquivo `.env.test` para ambiente de testes
- Documentação de configuração de ambiente em `docs/configuracao-ambiente.md`

### Modificado
- Docker Compose atualizado para usar variáveis do arquivo `.env`
- Arquivos de configuração Spring (`application*.yml`) para usar variáveis de ambiente
- Configuração de logging parametrizável via variáveis de ambiente

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
