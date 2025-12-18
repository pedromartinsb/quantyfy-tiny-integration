CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    tiny_id VARCHAR(50) NOT NULL UNIQUE,
    numero VARCHAR(50),
    numero_ecommerce VARCHAR(100),
    data_pedido DATE,
    data_entrega DATE,
    data_faturamento DATE,
    nome_cliente VARCHAR(255),
    valor NUMERIC(15,2),
    total_produtos NUMERIC(15,2),
    total_pedido NUMERIC(15,2),
    numero_ordem_compra VARCHAR(100),
    forma_envio VARCHAR(100),
    situacao VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);

CREATE UNIQUE INDEX idx_orders_tiny_id
ON orders (tiny_id);
CREATE INDEX idx_orders_data_pedido
ON orders (data_pedido);
CREATE INDEX idx_orders_situacao
ON orders (situacao);
CREATE INDEX idx_orders_data_situacao
ON orders (data_pedido, situacao);
CREATE INDEX idx_orders_numero_ecommerce
ON orders (numero_ecommerce);
CREATE INDEX idx_orders_nome_cliente
ON orders (nome_cliente);
