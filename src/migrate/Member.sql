CREATE TABLE members
(
    id                 SERIAL PRIMARY KEY,                                 -- 主鍵 (Primary Key), 自動遞增
    name               VARCHAR(100)       NOT NULL,                        -- 姓名
    phone_number       VARCHAR(10) UNIQUE NOT NULL,                        -- 手機號碼, 必須唯一且不為空
    national_id_number VARCHAR(10) UNIQUE NOT NULL,                        -- 身分證字號, 必須唯一且不為空
    date_of_birth      DATE,                                               -- 生日
    email              VARCHAR(255)       NOT NULL,                        -- 電子郵件, 必須不為空
    password_hash      VARCHAR(255)       NOT NULL,                        -- 密碼的雜湊值, 絕對不要儲存明文密碼
    gender             VARCHAR(10),                                        -- 性別 (例如: 'Male', 'Female', 'Other')
    address            VARCHAR(255),                                       -- 地址
    registration_date  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP, -- 註冊時間
    last_login_date    TIMESTAMP WITH TIME ZONE,                           -- 最後登入時間
    is_active          BOOLEAN                  DEFAULT TRUE,              -- 帳戶是否啟用
    role               VARCHAR(50)              DEFAULT 'member'           -- 會員角色 (例如: 'member', 'admin', 'vip')
);