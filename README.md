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

Para testar as rotas protegidas, faça login em `/auth/login` e use o botão **Authorize** com o token retornado.

## 🧪 Testes

```bash
mvn test
```

Roda os testes unitários e os de integração. Os testes de integração usam Testcontainers, que sobe automaticamente um PostgreSQL temporário em container, então basta ter o Docker em execução, sem precisar subir o banco nem a aplicação.

## 📌 Endpoints

| Método | Rota                     | Descrição                               | Autenticação |
|--------|--------------------------|-----------------------------------------|--------------|
| POST   | `/clientes`              | Cadastra um cliente                     | Pública      |
| POST   | `/auth/login`            | Autentica e retorna token JWT           | Pública      |
| GET    | `/clientes/me`           | Dados do cliente logado                 | Token        |
| POST   | `/contas`                | Abre conta para o cliente logado        | Token        |
| GET    | `/contas`                | Lista as contas do cliente logado       | Token        |
| GET    | `/contas/{id}`           | Consulta conta e saldo                  | Token        |
| POST   | `/contas/{id}/deposito`  | Realiza depósito                        | Token        |
| POST   | `/contas/{id}/saque`     | Realiza saque                           | Token        |
| POST   | `/transferencias`        | Transfere entre contas                  | Token        |
| GET    | `/contas/{id}/extrato`   | Extrato paginado com filtro por período | Token        |     
| GET    | `/contas/{id}/limite`    | Limite diário disponível                | Token        |

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
- **Autenticação stateless com JWT** assinado (HS256), usando o suporte oficial do Spring Security (OAuth2 Resource Server), com validação de assinatura, expiração e emissor.
- **Autorização por posse**: o id do cliente vem sempre do token, nunca da requisição, e cada operação verifica se a conta pertence a quem está logado (403 caso contrário).
- **Proteção contra enumeração de usuários**: CPF inexistente e senha incorreta retornam a mesma resposta.
- **Limite diário configurável** (`application.yml`) para saques e transferências enviadas, calculado a partir do histórico de transações, sem colunas que precisem ser zeradas.
- **Relógio injetável (`Clock`)**, permitindo testar regras que dependem de data e hora com um horário fixo.
- **Testes de integração com Testcontainers**: a API completa (segurança, regras de negócio e banco) é testada contra um PostgreSQL real em container, incluindo a prova de rollback em falhas no meio de uma transferência.

## 🗺️ Roadmap

- [x] Cadastro de clientes e abertura de contas
- [x] Depósito, saque e consulta de saldo
- [x] Transferência entre contas
- [x] Extrato com histórico de transações paginado
- [x] Autenticação com Spring Security + JWT
- [x] Limite diário de saque/transferência
- [x] Testes de integração com Testcontainers
- [ ] Pipeline CI com GitHub Actions
- [ ] Deploy em nuvem

## 👤 Autor

**Lucas Carreiro** — [LinkedIn](https://www.linkedin.com/in/lucascarreiro) · [GitHub](https://github.com/lucascarreiroc)
