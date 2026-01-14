# SonarQube - Configuração e Uso

## 📋 Visão Geral

O SonarQube Community está configurado para análise contínua de qualidade de código, com banco de dados PostgreSQL dedicado e persistência de dados.

---

## 🚀 Inicialização

### 1. Subir os containers

```bash
docker-compose up -d postgres-sonar sonarqube
```

**Aguarde ~2 minutos** para o SonarQube inicializar completamente.

### 2. Acessar o SonarQube

- **URL**: http://localhost:9000
- **Credenciais padrão**:
  - Usuário: `admin`
  - Senha: `admin`

⚠️ **Importante**: Na primeira vez, você será forçado a alterar a senha.

---

## 🔑 Gerar Token de Acesso

### Passo a Passo:

1. Acesse http://localhost:9000 e faça login
2. Clique no avatar (canto superior direito) → **My Account**
3. Vá para a aba **Security**
4. Em **Generate Tokens**:
   - **Name**: `order-service-token`
   - **Type**: `Project Analysis Token` ou `Global Analysis Token`
   - **Expires in**: `No expiration` ou `90 days`
5. Clique em **Generate**
6. **Copie o token gerado** (aparece apenas uma vez!)

### Exemplo de token gerado:
```
sqp_1a2b3c4d5e6f7g8h9i0j1k2l3m4n5o6p7q8r
```

### Atualizar o .env:

Edite o arquivo `.env` e substitua o token:

```bash
SONAR_TOKEN=sqp_1a2b3c4d5e6f7g8h9i0j1k2l3m4n5o6p7q8r
```

---

## 📊 Executar Análise

### Com Maven:

```bash
# Análise completa com testes e cobertura
.\mvnw clean verify sonar:sonar `
  -Dsonar.host.url=http://localhost:9000 `
  -Dsonar.token=%SONAR_TOKEN%

# Ou usando a variável do .env diretamente
.\mvnw clean verify sonar:sonar `
  -Dsonar.host.url=${SONAR_HOST_URL} `
  -Dsonar.token=${SONAR_TOKEN}
```

### Linux/Mac:

```bash
./mvnw clean verify sonar:sonar \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=$SONAR_TOKEN
```

---

## 📦 Estrutura do Docker Compose

```yaml
postgres-sonar:
  - Banco PostgreSQL dedicado para SonarQube
  - Porta: 5433 (para não conflitar com banco da aplicação)
  - Volume persistente: sonar_postgres_data

sonarqube:
  - Imagem: sonarqube:community (latest)
  - Porta: 9000
  - Volumes persistentes:
    - sonarqube_data (análises e configurações)
    - sonarqube_extensions (plugins)
    - sonarqube_logs (logs)
```

---

## 🔧 Configuração do Projeto no SonarQube

### 1. Criar Projeto Manualmente:

1. Em http://localhost:9000, clique em **Create Project**
2. **Project key**: `io.github.douglasdreer:order-service`
3. **Display name**: `Order Management Service`
4. Clique em **Set Up**
5. Escolha **Locally**
6. Siga as instruções para gerar o token (se ainda não tiver)

### 2. Configuração no pom.xml:

O projeto já está configurado com as propriedades do SonarQube:

```xml
<properties>
    <sonar.projectKey>io.github.douglasdreer:order-service</sonar.projectKey>
    <sonar.projectName>Order Management Service</sonar.projectName>
    <sonar.host.url>http://localhost:9000</sonar.host.url>
    <sonar.coverage.jacoco.xmlReportPaths>
        ${project.build.directory}/site/jacoco/jacoco.xml
    </sonar.coverage.jacoco.xmlReportPaths>
</properties>
```

---

## 📈 Métricas Analisadas

| Métrica | Descrição |
|---------|-----------|
| **Bugs** | Problemas que podem causar comportamento incorreto |
| **Vulnerabilities** | Pontos de segurança que precisam atenção |
| **Code Smells** | Problemas de manutenibilidade |
| **Coverage** | Cobertura de testes (JaCoCo) |
| **Duplications** | Código duplicado |
| **Technical Debt** | Tempo estimado para corrigir issues |

---

## 🗄️ Persistência de Dados

### Volumes Docker:

```bash
# Listar volumes
docker volume ls | grep sonar

# Inspecionar volume
docker volume inspect ms-manager-order-service_sonarqube_data

# Backup do banco SonarQube
docker exec sonar-postgres pg_dump -U sonar sonardb > sonar_backup.sql

# Restore do banco SonarQube
docker exec -i sonar-postgres psql -U sonar sonardb < sonar_backup.sql
```

### Dados Persistidos:

- ✅ **Análises históricas** - Todas as análises são mantidas
- ✅ **Configurações** - Quality Profiles, Quality Gates
- ✅ **Usuários e tokens** - Credenciais e tokens de acesso
- ✅ **Plugins** - Extensões instaladas
- ✅ **Issues** - Bugs, vulnerabilidades identificadas

---

## 🧹 Manutenção

### Limpar dados (resetar):

```bash
# Parar containers
docker-compose down

# Remover volumes (CUIDADO: apaga todo histórico)
docker volume rm ms-manager-order-service_sonarqube_data
docker volume rm ms-manager-order-service_sonar_postgres_data
docker volume rm ms-manager-order-service_sonarqube_extensions
docker volume rm ms-manager-order-service_sonarqube_logs

# Recriar tudo
docker-compose up -d postgres-sonar sonarqube
```

### Ver logs:

```bash
# Logs do SonarQube
docker logs -f order-sonarqube

# Logs do PostgreSQL do SonarQube
docker logs -f sonar-postgres
```

---

## 🔍 Quality Gates

### Configuração Recomendada:

1. Acesse **Quality Gates** no menu
2. Edite o **Sonar way** ou crie um customizado
3. Defina condições:
   - Coverage >= 80%
   - Duplications <= 3%
   - Bugs = 0
   - Vulnerabilities = 0
   - Code Smells rating >= A

---

## 🐛 Troubleshooting

### SonarQube não inicia:

```bash
# Verificar logs
docker logs order-sonarqube

# Problemas comuns:
# 1. Memória insuficiente - Aumente RAM do Docker (min 4GB)
# 2. Porta 9000 em uso - Mude SONAR_WEB_PORT no .env
# 3. PostgreSQL não healthy - docker ps para ver status
```

### Erro de conexão com PostgreSQL:

```bash
# Verificar se PostgreSQL está rodando
docker exec sonar-postgres pg_isready -U sonar

# Verificar rede
docker network inspect ms-manager-order-service_order-network
```

### Token inválido:

1. Gere um novo token no SonarQube
2. Atualize o `.env`
3. Execute a análise novamente

---

## 📚 Recursos Adicionais

- [Documentação Oficial SonarQube](https://docs.sonarqube.org/latest/)
- [SonarQube Docker Hub](https://hub.docker.com/_/sonarqube)
- [Maven SonarQube Scanner](https://docs.sonarqube.org/latest/analysis/scan/sonarscanner-for-maven/)

---

## ✅ Checklist para Avaliador

- [ ] `docker-compose up -d` executado
- [ ] SonarQube acessível em http://localhost:9000
- [ ] Login com admin/admin realizado
- [ ] Senha alterada na primeira execução
- [ ] Token gerado e salvo no `.env`
- [ ] Análise executada: `.\mvnw clean verify sonar:sonar`
- [ ] Projeto visível no dashboard do SonarQube
- [ ] Métricas exibindo cobertura e qualidade
- [ ] Histórico de análises preservado após restart
