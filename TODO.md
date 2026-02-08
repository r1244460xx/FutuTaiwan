# 專案優化建議 (TODO)

以下是針對目前專案原始碼的審閱建議，可作為未來優化和改進的參考：

## 主要建議

1.  **引入 DTOs (Data Transfer Objects)**:
    *   **目的**: 提高 API 的安全性、靈活性和可維護性。
    *   **實踐**: 為每個 Entity 的輸入 (例如 `MemberCreateRequest`, `StockGroupUpdateRequest`) 和輸出 (例如 `MemberResponse`, `StockGroupResponse`) 創建專用的 DTOs。
    *   **好處**:
        *   **安全性**: 過濾掉敏感資訊 (如 `passwordHash`)，防止客戶端修改不應修改的欄位 (如 `id`, `registrationDate`)。
        *   **靈活性**: 允許 API 響應與內部 Entity 結構不同，方便未來修改。
        *   **可維護性**: 分離 API 契約與內部模型。

2.  **全局異常處理 (Global Exception Handling)**:
    *   **目的**: 統一處理應用程式中的異常，提供標準化的錯誤響應。
    *   **實踐**: 實現一個 `@ControllerAdvice` 類別，使用 `@ExceptionHandler` 註解來捕獲特定類型的異常，並返回一致的 `ResponseEntity` 錯誤訊息和 HTTP 狀態碼。
    *   **好處**: 避免在每個 Controller 方法中重複的 `try-catch` 區塊，提高程式碼整潔度，並向客戶端提供友好的錯誤訊息。

3.  **輸入驗證 (Input Validation)**:
    *   **目的**: 確保客戶端傳入的資料符合預期的格式和約束。
    *   **實踐**: 結合 DTOs 和 JSR 303/380 註解 (例如 `@NotNull`, `@Size`, `@Email`, `@Pattern` 等) 在 Controller 層對輸入資料進行驗證。使用 `@Valid` 註解觸發驗證。
    *   **好處**: 在業務邏輯執行前捕獲無效輸入，減少錯誤，提高應用程式的健壯性。

4.  **`StockGroup` 的 `ManyToMany` 級聯策略 (Cascade Strategy)**:
    *   **目的**: 確保關聯實體的生命週期管理符合業務邏輯。
    *   **實踐**: 重新審視 `StockGroup.java` 中 `stocks` 集合的 `@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})` 策略。
    *   **考量**: 通常 `Stock` 應該是獨立管理的實體。如果 `Stock` 的生命週期不應由 `StockGroup` 的持久化或合併操作來控制，則應移除或調整 `CascadeType.PERSIST` 和 `CascadeType.MERGE`。

## 潛在邏輯錯誤與修復建議 (提交前考慮)

以下是目前專案中可能導致資料庫約束異常的潛在邏輯錯誤，建議在提交前考慮修復：

1.  **`StockGroupService.java` 中的 `updateStockGroup` 方法未檢查名稱唯一性**:
    *   **問題**: `StockGroup` 的 `name` 欄位是唯一的。`updateStockGroup` 方法在更新群組名稱時，沒有檢查新的名稱是否已被其他群組使用。這可能導致資料庫拋出唯一約束違規錯誤。
    *   **修復建議**: 在更新前，檢查 `updatedStockGroup.getName()` 是否與現有群組的名稱衝突 (排除自身)。
    *   **範例程式碼**:
        ```java
        @Transactional
        public StockGroup updateStockGroup(Long id, StockGroup updatedStockGroup) {
            return stockGroupRepository.findById(id).map(stockGroup -> {
                // 檢查新的名稱是否與其他群組衝突 (除了自身)
                if (!stockGroup.getName().equals(updatedStockGroup.getName()) && stockGroupRepository.findByName(updatedStockGroup.getName()).isPresent()) {
                    throw new IllegalArgumentException("Stock group with name '" + updatedStockGroup.getName() + "' already exists.");
                }
                stockGroup.setName(updatedStockGroup.getName());
                stockGroup.setDescription(updatedStockGroup.getDescription());
                return stockGroupRepository.save(stockGroup);
            }).orElseThrow(() -> new RuntimeException("Stock group not found with id " + id));
        }
        ```

2.  **`StockService.java` 中的 `updateStock` 方法未檢查代碼唯一性**:
    *   **問題**: `Stock` 的 `code` 欄位是唯一的。`updateStock` 方法在更新股票代碼時，沒有檢查新的代碼是否已被其他股票使用。這可能導致資料庫拋出唯一約束違規錯誤。
    *   **修復建議**: 在更新前，檢查 `updatedStock.getCode()` 是否與現有股票的代碼衝突 (排除自身)。
    *   **範例程式碼**:
        ```java
        @Transactional
        public Stock updateStock(Long id, Stock updatedStock) {
            return stockRepository.findById(id).map(stock -> {
                // 檢查新的代碼是否與其他股票衝突 (除了自身)
                if (!stock.getCode().equals(updatedStock.getCode()) && stockRepository.findByCode(updatedStock.getCode()).isPresent()) {
                    throw new IllegalArgumentException("Stock with code '" + updatedStock.getCode() + "' already exists.");
                }
                stock.setCode(updatedStock.getCode());
                stock.setName(updatedStock.getName());
                return stockRepository.save(stock);
            }).orElseThrow(() -> new RuntimeException("Stock not found with id " + id));
        }
        ```
