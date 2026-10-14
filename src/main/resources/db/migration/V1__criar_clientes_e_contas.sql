CREATE TABLE clientes (
    id        BIGSERIAL PRIMARY KEY,
    nome      VARCHAR(120) NOT NULL,
    cpf       VARCHAR(11)  NOT NULL UNIQUE,
    email     VARCHAR(120) NOT NULL UNIQUE,
    criado_em TIMESTAMP    NOT NULL
);

CREATE TABLE contas (
    id         BIGSERIAL PRIMARY KEY,
    numero     VARCHAR(8)     NOT NULL UNIQUE,
    saldo      NUMERIC(19, 2) NOT NULL DEFAULT 0,
    cliente_id BIGINT         NOT NULL REFERENCES clientes (id),
    criada_em  TIMESTAMP      NOT NULL,
    version    BIGINT         NOT NULL DEFAULT 0
);
