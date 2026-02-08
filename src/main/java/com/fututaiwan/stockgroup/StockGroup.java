package com.fututaiwan.stockgroup;

import com.fututaiwan.member.Member;
import com.fututaiwan.stock.Stock;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * 股票群組實體 (StockGroup Entity)
 * 對應資料庫中的 'stock_groups' 表格
 */
@Entity
@Table(name = "stock_groups")
@Data // Lombok: 自動生成 getter, setter, toString, equals, hashCode
@NoArgsConstructor // Lombok: 自動生成無參建構子
@AllArgsConstructor // Lombok: 自動生成包含所有欄位的建構子
@Builder // Lombok: 提供 Builder 模式
public class StockGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 對應 PostgreSQL 的 SERIAL
    private Long id;

    @Column(name = "name", nullable = false, length = 100, unique = true)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY) // 多個 StockGroup 可以屬於一個 Member
    @JoinColumn(name = "member_id", nullable = false) // 外鍵欄位
    private Member member;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "stock_group_stocks",
            joinColumns = @JoinColumn(name = "stock_group_id"),
            inverseJoinColumns = @JoinColumn(name = "stock_id")
    )
    private Set<Stock> stocks = new HashSet<>();
}
