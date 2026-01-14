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

### Exemplo: Kubernetes Secrets

```yaml
# k8s-secrets.yaml
apiVersion: v1
kind: Secret
metadata:
  name: order-service-secrets
  namespace: production
type: Opaque
stringData:
  DB_HOST: "postgres.production.svc.cluster.local"
  DB_PORT: "5432"
  DB_NAME: "orderdb_prod"
  DB_USERNAME: "order_user_prod"
  DB_PASSWORD: "<REDACTED_USE_SEALED_SECRETS>"
  RABBITMQ_HOST: "rabbitmq.production.svc.cluster.local"
  RABBITMQ_USERNAME: "order_service"
  RABBITMQ_PASSWORD: "<REDACTED_USE_SEALED_SECRETS>"
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order-service
spec:
  template:
    spec:
      containers:
      - name: order-service
        image: order-service:1.0.0
        envFrom:
        - secretRef:
            name: order-service-secrets
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
```

### Encriptação de Senhas (Jasypt)

```xml
<!-- pom.xml -->
<dependency>
    <groupId>com.github.ulisesbocchio</groupId>
    <artifactId>jasypt-spring-boot-starter</artifactId>
    <version>3.0.5</version>
</dependency>
```

```yaml
# application.yml com senhas encriptadas
spring:
  datasource:
    password: ENC(xMhPo8RfQyH+ZDyMA+3Q8LmQz5VWJQ==)

jasypt:
  encryptor:
    password: ${JASYPT_ENCRYPTOR_PASSWORD}
    algorithm: PBEWithMD5AndDES
```

```bash
# Encriptar senha
java -cp jasypt-1.9.3.jar \
  org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI \
  input="minha_senha_secreta" \
  password="chave_mestra" \
  algorithm=PBEWithMD5AndDES

# Executar aplicação com chave
java -jar order-service.jar \
  -Djasypt.encryptor.password=chave_mestra
```

---

## 🔧 Troubleshooting

### Problema: Aplicação não conecta no PostgreSQL

**Sintomas:**
```
Caused by: org.postgresql.util.PSQLException: Connection refused
```

**Soluções:**

1. **Verificar se o container está rodando:**
   ```bash
   docker ps | grep postgres
   ```

2. **Verificar logs do PostgreSQL:**
   ```bash
   docker logs postgres-order
   ```

3. **Testar conexão manual:**
   ```bash
   psql -h localhost -p 5432 -U order_user -d orderdb
   ```

4. **Verificar variáveis de ambiente:**
   ```bash
   echo $DB_HOST
   echo $DB_PORT
   ```

5. **Verificar firewall:**
   ```bash
   # Windows
   Test-NetConnection localhost -Port 5432
   
   # Linux
   nc -zv localhost 5432
   ```

### Problema: RabbitMQ não consome mensagens

**Sintomas:**
```
org.springframework.amqp.AmqpException: No method found for class [B
```

**Soluções:**

1. **Verificar se a fila existe:**
   - Acessar: http://localhost:15672
   - Login: guest/guest
   - Verificar Queues tab

2. **Verificar bindings:**
   ```bash
   # Via RabbitMQ Admin API
   curl -u guest:guest http://localhost:15672/api/bindings
   ```

3. **Purgar fila para teste:**
   ```bash
   curl -u guest:guest -X DELETE \
     http://localhost:15672/api/queues/%2F/order.created.queue/contents
   ```

4. **Verificar formato da mensagem:**
   - Consumer espera JSON com `OrderCreatedEvent` format
   - Verificar Content-Type: application/json

### Problema: Flyway migration failed

**Sintomas:**
```
org.flywaydb.core.api.FlywayException: Validate failed: 
  Migration checksum mismatch
```

**Soluções:**

1. **Verificar histórico de migrações:**
   ```sql
   SELECT * FROM flyway_schema_history ORDER BY installed_rank;
   ```

2. **Repair (desenvolvimento):**
   ```bash
   ./mvnw flyway:repair
   ```

3. **Clean + Migrate (CUIDADO - apaga dados!):**
   ```bash
   ./mvnw flyway:clean flyway:migrate
   ```

4. **Baseline para banco existente:**
   ```bash
   ./mvnw flyway:baseline -Dflyway.baselineVersion=1
   ```

### Problema: Memory leak / OutOfMemoryError

**Sintomas:**
```
java.lang.OutOfMemoryError: Java heap space
```

**Diagnóstico:**

1. **Gerar heap dump:**
   ```bash
   jmap -dump:format=b,file=heap.bin <PID>
   ```

2. **Analisar com VisualVM ou Eclipse MAT**

3. **Verificar métricas:**
   ```bash
   curl http://localhost:8080/actuator/metrics/jvm.memory.used
   ```

