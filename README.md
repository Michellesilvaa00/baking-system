<div align="center">

<img src="https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/>
<img src="https://img.shields.io/badge/Spring_Boot-3.2.0-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white"/>
<img src="https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white"/>
<img src="https://img.shields.io/badge/Maven-3.8-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white"/>
<img src="https://img.shields.io/badge/Hibernate-JPA-59666C?style=for-the-badge&logo=hibernate&logoColor=white"/>
<img src="https://img.shields.io/badge/JUnit5-22%20testes-25A162?style=for-the-badge&logo=junit5&logoColor=white"/>

<br/>
<br/>

# 🏦 Banking System API

**API REST de sistema bancário desenvolvida com Java 17 + Spring Boot 3 + MySQL**

Projeto back-end com arquitetura em camadas, cobertura de testes, tratamento centralizado de erros e boas práticas do mercado enterprise Java.

<br/>

[📋 Endpoints](#-endpoints) &nbsp;•&nbsp; [🚀 Como rodar](#-como-rodar) &nbsp;•&nbsp; [🧪 Testes](#-testes) &nbsp;•&nbsp; [🏗️ Arquitetura](#%EF%B8%8F-arquitetura) &nbsp;•&nbsp; [🗄️ Banco de dados](#%EF%B8%8F-modelo-de-dados)

</div>

---

## 📌 Sobre o projeto

Sistema bancário RESTful que simula operações reais de uma instituição financeira. Desenvolvido com foco em **clean code**, **boas práticas**, **segurança transacional** e **tratamento robusto de exceções**, seguindo os padrões mais adotados no mercado Java enterprise.

### ✨ Funcionalidades

- 🏦 **Conta Corrente** — abertura com número gerado automaticamente, agência e CPF validados
- 💰 **Depósito** — com rastreabilidade completa de saldo antes/depois
- 💸 **Saque** — com suporte a limite de cheque especial configurável por conta
- 🔄 **Transferência** — atômica entre contas, com registro nas duas pontas
- 📊 **Extrato** — completo ou filtrado por período com `LocalDateTime`
- 🔒 **Controle de status** — `ATIVA` / `INATIVA` / `BLOQUEADA` / `ENCERRADA`
- ❌ **Tratamento de erros** — respostas JSON padronizadas com códigos semânticos

---

## 🛠️ Tecnologias

| Tecnologia | Versão | Finalidade |
|-----------|--------|-----------|
| **Java** | 17 LTS | Linguagem principal — versão LTS mais adotada no mercado |
| **Spring Boot** | 3.2.0 | Framework principal — produtividade e suporte a Jakarta EE |
| **Spring Data JPA** | 3.2.0 | Abstração do banco com queries customizadas via JPQL |
| **Hibernate** | 6.x | ORM padrão de mercado, DDL automático via `ddl-auto=update` |
| **MySQL** | 8.x | Banco relacional robusto amplamente usado em produção |
| **Lombok** | latest | Redução de boilerplate — `@Builder`, `@Data`, `@Slf4j` |
| **Bean Validation** | 3.x | Validação declarativa de DTOs com `@Valid` e `@CPF` |
| **H2 Database** | latest | Banco em memória para testes de integração isolados |
| **JUnit 5** | latest | Framework de testes unitários e de integração |
| **Mockito** | latest | Mock de dependências nos testes unitários |

---

## 🏗️ Arquitetura

O projeto segue a arquitetura em **3 camadas** com separação clara de responsabilidades:

```
┌─────────────────────────────────────────────────────────┐
│                    Cliente HTTP                          │
│              (Postman / curl / Frontend)                 │
└───────────────────────┬─────────────────────────────────┘
                        │ JSON
┌───────────────────────▼─────────────────────────────────┐
│                  Controller Layer                        │
│   ContaCorrenteController   TransacaoController          │
│   • Recebe e valida requisições HTTP com @Valid          │
│   • Retorna ResponseEntity com status semântico          │
└───────────────────────┬─────────────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────────────┐
│                   Service Layer                          │
│   ContaCorrenteService      TransacaoService             │
│   • Regras de negócio e validações de domínio            │
│   • Controle transacional com @Transactional             │
│   • Lança exceções customizadas da hierarquia            │
└───────────────────────┬─────────────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────────────┐
│                 Repository Layer                         │
│   ContaCorrenteRepository   TransacaoRepository          │
│   • Spring Data JPA + Hibernate                          │
│   • Queries customizadas com @Query / JPQL               │
└───────────────────────┬─────────────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────────────┐
│                     MySQL 8.x                            │
│          contas_correntes  ·  transacoes                 │
└─────────────────────────────────────────────────────────┘
```

### 📁 Estrutura de pacotes

```
src/
├── main/java/com/bank/
│   ├── BankingSystemApplication.java       # Entry point
│   ├── config/
│   │   └── WebConfig.java                  # Configuração de CORS
│   ├── controller/
│   │   ├── ContaCorrenteController.java    # Endpoints de conta
│   │   └── TransacaoController.java        # Endpoints de transações
│   ├── service/
│   │   ├── ContaCorrenteService.java       # Regras de negócio da conta
│   │   └── TransacaoService.java           # Depósito, saque, transferência
│   ├── repository/
│   │   ├── ContaCorrenteRepository.java    # JPA Repository + queries JPQL
│   │   └── TransacaoRepository.java        # JPA Repository + filtro por período
│   ├── model/
│   │   ├── ContaCorrente.java              # Entidade JPA com @PrePersist
│   │   └── Transacao.java                  # Entidade JPA com código de autorização
│   ├── dto/
│   │   ├── ContaDTO.java                   # Request / Response da conta
│   │   └── TransacaoDTO.java               # Request / Response de transações e extrato
│   └── exception/
│       ├── BankingException.java            # Hierarquia de exceções de domínio
│       └── GlobalExceptionHandler.java      # @RestControllerAdvice centralizado
└── test/java/com/bank/
    ├── BankingIntegrationTest.java          # 16 testes de integração com H2
    └── TransacaoServiceTest.java            # 6 testes unitários com Mockito
```

---

## 📋 Endpoints

### Contas

| Método | Endpoint | Descrição | HTTP Status |
|--------|----------|-----------|-------------|
| `POST` | `/api/contas` | Criar nova conta corrente | `201 Created` |
| `GET` | `/api/contas` | Listar todas as contas | `200 OK` |
| `GET` | `/api/contas?status=ATIVA` | Filtrar contas por status | `200 OK` |
| `GET` | `/api/contas/{numeroConta}` | Buscar conta por número | `200 OK` |
| `GET` | `/api/contas/cpf/{cpf}` | Buscar conta por CPF | `200 OK` |
| `PATCH` | `/api/contas/{numeroConta}/status` | Atualizar status da conta | `200 OK` |

### Transações

| Método | Endpoint | Descrição | HTTP Status |
|--------|----------|-----------|-------------|
| `POST` | `/api/contas/{numero}/depositar` | Realizar depósito | `200 OK` |
| `POST` | `/api/contas/{numero}/sacar` | Realizar saque | `200 OK` |
| `POST` | `/api/contas/{numero}/transferir` | Transferência entre contas | `200 OK` |
| `GET` | `/api/contas/{numero}/extrato` | Consultar extrato completo | `200 OK` |
| `GET` | `/api/contas/{numero}/extrato?inicio=...&fim=...` | Extrato por período | `200 OK` |

### Códigos de erro tratados

| HTTP | Código interno | Situação |
|------|----------------|----------|
| `400` | `VALOR_INVALIDO` | Valor zero ou negativo |
| `400` | `TRANSFERENCIA_MESMA_CONTA` | Conta origem igual à destino |
| `404` | `CONTA_NAO_ENCONTRADA` | Conta não existe no banco |
| `409` | `CPF_JA_CADASTRADO` | CPF já vinculado a outra conta |
| `422` | `SALDO_INSUFICIENTE` | Saldo + cheque especial insuficiente |
| `422` | `CONTA_INATIVA` | Conta bloqueada ou encerrada |

---

## 🚀 Como rodar

### Pré-requisitos

- [Java 17+](https://adoptium.net) — Eclipse Temurin recomendado
- [Maven 3.8+](https://maven.apache.org/download.cgi) — ou usar o wrapper da IDE
- [MySQL 8.x](https://dev.mysql.com/downloads/) — ou via Docker

### 1. Clonar o repositório

```bash
git clone https://github.com/Michellesilvaa00/banking-system.git
cd banking-system
```

### 2. Criar o banco de dados

```sql
CREATE DATABASE banking_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

> Usando Docker:
> ```bash
> docker run -d --name mysql-bank -e MYSQL_ROOT_PASSWORD=root -p 3306:3306 mysql:8
> ```

### 3. Configurar credenciais

Edite `src/main/resources/application.properties`:

```properties
spring.datasource.username=root
spring.datasource.password=sua_senha
```

> As tabelas `contas_correntes` e `transacoes` são criadas **automaticamente** pelo Hibernate no primeiro start.

### 4. Executar

```bash
mvn spring-boot:run
```

✅ API disponível em `http://localhost:8080`

---

## 🧪 Testes

```bash
mvn test
```

### Cobertura

```
BankingIntegrationTest — 16 testes de integração (H2 in-memory)
  ✅ Criar conta com depósito inicial
  ✅ Rejeitar CPF duplicado
  ✅ Depósito aumenta saldo corretamente
  ✅ Saque diminui saldo corretamente
  ✅ Saque usando limite de cheque especial
  ✅ Rejeitar saque com saldo insuficiente
  ✅ Transferência atualiza saldo nas duas contas
  ✅ Rejeitar transferência para a mesma conta
  ✅ Extrato lista todas as transações em ordem
  ✅ Busca por CPF retorna a conta correta
  ✅ Atualizar status para BLOQUEADA
  ✅ Rejeitar operação em conta bloqueada
  ✅ Conta não encontrada retorna 404
  ✅ Listar todas as contas cadastradas

TransacaoServiceTest — 6 testes unitários (Mockito)
  ✅ Depósito aumenta saldo
  ✅ Saque diminui saldo
  ✅ Saque insuficiente lança SaldoInsuficienteException
  ✅ Transferência move saldo entre as duas contas
  ✅ Transferência para mesma conta lança exceção
  ✅ Saque dentro do limite de cheque especial funciona

────────────────────────────────────────
  Total: 22 testes — BUILD SUCCESS ✅
```

---

## 💡 Decisões de design

**Transações atômicas**
Transferências usam `@Transactional` garantindo que débito e crédito acontecem juntos ou nenhum acontece. Em caso de falha, o rollback é automático pelo Spring.

**Hierarquia de exceções de domínio**
Todas as exceções de negócio estendem `BankingException` e são capturadas centralmente pelo `GlobalExceptionHandler` (`@RestControllerAdvice`), retornando JSON padronizado com código semântico e timestamp.

**Rastreabilidade completa**
Cada transação persiste `saldo_antes`, `saldo_depois` e um `codigo_autorizacao` único (`AUTH + timestamp`), permitindo auditoria completa de qualquer movimentação.

**Cheque especial**
O método `temSaldoSuficiente()` verifica `saldo + limiteChequeEspecial`, permitindo saldo negativo controlado dentro do limite configurado individualmente por conta.

**DTOs separados das entidades**
`Request` e `Response` são classes distintas das entidades JPA, evitando exposição de dados internos e facilitando a evolução independente da API e do modelo de banco.

---

## 🗄️ Modelo de dados

### `contas_correntes`

```sql
id                      BIGINT          PK AUTO_INCREMENT
numero_conta            VARCHAR(10)     UNIQUE NOT NULL   -- gerado automaticamente
agencia                 VARCHAR(6)      NOT NULL
titular                 VARCHAR(100)    NOT NULL
cpf                     VARCHAR(14)     UNIQUE NOT NULL   -- formato: 000.000.000-00
saldo                   DECIMAL(15,2)   NOT NULL
limite_cheque_especial  DECIMAL(15,2)   DEFAULT 0.00
status                  ENUM            ATIVA | INATIVA | BLOQUEADA | ENCERRADA
data_abertura           DATETIME        NOT NULL
data_atualizacao        DATETIME
```

### `transacoes`

```sql
id                      BIGINT          PK AUTO_INCREMENT
conta_id                BIGINT          FK → contas_correntes
numero_conta_destino    VARCHAR(10)     -- preenchido em transferências
tipo                    ENUM            DEPOSITO | SAQUE | TRANSFERENCIA_ENVIADA | TRANSFERENCIA_RECEBIDA
valor                   DECIMAL(15,2)   NOT NULL
saldo_antes             DECIMAL(15,2)
saldo_depois            DECIMAL(15,2)
descricao               VARCHAR(255)
status                  ENUM            CONCLUIDA | PENDENTE | CANCELADA | FALHA
data_hora               DATETIME        NOT NULL
codigo_autorizacao      VARCHAR(20)     -- AUTH + timestamp único
```

---

## 📬 Exemplos de Request / Response

### Criar conta — `POST /api/contas`

```json
// Request
{
  "titular": "João Silva",
  "cpf": "529.982.247-25",
  "agencia": "0001",
  "depositoInicial": 1000.00,
  "limiteChequeEspecial": 500.00
}

// Response 201
{
  "id": 1,
  "numeroConta": "3847291056",
  "agencia": "0001",
  "titular": "João Silva",
  "cpf": "529.982.247-25",
  "saldo": 1000.00,
  "limiteChequeEspecial": 500.00,
  "saldoDisponivel": 1500.00,
  "status": "ATIVA",
  "dataAbertura": "2025-06-01T10:30:00"
}
```

### Transferência — `POST /api/contas/{numero}/transferir`

```json
// Request
{
  "numeroContaDestino": "9182736450",
  "valor": 300.00,
  "descricao": "Pagamento aluguel"
}

// Response 200
{
  "id": 5,
  "numeroConta": "3847291056",
  "numeroContaDestino": "9182736450",
  "tipo": "TRANSFERENCIA_ENVIADA",
  "valor": 300.00,
  "saldoAntes": 1000.00,
  "saldoDepois": 700.00,
  "descricao": "Pagamento aluguel → Maria Souza",
  "status": "CONCLUIDA",
  "dataHora": "2025-06-01T11:00:00",
  "codigoAutorizacao": "AUTH1748772000123",
  "mensagem": "Transferência realizada com sucesso"
}
```

### Erro — Saldo insuficiente

```json
// Response 422
{
  "codigo": "SALDO_INSUFICIENTE",
  "mensagem": "Saldo insuficiente para realizar a operação",
  "detalhes": null,
  "timestamp": "2025-06-01T11:05:00"
}
```

---

## 👩‍💻 Autora

<div align="center">

Desenvolvido com ☕ e dedicação por **Michelle Silva**

[![LinkedIn](https://img.shields.io/badge/LinkedIn-Michelle%20Silva-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/michelle-silva-432311318/)
[![GitHub](https://img.shields.io/badge/GitHub-Michellesilvaa00-100000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/Michellesilvaa00)

<br/>

⭐ **Se este projeto foi útil, deixe uma estrela!** Isso ajuda o projeto a ganhar visibilidade.

</div>

