# 🏦 Sistema Bancário — Spring Boot + MySQL

[![Java](https://img.shields.io/badge/Java-17-orange)](https://adoptium.net)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.x-blue)](https://www.mysql.com)
[![Maven](https://img.shields.io/badge/Maven-3.x-red)](https://maven.apache.org)

Sistema bancário completo com conta corrente, depósito, saque e transferência.

---

## 🚀 Como rodar (IntelliJ IDEA)

### Pré-requisitos
- Java 17+ → [adoptium.net](https://adoptium.net)
- Maven 3.x (embutido no IntelliJ)
- MySQL 8.x

### Passo a passo
```bash
# 1. Criar banco no MySQL / HeidiSQL
CREATE DATABASE banking_db;

# 2. Editar src/main/resources/application.properties
spring.datasource.username=root
spring.datasource.password=SUA_SENHA

# 3. No IntelliJ: botão ▶️ em BankingSystemApplication
# As tabelas são criadas automaticamente pelo Hibernate
```

A API sobe em **http://localhost:8080**

---

## 📋 Endpoints

### Contas
| Método | URL | Descrição |
|--------|-----|-----------|
| POST | `/api/contas` | Criar conta |
| GET | `/api/contas` | Listar todas |
| GET | `/api/contas/{numero}` | Buscar por número |
| GET | `/api/contas/cpf/{cpf}` | Buscar por CPF |
| PATCH | `/api/contas/{numero}/status` | Atualizar status |

### Transações
| Método | URL | Descrição |
|--------|-----|-----------|
| POST | `/api/contas/{numero}/depositar` | Depósito |
| POST | `/api/contas/{numero}/sacar` | Saque |
| POST | `/api/contas/{numero}/transferir` | Transferência |
| GET | `/api/contas/{numero}/extrato` | Extrato completo |

---

## 📦 Exemplos de uso

### Criar conta
```bash
curl -X POST http://localhost:8080/api/contas \
  -H "Content-Type: application/json" \
  -d '{"titular":"João Silva","cpf":"529.982.247-25","agencia":"0001","depositoInicial":1000.00}'
```

### Depositar
```bash
curl -X POST http://localhost:8080/api/contas/{numeroConta}/depositar \
  -H "Content-Type: application/json" \
  -d '{"valor":500.00,"descricao":"Salário"}'
```

### Sacar
```bash
curl -X POST http://localhost:8080/api/contas/{numeroConta}/sacar \
  -H "Content-Type: application/json" \
  -d '{"valor":200.00}'
```

### Transferir
```bash
curl -X POST http://localhost:8080/api/contas/{numeroConta}/transferir \
  -H "Content-Type: application/json" \
  -d '{"numeroContaDestino":"NUMERO_DESTINO","valor":300.00,"descricao":"Pagamento"}'
```

### Ver extrato
```bash
curl http://localhost:8080/api/contas/{numeroConta}/extrato
```

---

## 🧪 Rodar testes
```bash
mvn test
# 16 testes de integração — todos devem passar
```

---

## 🗄️ Estrutura do banco

### `contas_correntes`
| Campo | Tipo | Descrição |
|-------|------|-----------|
| numero_conta | VARCHAR(10) | Número único gerado |
| titular | VARCHAR(100) | Nome completo |
| cpf | VARCHAR(14) | CPF formatado (único) |
| saldo | DECIMAL(15,2) | Saldo atual |
| limite_cheque_especial | DECIMAL(15,2) | Limite extra |
| status | ENUM | ATIVA/INATIVA/BLOQUEADA/ENCERRADA |

### `transacoes`
| Campo | Tipo | Descrição |
|-------|------|-----------|
| tipo | ENUM | DEPOSITO/SAQUE/TRANSFERENCIA_ENVIADA/TRANSFERENCIA_RECEBIDA |
| valor | DECIMAL(15,2) | Valor da operação |
| saldo_antes / saldo_depois | DECIMAL | Rastreabilidade |
| codigo_autorizacao | VARCHAR(20) | Código único AUTH+timestamp |

---

## 📁 Estrutura do projeto
```
src/
├── main/java/com/bank/
│   ├── BankingSystemApplication.java
│   ├── config/WebConfig.java
│   ├── controller/ContaCorrenteController.java
│   ├── controller/TransacaoController.java
│   ├── service/ContaCorrenteService.java
│   ├── service/TransacaoService.java
│   ├── repository/ContaCorrenteRepository.java
│   ├── repository/TransacaoRepository.java
│   ├── model/ContaCorrente.java
│   ├── model/Transacao.java
│   ├── dto/ContaDTO.java
│   ├── dto/TransacaoDTO.java
│   └── exception/BankingException.java + GlobalExceptionHandler.java
└── test/java/com/bank/
    ├── BankingIntegrationTest.java  (16 testes de integração)
    └── TransacaoServiceTest.java    (6 testes unitários)
```
