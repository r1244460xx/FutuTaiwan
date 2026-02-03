CREATE TABLE stocks
(
    id            SERIAL PRIMARY KEY,                                 -- 主鍵 (Primary Key), 自動遞增
    stock_code    VARCHAR(20) UNIQUE NOT NULL,                        -- 股票代碼, 必須唯一且不為空
    stock_name    VARCHAR(100) UNIQUE NOT NULL,                       -- 股票名稱, 必須唯一且不為空
    industry      VARCHAR(100),                                       -- 所屬產業
    last_updated  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP  -- 最後更新時間
);