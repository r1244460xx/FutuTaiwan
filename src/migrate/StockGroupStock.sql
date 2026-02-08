CREATE TABLE IF NOT EXISTS stock_group_stocks (
    stock_group_id BIGINT NOT NULL,
    stock_id BIGINT NOT NULL,
    PRIMARY KEY (stock_group_id, stock_id),
    CONSTRAINT fk_stock_group_stocks_group
        FOREIGN KEY (stock_group_id)
        REFERENCES stock_groups(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_stock_group_stocks_stock
        FOREIGN KEY (stock_id)
        REFERENCES stocks(id)
        ON DELETE CASCADE
);
