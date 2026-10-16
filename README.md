# Mini Banco 🏦

API REST de carteira digital desenvolvida com **Java 21** e **Spring Boot 3**, simulando operações bancárias básicas com foco em regras de negócio, consistência de dados e boas práticas.

> 🚧 Projeto em desenvolvimento - veja o [roadmap](#-roadmap).

## 🛠️ Tecnologias

- Java 21 e Spring Boot 3
- Spring Data JPA + PostgreSQL
- Flyway (versionamento do banco)
- Bean Validation
- Springdoc OpenAPI (Swagger)
- JUnit 5, Mockito e AssertJ
- Docker Compose

## ▶️ Como rodar

Pré-requisitos: Java 21, Maven e Docker.

```bash
# 1. Subir o PostgreSQL
docker compose up -d

# 2. Rodar a aplicação
mvn spring-boot:run
```

Documentação interativa: http://localhost:8080/swagger-ui.html

## 📌 Endpoints

| Método | Rota                     | Descrição               |
|--------|--------------------------|-------------------------|
| POST   | `/clientes`              | Cadastra um cliente     |
| GET    | `/clientes/{id}`         | Busca um cliente        |
| POST   | `/contas`                | Abre conta para cliente |
| GET    | `/contas/{id}`           | Consulta conta e saldo  |
| POST   | `/contas/{id}/deposito`  | Realiza depósito        |
| POST   | `/contas/{id}/saque`     | Realiza saque           |

## 🧠 Decisões técnicas

- **`BigDecimal` para valores monetários**, evitando erros de arredondamento de `double`.
- **Regras de negócio na entidade** (`Conta.depositar` / `Conta.sacar`), mantendo o service enxuto.
- **Lock otimista com `@Version`**, impedindo que operações simultâneas sobrescrevam o saldo.
- **Flyway** controla o schema; o Hibernate apenas valida (`ddl-auto: validate`).
- **Erros padronizados** no formato Problem Details (RFC 7807).

## 🗺️ Roadmap

- [x] Cadastro de clientes e abertura de contas
- [x] Depósito, saque e consulta de saldo
- [x] Transferência entre contas
- [ ] Extrato com histórico de transações paginado
- [ ] Limite diário de saque/transferência
- [ ] Autenticação com Spring Security + JWT
- [ ] Testes de integração com Testcontainers
- [ ] Pipeline CI com GitHub Actions
- [ ] Deploy em nuvem

## 👤 Autor

**Lucas Carreiro** — [LinkedIn](https://www.linkedin.com/in/lucascarreiro) · [GitHub](https://github.com/lucascarreiroc)
