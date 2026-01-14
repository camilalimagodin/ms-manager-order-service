# 🛒 Order Management Service

<p align="center">
  <img src="https://img.shields.io/badge/Java-21_LTS-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-3.2.2-6DB33F?style=for-the-badge&logo=spring&logoColor=white" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/PostgreSQL-15-4169E1?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL"/>
  <img src="https://img.shields.io/badge/RabbitMQ-3.12-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white" alt="RabbitMQ"/>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Flyway-9.22-CC0200?style=for-the-badge&logo=flyway&logoColor=white" alt="Flyway"/>
  <img src="https://img.shields.io/badge/JUnit-5-25A162?style=for-the-badge&logo=junit5&logoColor=white" alt="JUnit 5"/>
  <img src="https://img.shields.io/badge/SonarQube-10-4E9BCD?style=for-the-badge&logo=sonarqube&logoColor=white" alt="SonarQube"/>
  <img src="https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker"/>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Testcontainers-1.19-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Testcontainers"/>
  <img src="https://img.shields.io/badge/Resilience4j-2.2-000000?style=for-the-badge&logo=resilience4j&logoColor=white" alt="Resilience4j"/>
  <img src="https://img.shields.io/badge/Prometheus-Metrics-E6522C?style=for-the-badge&logo=prometheus&logoColor=white" alt="Prometheus"/>
  <img src="https://img.shields.io/badge/Maven-3.9-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" alt="Maven"/>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/License-MIT-yellow.svg?style=flat-square" alt="License"/>
  <img src="https://img.shields.io/badge/PRs-Welcome-brightgreen.svg?style=flat-square" alt="PRs Welcome"/>
  <img src="https://img.shields.io/badge/Maintained-Yes-green.svg?style=flat-square" alt="Maintained"/>
</p>

<p align="center">
  <b>🚀 Microserviço de gerenciamento de pedidos de alta volumetria</b>
</p>

---

## � Métricas do Projeto

<table>
  <tr>
    <td align="center">
      <b>🧪 Testes</b><br>
      <img src="https://img.shields.io/badge/Tests-36%20passed-success?style=flat-square" alt="Tests"/><br>
      <small>100% Success Rate</small>
    </td>
    <td align="center">
      <b>📈 Cobertura</b><br>
      <img src="https://img.shields.io/badge/Coverage-80%25+-success?style=flat-square" alt="Coverage"/><br>
      <small>JaCoCo + SonarQube</small>
    </td>
    <td align="center">
      <b>🔍 Qualidade</b><br>
      <img src="https://img.shields.io/badge/Quality-A-brightgreen?style=flat-square" alt="Quality"/><br>
      <small>SonarQube Analysis</small>
    </td>
    <td align="center">
      <b>🐛 Bugs</b><br>
      <img src="https://img.shields.io/badge/Bugs-0-success?style=flat-square" alt="Bugs"/><br>
      <small>Zero Tolerance</small>
    </td>
  </tr>
  <tr>
    <td align="center">
      <b>🛡️ Vulnerabilidades</b><br>
      <img src="https://img.shields.io/badge/Vulnerabilities-0-success?style=flat-square" alt="Vulnerabilities"/><br>
      <small>Security First</small>
    </td>
    <td align="center">
      <b>📦 Code Smells</b><br>
      <img src="https://img.shields.io/badge/Code%20Smells-Low-green?style=flat-square" alt="Code Smells"/><br>
      <small>Clean Code</small>
    </td>
    <td align="center">
      <b>🎯 Tech Debt</b><br>
      <img src="https://img.shields.io/badge/Tech%20Debt-%3C1h-success?style=flat-square" alt="Tech Debt"/><br>
      <small>Maintainability</small>
    </td>
    <td align="center">
      <b>📐 Arquitetura</b><br>
      <img src="https://img.shields.io/badge/Architecture-Hexagonal-blue?style=flat-square" alt="Architecture"/><br>
      <small>Clean Architecture</small>
    </td>
  </tr>
</table>

### 📋 Resumo Técnico

| Métrica | Valor | Status |
|---------|-------|--------|
| **Linhas de Código** | ~2.500 | 📝 |
| **Testes Unitários** | 36 | ✅ 100% Passing |
| **Cobertura de Testes** | 80%+ | ✅ Acima do mínimo |
| **Classes de Domínio** | 8 | 🎯 DDD |
| **Use Cases** | 3 | 🔄 CQRS |
| **REST Endpoints** | 8 | 🌐 RESTful |
| **Migrations** | 2 | 🗄️ Versionado |
| **Complexidade Ciclomática** | < 10 | ✅ Baixa |
| **Duplicação de Código** | < 3% | ✅ Mínima |

---

## �📋 Sobre o Projeto

O **Order Management Service** é um microserviço robusto desenvolvido para gerenciar pedidos em sistemas de alta demanda. Construído com as melhores práticas de arquitetura de software, oferece alta disponibilidade, resiliência e escalabilidade.

### ✨ Principais Características

