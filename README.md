# FastFood SOAT - Backstage Service

[![CI Test & Docker Build Validation](https://github.com/SOAT-Project/fastfood-soat-backstage-service/actions/workflows/validation.yaml/badge.svg)](https://github.com/SOAT-Project/fastfood-soat-backstage-service/actions/workflows/validation.yaml)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

Serviço de gerenciamento de pedidos na cozinha (backstage) do sistema FastFood SOAT. Este microserviço é responsável por receber pedidos através de filas SQS, gerenciar o status de preparação e notificar atualizações de status.

## 📋 Índice

- [Funcionalidades](#-funcionalidades)
- [Arquitetura](#-arquitetura)
- [Tecnologias](#-tecnologias)
- [Endpoints da API](#-endpoints-da-api)
- [Mensageria](#-mensageria)
- [Recursos AWS](#-recursos-aws)
- [Como Rodar o App](#-como-rodar-o-app)
- [Testes](#-testes)
- [Deploy](#-deploy)

## 🚀 Funcionalidades

### Gestão de Pedidos na Cozinha (Work Orders)

- **Recebimento de Pedidos**: Consumo automático de pedidos via AWS SQS
- **Consulta de Pedido**: Busca de pedido por ID
- **Listagem de Pedidos**: Listagem filtrada por status
- **Atualização de Status**: Gerenciamento do ciclo de vida do pedido
- **Notificações**: Envio automático de notificações de mudança de status via SQS

### Status do Pedido (WorkOrder)

1. **RECEIVED** - Status inicial ao chegar no Backstage (não notificado)
2. **PREPARING** - Quando o preparo do pedido inicia (notificado)
3. **READY** - Quando o pedido está pronto para entrega (notificado)
4. **COMPLETED** - Quando o pedido foi entregue ao cliente (notificado)

## 🏗️ Arquitetura

O projeto segue os princípios de **Clean Architecture** e **Hexagonal Architecture**:

```
├── application/
│   ├── domain/         # Entidades e regras de negócio
│   ├── port/           # Interfaces de entrada e saída
│   └── usecase/        # Casos de uso da aplicação
└── adapter/
    ├── inbound/        # Adaptadores de entrada (API REST, SQS Consumer)
    └── outbound/       # Adaptadores de saída (DynamoDB, SQS Producer)
```

## 🛠️ Tecnologias

- **Java 25** - Linguagem de programação
- **Spring Boot 4.0.0** - Framework principal
- **Spring Cloud AWS** - Integração com serviços AWS
- **AWS DynamoDB** - Banco de dados NoSQL para persistência
- **AWS SQS** - Mensageria para comunicação assíncrona
- **Spring Actuator** - Monitoramento e health checks
- **Lombok** - Redução de código boilerplate
- **Maven** - Gerenciamento de dependências
- **JUnit 5 & Mockito** - Testes unitários (100% de cobertura)
- **Docker** - Containerização (multi-arch: amd64/arm64)
- **Kubernetes** - Orquestração de containers
- **Terraform** - Infrastructure as Code
- **GitHub Actions** - CI/CD

## 📡 Endpoints da API

Base Path: `/backstage/work-orders`

### Consultar Pedido por ID

```http
GET /backstage/work-orders/{id}
```

**Resposta de Sucesso (200 OK):**
```json
{
  "id": "a866f7ba-3c24-4d1e-b138-0f80d1300cc2",
  "orderNumber": "1",
  "status": "PREPARING",
  "items": [
    {
      "name": "xpto",
      "quantity": 1
    }
  ],
  "createdAt": "2026-01-20T10:30:00Z",
  "updatedAt": "2026-01-20T10:35:00Z"
}
```

### Listar Pedidos por Status

```http
GET /backstage/work-orders?status=PREPARING
```

**Parâmetros de Query:**
- `status` (obrigatório): RECEIVED, PREPARING, READY ou DELIVERED

**Resposta de Sucesso (200 OK):**
```json
[
  {
    "id": "a866f7ba-3c24-4d1e-b138-0f80d1300cc2",
    "orderNumber": "1",
    "status": "PREPARING",
    "items": [...],
    "createdAt": "2026-01-20T10:30:00Z",
    "updatedAt": "2026-01-20T10:35:00Z"
  }
]
```

### Atualizar Status do Pedido

```http
PUT /backstage/work-orders/{id}/status
Content-Type: application/json

{
  "status": "READY"
}
```

**Resposta de Sucesso:** `204 No Content`

### Health Check

```http
GET /backstage/api/actuator/health
GET /backstage/api/actuator/health/liveness
GET /backstage/api/actuator/health/readiness
```

## 📨 Mensageria

### Consumo de Pedidos (SQS)

**Fila:** `fastfood-soat-terraform-order-to-kitchen.fifo`

**Formato da Mensagem Recebida:**
```json
{
  "data": {
    "id": "a866f7ba-3c24-4d1e-b138-0f80d1300cc2",
    "orderNumber": "1",
    "items": [
      {
        "name": "xpto",
        "quantity": 1
      },
      {
        "name": "xpto2",
        "quantity": 2
      }
    ]
  }
}
```

### Notificações de Status (SQS)

**Fila:** `fastfood-soat-terraform-kitchen-to-order`

**Formato da Mensagem Enviada:**
```json
{
  "data": {
    "id": "a866f7ba-3c24-4d1e-b138-0f80d1300cc2",
    "status": "PREPARING"
  }
}
```

## ☁️ Recursos AWS

### DynamoDB

- **Tabela:** Armazena os Work Orders
- **Partition Key:** `id` (String)
- **Atributos:** id, orderNumber, status, items, createdAt, updatedAt

### SQS (Simple Queue Service)

- **Fila de Entrada:** `fastfood-soat-terraform-order-to-kitchen.fifo` (FIFO)
- **Fila de Saída:** `fastfood-soat-terraform-kitchen-to-order` (Standard)

### Configuração AWS

Configure as seguintes variáveis de ambiente:

```bash
AWS_REGION=sa-east-1
AWS_ACCESS_KEY_ID=your-access-key
AWS_SECRET_ACCESS_KEY=your-secret-key
MESSAGE_ORDER=fastfood-soat-terraform-order-to-kitchen.fifo
MESSAGE_ORDER_STATUS=fastfood-soat-terraform-kitchen-to-order
```

## 🚀 Como Rodar o App

### Pré-requisitos

- Java 25 (JDK)
- Maven 3.9+
- Docker (opcional)
- Conta AWS com DynamoDB e SQS configurados

### Executar Localmente

1. **Clone o repositório:**
```bash
git clone https://github.com/SOAT-Project/fastfood-soat-backstage-service.git
cd fastfood-soat-backstage-service/app
```

2. **Configure as variáveis de ambiente:**
```bash
export AWS_REGION=sa-east-1
export AWS_ACCESS_KEY_ID=your-access-key
export AWS_SECRET_ACCESS_KEY=your-secret-key
export MESSAGE_ORDER=fastfood-soat-terraform-order-to-kitchen.fifo
export MESSAGE_ORDER_STATUS=fastfood-soat-terraform-kitchen-to-order
```

3. **Execute com Maven:**
```bash
./mvnw spring-boot:run
```

4. **Acesse a aplicação:**
```
http://localhost:8080/backstage/work-orders
http://localhost:8080/backstage/api/actuator/health
```

### Executar com Docker

1. **Build da imagem:**
```bash
cd app
docker build -t fastfood-backstage:latest .
```

2. **Execute o container:**
```bash
docker run -d \
  -p 8080:8080 \
  -e AWS_REGION=sa-east-1 \
  -e AWS_ACCESS_KEY_ID=your-access-key \
  -e AWS_SECRET_ACCESS_KEY=your-secret-key \
  -e MESSAGE_ORDER=fastfood-soat-terraform-order-to-kitchen.fifo \
  -e MESSAGE_ORDER_STATUS=fastfood-soat-terraform-kitchen-to-order \
  --name backstage \
  fastfood-backstage:latest
```

### Executar com Docker Compose (Recomendado para desenvolvimento)

Crie um arquivo `docker-compose.yml`:

```yaml
version: '3.8'

services:
  backstage:
    build: ./app
    ports:
      - "8080:8080"
    environment:
      - AWS_REGION=sa-east-1
      - AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY_ID}
      - AWS_SECRET_ACCESS_KEY=${AWS_SECRET_ACCESS_KEY}
      - MESSAGE_ORDER=fastfood-soat-terraform-order-to-kitchen.fifo
      - MESSAGE_ORDER_STATUS=fastfood-soat-terraform-kitchen-to-order
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/backstage/api/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
```

Execute:
```bash
docker-compose up -d
```

## 🧪 Testes

O projeto possui **100% de cobertura de testes** na camada de aplicação.

### Executar Testes

```bash
cd app
./mvnw test
```

### Relatório de Cobertura (JaCoCo)

```bash
./mvnw clean test jacoco:report
```

O relatório HTML será gerado em: `target/site/jacoco/index.html`

### Análise de Qualidade (SonarQube)

O projeto está integrado com SonarQube Cloud para análise de qualidade de código:

```bash
./mvnw clean verify sonar:sonar \
  -Dsonar.projectKey=SOAT-Project_fastfood-soat-backstage-service \
  -Dsonar.organization=soat-project \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.token=$SONAR_TOKEN
```

### Stack de Testes

- **JUnit 5** - Framework de testes
- **Mockito** - Mock de dependências
- **AssertJ** - Assertions fluentes
- **JaCoCo** - Cobertura de código

### Casos de Teste Implementados

- ✅ Testes unitários de todos os Use Cases (Create, Get, List, Update, Delete)
- ✅ Testes de exceções customizadas (DomainException, NotFoundException, etc.)
- ✅ Testes de validação de handlers
- ✅ Testes de utilitários

## 📦 Deploy

### Kubernetes

O projeto inclui manifestos Kubernetes completos:

```bash
kubectl apply -f infra/kubernetes/namespace.yaml
kubectl apply -f infra/kubernetes/service-account.yaml
kubectl apply -f infra/kubernetes/configmap.yaml
kubectl apply -f infra/kubernetes/deployment.yaml
kubectl apply -f infra/kubernetes/service.yaml
kubectl apply -f infra/kubernetes/hpa.yaml
kubectl apply -f infra/kubernetes/http-route.yaml
```

**Recursos do Kubernetes:**
- Namespace dedicado: `backstage`
- Service Account com IAM roles para AWS
- ConfigMap e Secrets para configuração
- Deployment com 3 réplicas mínimas
- HPA (Horizontal Pod Autoscaler) configurado
- Service ClusterIP
- HTTPRoute (Envoy Gateway API) para roteamento

### Terraform

Infrastructure as Code para provisionamento na AWS:

```bash
cd infra/terraform
terraform init
terraform plan
terraform apply
```

**Recursos Provisionados:**
- EKS Cluster
- DynamoDB Tables
- SQS Queues (FIFO e Standard)
- IAM Roles e Policies
- VPC e Networking
- Load Balancers

### CI/CD

O projeto possui pipelines completos de CI/CD com GitHub Actions:

- **validation.yaml**: Testes, build Docker e análise de qualidade
- **docker.yaml**: Build multi-arch e push para Docker Hub
- **deploy.yaml**: Deploy automatizado no Kubernetes
- **terraform.yaml**: Provisionamento de infraestrutura

## 📄 Variáveis de Ambiente

| Variável | Descrição | Padrão |
|----------|-----------|--------|
| `SERVER_SERVLET_CONTEXT_PATH` | Context path da aplicação | `/backstage` |
| `SPRING_APPLICATION_NAME` | Nome da aplicação | `FastFood SOAT - Backstage` |
| `AWS_REGION` | Região AWS | `us-east-1` |
| `AWS_ACCESS_KEY_ID` | Access Key AWS | - |
| `AWS_SECRET_ACCESS_KEY` | Secret Key AWS | - |
| `MESSAGE_ORDER` | Fila SQS de entrada | `fastfood-soat-terraform-order-to-kitchen.fifo` |
| `MESSAGE_ORDER_STATUS` | Fila SQS de saída | `fastfood-soat-terraform-kitchen-to-order` |

## 📝 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

## 👥 Contribuidores

- SOAT Project Team

## 🔗 Links Úteis

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Cloud AWS](https://docs.awspring.io/spring-cloud-aws/docs/current/reference/html/)
- [AWS DynamoDB](https://docs.aws.amazon.com/dynamodb/)
- [AWS SQS](https://docs.aws.amazon.com/sqs/)
- [Kubernetes Documentation](https://kubernetes.io/docs/)

---
