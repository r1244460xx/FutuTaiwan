### 會員資料表 (`members`) Schema

| 欄位名稱 (`Column Name`) | 資料類型 (`Data Type`) | 約束 (`Constraints`) | 說明 (`Description`) |
| :----------------------- | :--------------------- | :---------------------------------------------------- | :------------------------------------------------- |
| `id` | `SERIAL` | `PRIMARY KEY` | 主鍵，自動遞增 |
| `name` | `VARCHAR(100)` | `NOT NULL` | 會員姓名 |
| `phone_number` | `VARCHAR(10)` | `UNIQUE`, `NOT NULL` | 手機號碼，必須唯一且不為空 |
| `national_id_number` | `VARCHAR(10)` | `UNIQUE`, `NOT NULL` | 身分證字號，必須唯一且不為空 |
| `date_of_birth` | `DATE` | | 生日 |
| `email` | `VARCHAR(255)` | `NOT NULL` | 電子郵件，必須不為空 |
| `password_hash` | `VARCHAR(255)` | `NOT NULL` | 密碼的雜湊值，絕對不要儲存明文密碼 |
| `gender` | `VARCHAR(10)` | | 性別 (例如: 'Male', 'Female', 'Other') |
| `address` | `VARCHAR(255)` | | 地址 |
| `registration_date` | `TIMESTAMP WITH TIME ZONE` | `NOT NULL`, `DEFAULT CURRENT_TIMESTAMP` | 註冊時間，自動記錄 |
| `last_login_date` | `TIMESTAMP WITH TIME ZONE` | | 最後登入時間 |
| `is_active` | `BOOLEAN` | `NOT NULL`, `DEFAULT TRUE` | 帳戶是否啟用，預設為啟用 |
| `role` | `VARCHAR(50)` | `NOT NULL`, `DEFAULT 'member'` | 會員角色 (例如: 'member', 'admin', 'vip')，預設為 'member' |

### 個股資料表 (`stocks`) Schema

| 欄位名稱 (`Column Name`) | 資料類型 (`Data Type`) | 約束 (`Constraints`) | 說明 (`Description`) |
| :----------------------- | :--------------------- | :---------------------------------------------------- | :------------------------------------------------- |
| `id` | `SERIAL` | `PRIMARY KEY` | 主鍵，自動遞增 |
| `stock_code` | `VARCHAR(20)` | `UNIQUE`, `NOT NULL` | 股票代碼，必須唯一且不為空 |
| `stock_name` | `VARCHAR(100)` | `UNIQUE`, `NOT NULL` | 股票名稱，必須唯一且不為空 |
| `industry` | `VARCHAR(100)` | | 所屬產業 |
| `last_updated` | `TIMESTAMP WITH TIME ZONE` | `NOT NULL`, `DEFAULT CURRENT_TIMESTAMP` | 最後更新時間 |

### 個股群組資料表 (`stock_groups`) Schema

| 欄位名稱 (`Column Name`) | 資料類型 (`Data Type`) | 約束 (`Constraints`) | 說明 (`Description`) |
| :----------------------- | :--------------------- | :---------------------------------------------------- | :------------------------------------------------- |
| `id` | `SERIAL` | `PRIMARY KEY` | 主鍵，自動遞增 |
| `group_name` | `VARCHAR(100)` | `NOT NULL` | 群組名稱，必須不為空 |
| `member_id` | `BIGINT` | `NOT NULL`, `FOREIGN KEY (members.id)` | 所屬會員ID，必須不為空 |
| `creation_date` | `TIMESTAMP WITH TIME ZONE` | `NOT NULL`, `DEFAULT CURRENT_TIMESTAMP` | 建立時間 |
| `last_updated_date` | `TIMESTAMP WITH TIME ZONE` | `NOT NULL`, `DEFAULT CURRENT_TIMESTAMP` | 最後更新時間 |

### 個股群組關聯表 (`stock_group_stocks`) Schema

| 欄位名稱 (`Column Name`) | 資料類型 (`Data Type`) | 約束 (`Constraints`) | 說明 (`Description`) |
| :----------------------- | :--------------------- | :---------------------------------------------------- | :------------------------------------------------- |
| `stock_group_id` | `BIGINT` | `PRIMARY KEY`, `FOREIGN KEY (stock_groups.id)` | 個股群組ID |
| `stock_id` | `BIGINT` | `PRIMARY KEY`, `FOREIGN KEY (stocks.id)` | 個股ID |
