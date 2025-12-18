CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,

    -- Identificador Tiny
    tiny_id VARCHAR(50) NOT NULL UNIQUE,

    -- Identificação
    nome VARCHAR(255) NOT NULL,
    codigo VARCHAR(100),
    unidade VARCHAR(20),

    -- Preços
    preco NUMERIC(15,2),
    preco_promocional NUMERIC(15,2),
    preco_custo NUMERIC(15,2),
    preco_custo_medio NUMERIC(15,2),

    -- Estoque / Logística
    stock INTEGER,
    stock_reserved INTEGER,

    -- Status / Tipo
    situacao CHAR(1),
    tipo CHAR(1),
    tipo_variacao CHAR(1),
    classe_produto CHAR(1),

    -- Relacionamentos
    id_produto_pai VARCHAR(50),

    -- Categoria / Marca
    categoria VARCHAR(255),

    -- Auditoria
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_products_codigo ON products (codigo);
CREATE INDEX idx_products_categoria ON products (categoria);
CREATE INDEX idx_products_situacao ON products (situacao);
CREATE INDEX idx_products_tipo_variacao ON products (tipo_variacao);
CREATE INDEX idx_products_produto_pai ON products (id_produto_pai);
CREATE UNIQUE INDEX uq_products_codigo ON products (codigo);
