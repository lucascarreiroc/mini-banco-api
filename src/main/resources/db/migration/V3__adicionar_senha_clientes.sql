ALTER TABLE clientes ADD COLUMN senha VARCHAR(100);

-- Clientes criados antes desta versão recebem a senha provisória "senha123"
-- (aceitável apenas em ambiente de desenvolvimento)
UPDATE clientes
SET senha = '$2a$10$o6zxtvDbpUa7FwmJ8vUbmetl48RAho.VXlxmz5DJYJh8gCS.NJsga'
WHERE senha IS NULL;

ALTER TABLE clientes ALTER COLUMN senha SET NOT NULL;