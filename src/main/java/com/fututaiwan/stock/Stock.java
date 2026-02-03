package com.fututaiwan.stock;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

/**
 * 個股實體 (Stock Entity)
 * 對應資料庫中的 'stocks' 表格
 */
@Entity
@Table(name = "stocks")
@Data // Lombok: 自動生成 getter, setter, toString, equals, hashCode
@NoArgsConstructor // Lombok: 自動生成無參建構子
@AllArgsConstructor // Lombok: 自動生成包含所有欄位的建構子
@Builder // Lombok: 提供 Builder 模式
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 對應 PostgreSQL 的 SERIAL
    private Long id;

    @Column(name = "stock_code", unique = true, nullable = false, length = 20)
    private String stockCode;

    @Column(name = "stock_name", unique = true, nullable = false, length = 100)
    private String stockName;

    @Column(name = "industry", length = 100)
    private String industry;

    @UpdateTimestamp // 自動在實體更新時設置時間
    @Column(name = "last_updated", nullable = false)
    private Instant lastUpdated;
}