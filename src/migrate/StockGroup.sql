CREATE TABLE stock_groups
(
    id                  SERIAL PRIMARY KEY,                                 -- 主鍵 (Primary Key), 自動遞增
    group_name          VARCHAR(100)       NOT NULL,                        -- 群組名稱, 必須不為空
    member_id           BIGINT             NOT NULL,                        -- 所屬會員ID, 必須不為空
    creation_date       TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 建立時間
    last_updated_date   TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 最後更新時間

    CONSTRAINT fk_member
        FOREIGN KEY (member_id)
        REFERENCES members (id)
        ON DELETE CASCADE -- 當會員被刪除時，其所有群組也一併刪除
);

CREATE TABLE stock_group_stocks
(
    stock_group_id      BIGINT NOT NULL,
    stock_id            BIGINT NOT NULL,

    PRIMARY KEY (stock_group_id, stock_id), -- 複合主鍵

    CONSTRAINT fk_stock_group
        FOREIGN KEY (stock_group_id)
        REFERENCES stock_groups (id)
        ON DELETE CASCADE, -- 當群組被刪除時，其在關聯表中的記錄也一併刪除

    CONSTRAINT fk_stock
        FOREIGN KEY (stock_id)
        REFERENCES stocks (id)
        ON DELETE CASCADE -- 當個股被刪除時，其在關聯表中的記錄也一併刪除
);