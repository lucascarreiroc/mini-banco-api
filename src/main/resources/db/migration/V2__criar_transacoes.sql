CREATE TABLE transacoes
(
    id                   BIGSERIAL PRIMARY KEY,
    conta_id             BIGINT         NOT NULL REFERENCES contas (id),
    tipo                 VARCHAR(30)    NOT NULL,
    valor                NUMERIC(19, 2) NOT NULL,
    saldo_apos           NUMERIC(19, 2) NOT NULL,
    conta_contraparte_id BIGINT REFERENCES contas (id),
    data_hora            TIMESTAMP      NOT NULL
);

CREATE INDEX idx_transacoes_conta_data ON transacoes (conta_id, data_hora DESC);