**Soluções:**

1. **Aumentar heap:**
   ```bash
   java -Xms2g -Xmx4g -jar order-service.jar
   ```

2. **Ajustar GC:**
   ```bash
   java -XX:+UseG1GC \
        -XX:MaxGCPauseMillis=200 \
        -XX:ParallelGCThreads=4 \
        -jar order-service.jar
   ```

3. **Verificar connection leaks:**
   ```yaml
   spring.datasource.hikari.leak-detection-threshold: 30000
   ```

---

## 🐳 Docker Compose - Configuração Completa

```yaml
# docker-compose.yml - Completo com todos os serviços
version: '3.8'

services:
  
  # PostgreSQL - Banco principal
  postgres-order:
    image: postgres:15-alpine
    container_name: postgres-order
    env_file:
      - .env
    environment:
      POSTGRES_DB: ${DB_NAME:-orderdb}
      POSTGRES_USER: ${DB_USERNAME:-order_user}
      POSTGRES_PASSWORD: ${DB_PASSWORD:-order_pass}
      POSTGRES_INITDB_ARGS: "--encoding=UTF-8 --lc-collate=pt_BR.UTF-8 --lc-ctype=pt_BR.UTF-8"
    ports:
      - "${DB_PORT:-5432}:5432"
    volumes:
      - postgres_order_data:/var/lib/postgresql/data
      - ./docker/init-db.sql:/docker-entrypoint-initdb.d/01-init.sql:ro
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${DB_USERNAME:-order_user} -d ${DB_NAME:-orderdb}"]
      interval: 10s
      timeout: 5s
      retries: 5
      start_period: 30s
    networks:
      - order-network
    restart: unless-stopped
  
  # RabbitMQ - Message Broker
  rabbitmq:
    image: rabbitmq:3.12-management-alpine
    container_name: rabbitmq-order
    env_file:
      - .env
    environment:
      RABBITMQ_DEFAULT_USER: ${RABBITMQ_USERNAME:-guest}
      RABBITMQ_DEFAULT_PASS: ${RABBITMQ_PASSWORD:-guest}
      RABBITMQ_DEFAULT_VHOST: ${RABBITMQ_VHOST:-/}
    ports:
      - "${RABBITMQ_PORT:-5672}:5672"      # AMQP
      - "${RABBITMQ_MGMT_PORT:-15672}:15672"  # Management UI
    volumes:
      - rabbitmq_data:/var/lib/rabbitmq
      - ./docker/rabbitmq/rabbitmq.conf:/etc/rabbitmq/rabbitmq.conf:ro
      - ./docker/rabbitmq/definitions.json:/etc/rabbitmq/definitions.json:ro
    healthcheck:
      test: ["CMD", "rabbitmq-diagnostics", "ping"]
      interval: 30s
      timeout: 10s
      retries: 5
      start_period: 30s
    networks:
      - order-network
    restart: unless-stopped
  
  # PostgreSQL SonarQube
  postgres-sonar:
    image: postgres:15-alpine
    container_name: postgres-sonar
    environment:
      POSTGRES_DB: ${SONAR_DB_NAME:-sonardb}
      POSTGRES_USER: ${SONAR_DB_USERNAME:-sonar}
      POSTGRES_PASSWORD: ${SONAR_DB_PASSWORD:-sonar}
    ports:
      - "5433:5432"
    volumes:
      - sonar_postgres_data:/var/lib/postgresql/data
    networks:
      - order-network
    restart: unless-stopped
  
  # SonarQube
  sonarqube:
    image: sonarqube:community
    container_name: sonarqube
    depends_on:
      - postgres-sonar
    environment:
      SONAR_JDBC_URL: jdbc:postgresql://postgres-sonar:5432/${SONAR_DB_NAME:-sonardb}
      SONAR_JDBC_USERNAME: ${SONAR_DB_USERNAME:-sonar}
      SONAR_JDBC_PASSWORD: ${SONAR_DB_PASSWORD:-sonar}
    ports:
      - "${SONAR_WEB_PORT:-9000}:9000"
    volumes:
      - sonarqube_data:/opt/sonarqube/data
      - sonarqube_extensions:/opt/sonarqube/extensions
      - sonarqube_logs:/opt/sonarqube/logs
    networks:
      - order-network
    restart: unless-stopped

volumes:
  postgres_order_data:
    driver: local
  rabbitmq_data:
    driver: local
  sonar_postgres_data:
    driver: local
  sonarqube_data:
    driver: local
  sonarqube_extensions:
    driver: local
  sonarqube_logs:
    driver: local

networks:
  order-network:
    driver: bridge
    name: order-network
```

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