| Característica | Descrição |
|----------------|-----------|
| 🚀 **Alta Performance** | Otimizado para processar grandes volumes de pedidos |
| 🔄 **Mensageria Assíncrona** | RabbitMQ para processamento desacoplado |
| 🛡️ **Resiliência** | Circuit Breaker com Resilience4j |
| 📊 **Observabilidade** | Métricas Prometheus + Health checks |
| 🗄️ **Migrations** | Flyway para versionamento de banco de dados |
| 🧪 **Testes Robustos** | JUnit 5 + Testcontainers + ArchUnit |
| 🔍 **Qualidade de Código** | SonarQube + JaCoCo para cobertura |
| 🐳 **Cloud Native** | 100% containerizado e pronto para K8s |

---

## 🏗️ Arquitetura

```
┌─────────────────────────────────────────────────────────────────┐
│                        Order Service                            │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐  │
│  │   REST API  │  │  Messaging  │  │      Actuator           │  │
│  │   (Web)     │  │  (RabbitMQ) │  │  (Health/Metrics)       │  │
│  └──────┬──────┘  └──────┬──────┘  └───────────┬─────────────┘  │
│         │                │                     │                │
│  ┌──────┴────────────────┴─────────────────────┴─────────────┐  │
│  │                    Application Layer                      │  │
│  │              (Use Cases / Services)                       │  │
│  └──────────────────────────┬────────────────────────────────┘  │
│                             │                                   │
│  ┌──────────────────────────┴────────────────────────────────┐  │
│  │                      Domain Layer                         │  │
│  │         (Entities / Value Objects / Exceptions)           │  │
│  └──────────────────────────┬────────────────────────────────┘  │
│                             │                                   │
│  ┌──────────────────────────┴────────────────────────────────┐  │
│  │                  Infrastructure Layer                     │  │
│  │        (Repositories / Messaging / Config)                │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
         │                           │
         ▼                           ▼
┌─────────────────┐        ┌─────────────────┐
│   PostgreSQL    │        │    RabbitMQ     │
│   (Database)    │        │   (Message      │
│                 │        │    Broker)      │
└─────────────────┘        └─────────────────┘
```

---

## 🚀 Começando

### Pré-requisitos

Antes de começar, você vai precisar ter instalado:

| Ferramenta | Versão | Obrigatório |
|------------|--------|-------------|
| ☕ Java | 21+ | ✅ |
| 🐳 Docker | 20+ | ✅ |
| 📦 Maven | 3.9+ | ❌ (wrapper incluso) |

### ⚡ Quick Start

```bash
# 1. Clone o repositório
git clone https://github.com/seu-usuario/ms-manager-order-service.git
cd ms-manager-order-service

# 2. Configure o ambiente
cp .env.example .env

# 3. Suba os serviços
docker-compose up -d

# 4. Execute a aplicação
./mvnw spring-boot:run
```

🎉 **Pronto!** Acesse: http://localhost:8080

---

## 🔍 Endpoints & Serviços

### REST API

| Endpoint | Método | Descrição |
|----------|--------|------------|
| 📄 Swagger UI | http://localhost:8080/swagger-ui.html | Documentação interativa |
| 📋 OpenAPI | http://localhost:8080/v3/api-docs | Especificação OpenAPI |
| 📦 Criar Pedido | POST /api/v1/orders | Cria novo pedido |
| 🔍 Buscar por ID | GET /api/v1/orders/{id} | Busca pedido por ID |
| 🔍 Buscar por ID Externo | GET /api/v1/orders/external/{externalOrderId} | Busca por ID externo |
| 📊 Listar por Status | GET /api/v1/orders/status/{status} | Lista pedidos por status |
| 📋 Listar Todos | GET /api/v1/orders | Lista todos os pedidos |
| ⚙️ Processar | POST /api/v1/orders/{id}/process | Processa pedido |
| ✅ Marcar Disponível | PATCH /api/v1/orders/{id}/available | Marca como disponível |
| ❌ Marcar Falha | PATCH /api/v1/orders/{id}/failed | Marca como falha |

### Health & Monitoring

| Serviço | URL | Descrição |
|---------|-----|-----------|
| 🏥 Health | http://localhost:8080/actuator/health | Status da aplicação |
| 📊 Métricas | http://localhost:8080/actuator/metrics | Métricas do sistema |
| 📈 Prometheus | http://localhost:8080/actuator/prometheus | Métricas formatadas |
| ℹ️ Info | http://localhost:8080/actuator/info | Informações da app |

### Serviços de Infraestrutura

| Serviço | URL | Credenciais |
|---------|-----|-------------|
| 🐰 RabbitMQ | http://localhost:15672 | guest / guest |
| 🔍 SonarQube | http://localhost:9000 | admin / (configurar na 1ª execução) |

---

## 🧪 Testes

O projeto possui uma suíte completa de testes:

```bash
# Executar todos os testes
./mvnw test

# Testes com cobertura (JaCoCo)
./mvnw verify

# Relatório de cobertura
open target/site/jacoco/index.html
```

### Stack de Testes

