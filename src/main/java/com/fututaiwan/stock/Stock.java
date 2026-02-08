package com.fututaiwan.stock;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 股票實體 (Stock Entity)
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

    @Column(name = "code", nullable = false, length = 10, unique = true) // 將 symbol 改為 code
    private String code; // 股票代碼，例如 "2330"

    @Column(name = "name", nullable = false, length = 100, unique = true)
    private String name; // 公司名稱，例如 "台積電"
}
