package com.fututaiwan.stockgroup;

import com.fututaiwan.member.Member;
import com.fututaiwan.stock.Stock;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * 個股群組實體 (Stock Group Entity)
 * 用於儲存使用者自訂的個股群組
 */
@Entity
@Table(name = "stock_groups")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_name", nullable = false, length = 100)
    private String groupName;

    // 多個個股群組可以屬於一個會員 (Many stock groups to One Member)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false) // 外鍵指向 member 表的 id
    private Member member;

    // 一個個股群組可以有多個股票，一個股票也可以屬於多個群組 (Many stock groups to Many Stocks)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "stock_group_stocks", // 關聯表的名稱
        joinColumns = @JoinColumn(name = "stock_group_id"), // 本實體 (StockGroup) 在關聯表中的外鍵
        inverseJoinColumns = @JoinColumn(name = "stock_id") // 另一個實體 (Stock) 在關聯表中的外鍵
    )
    @Builder.Default // 使用 Lombok 的 @Builder 時，為集合類型提供預設值
    private Set<Stock> stocks = new HashSet<>();

    @CreationTimestamp // 自動在實體首次持久化時設置時間
    @Column(name = "creation_date", nullable = false, updatable = false)
    private Instant creationDate;

    @UpdateTimestamp // 自動在實體更新時設置時間
    @Column(name = "last_updated_date", nullable = false)
    private Instant lastUpdatedDate;
}