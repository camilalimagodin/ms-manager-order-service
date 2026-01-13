# Configuração de Ambiente

Este documento descreve como configurar as variáveis de ambiente para o **Order Management Service**.

## 📋 Visão Geral

O projeto utiliza a biblioteca [spring-dotenv](https://github.com/paulschwarz/spring-dotenv) para carregar variáveis de ambiente de arquivos `.env`, seguindo as melhores práticas de configuração de aplicações.

## 🚀 Quick Start

1. **Copie o arquivo de exemplo:**
   ```bash
   cp .env.example .env
   ```

2. **Edite o arquivo `.env`** com suas configurações:
   ```bash
   # Edite com seu editor preferido
   code .env
   ```

3. **Inicie os serviços com Docker Compose:**
   ```bash
   docker-compose up -d
   ```

4. **Execute a aplicação:**
   ```bash
   mvn spring-boot:run
   ```

## 📁 Arquivos de Ambiente

| Arquivo | Descrição | Versionado |
|---------|-----------|------------|
| `.env.example` | Template com todas as variáveis | ✅ Sim |
| `.env` | Configurações locais de desenvolvimento | ❌ Não |
| `.env.test` | Configurações para testes | ✅ Sim |

> ⚠️ **IMPORTANTE:** Nunca commite o arquivo `.env` no repositório! Ele está incluído no `.gitignore`.

## 🔧 Variáveis de Ambiente

### Database (PostgreSQL)

| Variável | Descrição | Valor Padrão |
|----------|-----------|--------------|
| `DB_HOST` | Host do banco de dados | `localhost` |
| `DB_PORT` | Porta do banco de dados | `5432` |
| `DB_NAME` | Nome do banco de dados | `orderdb` |
| `DB_USERNAME` | Usuário do banco | `order_user` |
| `DB_PASSWORD` | Senha do banco | - |

### RabbitMQ

| Variável | Descrição | Valor Padrão |
|----------|-----------|--------------|
| `RABBITMQ_HOST` | Host do RabbitMQ | `localhost` |
| `RABBITMQ_PORT` | Porta AMQP | `5672` |
| `RABBITMQ_USERNAME` | Usuário do RabbitMQ | `guest` |
| `RABBITMQ_PASSWORD` | Senha do RabbitMQ | `guest` |

### Server

| Variável | Descrição | Valor Padrão |
|----------|-----------|--------------|
| `SERVER_PORT` | Porta da aplicação | `8080` |
| `SPRING_PROFILES_ACTIVE` | Profile ativo | `local` |

### Logging

| Variável | Descrição | Valor Padrão |
|----------|-----------|--------------|
| `LOG_LEVEL_ROOT` | Nível de log raiz | `INFO` |
| `LOG_LEVEL_APP` | Nível de log da aplicação | `DEBUG` |
| `LOG_LEVEL_SQL` | Nível de log SQL | `DEBUG` |
| `LOG_LEVEL_SQL_PARAMS` | Nível de log parâmetros SQL | `TRACE` |

## 🐳 Docker Compose

O `docker-compose.yml` está configurado para ler as variáveis do arquivo `.env` automaticamente:

```yaml
services:
  postgres:
    env_file:
      - .env
    environment:
      POSTGRES_DB: ${DB_NAME:-orderdb}
      POSTGRES_USER: ${DB_USERNAME:-order_user}
      POSTGRES_PASSWORD: ${DB_PASSWORD:-order_pass}
```

## 🔒 Segurança

### Boas Práticas

1. **Nunca commite senhas** no repositório
2. **Use senhas fortes** em ambientes de produção
3. **Rotacione credenciais** periodicamente
4. **Use secrets managers** em produção (AWS Secrets Manager, Azure Key Vault, HashiCorp Vault)

### Produção

Para ambientes de produção, recomenda-se:

- Usar um serviço de gerenciamento de secrets
- Injetar variáveis via CI/CD
- Usar diferentes credenciais por ambiente
- Habilitar SSL/TLS para conexões de banco

## 📝 Profiles Spring

| Profile | Arquivo | Uso |
|---------|---------|-----|
| `local` | `application-local.yml` | Desenvolvimento local |
| `test` | `application-test.yml` | Testes automatizados |
| `dev` | - | Ambiente de desenvolvimento |
| `staging` | - | Ambiente de homologação |
| `prod` | - | Ambiente de produção |

## 🧪 Testes

Os testes utilizam Testcontainers, que sobrescreve as configurações de banco e RabbitMQ automaticamente. O arquivo `.env.test` contém configurações base para testes.

```bash
# Executar testes
mvn test

# Executar com profile específico
SPRING_PROFILES_ACTIVE=test mvn test
```
