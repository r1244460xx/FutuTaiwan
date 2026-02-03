package com.fututaiwan.stock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 個股資料儲存庫 (Stock Repository)
 * 繼承 JpaRepository 提供基本的 CRUD 操作
 */
@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    /**
     * 根據股票代碼查找個股
     * @param stockCode 股票代碼
     * @return 包含個股實體的 Optional
     */
    Optional<Stock> findByStockCode(String stockCode);

    /**
     * 根據股票名稱查找個股
     * @param stockName 股票名稱
     * @return 包含個股實體的 Optional
     */
    Optional<Stock> findByStockName(String stockName);
}