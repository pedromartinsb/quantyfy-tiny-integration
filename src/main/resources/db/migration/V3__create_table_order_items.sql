CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_codigo VARCHAR(100),
    unidade VARCHAR(10),
    quantidade NUMERIC(10,2) NOT NULL,
    valor_unitario NUMERIC(12,2) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),

    CONSTRAINT fk_order_items_order
        FOREIGN KEY (order_id)
        REFERENCES orders(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_order_items_product
            FOREIGN KEY (product_codigo)
            REFERENCES products(codigo)
            ON DELETE CASCADE
);

CREATE INDEX idx_order_items_order_id
    ON order_items(order_id);
CREATE INDEX idx_order_items_product_codigo
    ON order_items(product_codigo);