| Ferramenta | Uso |
|------------|-----|
| **JUnit 5** | Framework de testes |
| **MockMvc** | Testes de REST Controllers |
| **Testcontainers** | Containers para testes de integração |
| **ArchUnit** | Testes de arquitetura |
| **JaCoCo** | Cobertura de código |
| **Mockito** | Mocks e stubs |

### Cobertura Atual

- ✅ **36 testes** passando
- ✅ **22 testes** de Application Layer (Use Cases)
- ✅ **14 testes** de REST Controllers

---

## 📊 Qualidade de Código

### SonarQube

#### Configuração Inicial

```bash
# 1. Subir SonarQube
docker-compose up -d postgres-sonar sonarqube

# 2. Acessar http://localhost:9000 (admin/admin)
# 3. Gerar token em My Account > Security
# 4. Adicionar token no .env: SONAR_TOKEN=seu_token
```

#### Executar Análise

```bash
# Análise completa com testes e cobertura
./mvnw clean verify sonar:sonar \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=seu_token
```

### Métricas Monitoradas

- ✅ Cobertura de código
- ✅ Bugs e vulnerabilidades
- ✅ Code smells
- ✅ Duplicações
- ✅ Débito técnico

---

## 🗄️ Banco de Dados

### Migrações com Flyway

As migrações são executadas automaticamente ao iniciar a aplicação.

```bash
# Localização das migrações
src/main/resources/db/migration/

# Padrão de nomenclatura
V1__Create_orders_table.sql
V2__Add_order_items_table.sql
```

---

## 📁 Estrutura do Projeto

```
ms-manager-order-service/
├── 📂 src/
│   ├── 📂 main/
│   │   ├── 📂 java/com/order/
│   │   │   ├── 📂 application/     # Casos de uso
│   │   │   ├── 📂 domain/          # Entidades e regras
│   │   │   ├── 📂 infrastructure/  # Implementações
│   │   │   └── 📂 interfaces/      # Controllers e DTOs
│   │   └── 📂 resources/
│   │       ├── 📄 application.yml
│   │       └── 📂 db/migration/    # Scripts Flyway
│   └── 📂 test/                    # Testes
├── 📂 docker/                      # Configurações Docker
├── 📂 docs/                        # Documentação
├── 📄 docker-compose.yml
├── 📄 .env.example
├── 📄 CHANGELOG.md
└── 📄 README.md
```

---

## 📚 Documentação

| Documento | Descrição |
|-----------|-----------|
| [📐 Arquitetura](docs/arquitetura.md) | Decisões arquiteturais |
| [🔌 Integração](docs/integracao.md) | APIs e contratos |
| [📊 Observabilidade](docs/observabilidade.md) | Métricas e logs |
| [🗄️ Persistência](docs/persistencia.md) | Modelo de dados |
| [🧪 Testes](docs/testes.md) | Estratégia de testes |
| [⚙️ Configuração](docs/configuracao-ambiente.md) | Variáveis de ambiente |

---

## 🛠️ Comandos Úteis

### Docker

```bash
docker-compose up -d      # Iniciar serviços
docker-compose down       # Parar serviços
docker-compose logs -f    # Ver logs
```

### Maven

```bash
./mvnw spring-boot:run    # Executar aplicação
./mvnw test               # Executar testes
./mvnw verify             # Testes + cobertura
./mvnw clean package      # Gerar JAR
./mvnw sonar:sonar        # Análise SonarQube
```

---

## 🤝 Contribuindo

Contribuições são sempre bem-vindas! 

1. 🍴 Fork o projeto
2. 🌿 Crie sua branch (`git checkout -b feature/MinhaFeature`)
3. 💾 Commit suas mudanças (`git commit -m 'feat: Adiciona MinhaFeature'`)
4. 📤 Push para a branch (`git push origin feature/MinhaFeature`)
5. 🔃 Abra um Pull Request

### Convenção de Commits

```
feat:     Nova funcionalidade
fix:      Correção de bug
docs:     Documentação
style:    Formatação
refactor: Refatoração
test:     Testes
chore:    Tarefas gerais
```

---

## 📝 Changelog

Veja o arquivo [CHANGELOG.md](CHANGELOG.md) para o histórico completo de mudanças.

---

## �‍💻 Autor

<p align="center">
  <img src="https://github.com/douglas-dreer.png" width="150" style="border-radius: 50%;" alt="Douglas Dreer"/>
</p>

<p align="center">
  <b>Douglas Dreer</b>
</p>

<p align="center">
  <a href="https://linkedin.com/in/douglas-dreer">
    <img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white" alt="LinkedIn"/>
  </a>
  <a href="https://github.com/douglas-dreer">
    <img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white" alt="GitHub"/>
  </a>
  <a href="mailto:douglasdreer@gmail.com">
    <img src="https://img.shields.io/badge/Email-D14836?style=for-the-badge&logo=gmail&logoColor=white" alt="Email"/>
  </a>
</p>

---

## �📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

<p align="center">
  <sub>Feito com ❤️ para alta performance e escalabilidade</sub>
</p>

<p align="center">
  <a href="#-order-management-service">⬆️ Voltar ao topo</a>
</p>
