# Mini Banco 🏦

API REST de carteira digital desenvolvida com **Java 21** e **Spring Boot 3**, simulando operações bancárias básicas com foco em regras de negócio, consistência de dados e boas práticas.

> 🚧 Projeto em desenvolvimento - veja o [roadmap](#%EF%B8%8F-roadmap).

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

| Método | Rota                    | Descrição                               |
|--------|-------------------------|-----------------------------------------|
| POST   | `/clientes`             | Cadastra um cliente                     |
| GET    | `/clientes/{id}`        | Busca um cliente                        |
| POST   | `/contas`               | Abre conta para cliente                 |
| GET    | `/contas/{id}`          | Consulta conta e saldo                  |
| POST   | `/contas/{id}/deposito` | Realiza depósito                        |
| POST   | `/contas/{id}/saque`    | Realiza saque                           |
| POST   | `/transferencias`       | Transfere entre contas                  |
| GET    | `/contas/{id}/extrato`  | Extrato paginado com filtro por período |

## 🧠 Decisões técnicas

- **`BigDecimal` para valores monetários**, evitando erros de arredondamento de `double`.
- **Regras de negócio na entidade** (`Conta.depositar` / `Conta.sacar`), mantendo o service enxuto.
- **Lock otimista com `@Version`**, impedindo que operações simultâneas sobrescrevam o saldo.
- **Flyway** controla o schema; o Hibernate apenas valida (`ddl-auto: validate`).
- **Erros padronizados** no formato Problem Details (RFC 7807).
- **Transferência atômica** com `@Transactional`: débito, crédito e registro das transações acontecem juntos ou nenhum acontece.
- **Histórico imutável**: cada movimentação gera um registro de transação que nunca é alterado, com o saldo após a operação.
- **Conflitos de concorrência** retornam 409, graças ao lock otimista.
- **Extrato paginado** com `Pageable`, ordenado das transações mais recentes, filtro por período (padrão de 30 dias) e limite máximo de 100 itens por página.
- **Prevenção do problema N+1** com `@EntityGraph`, carregando a conta contraparte na mesma consulta.
- **Índice composto** em `(conta_id, data_hora)` para consultas de extrato rápidas.
- **Senhas com hash BCrypt** (salt automático e custo configurável), nunca armazenadas em texto puro nem expostas nas respostas da API.

## 🗺️ Roadmap

- [x] Cadastro de clientes e abertura de contas
- [x] Depósito, saque e consulta de saldo
- [x] Transferência entre contas
- [x] Extrato com histórico de transações paginado
- [ ] Limite diário de saque/transferência
- [ ] Autenticação com Spring Security + JWT
- [ ] Testes de integração com Testcontainers
- [ ] Pipeline CI com GitHub Actions
- [ ] Deploy em nuvem

## 👤 Autor

**Lucas Carreiro** — [LinkedIn](https://www.linkedin.com/in/lucascarreiro) · [GitHub](https://github.com/lucascarreiroc)
