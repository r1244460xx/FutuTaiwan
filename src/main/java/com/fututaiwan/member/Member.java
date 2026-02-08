package com.fututaiwan.member;

import com.fututaiwan.stockgroup.StockGroup;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * 會員實體 (Member Entity)
 * 對應資料庫中的 'members' 表格
 */
@Entity
@Table(name = "members")
@Data // Lombok: 自動生成 getter, setter, toString, equals, hashCode
@NoArgsConstructor // Lombok: 自動生成無參建構子
@AllArgsConstructor // Lombok: 自動生成包含所有欄位的建構子
@Builder // Lombok: 提供 Builder 模式
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 對應 PostgreSQL 的 SERIAL
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "phone_number", unique = true, nullable = false, length = 10)
    private String phoneNumber;

    @Column(name = "national_id_number", unique = true, nullable = false, length = 10)
    private String nationalIdNumber;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "address", length = 255)
    private String address;

    @CreationTimestamp // 自動在實體首次持久化時設置時間
    @Column(name = "registration_date", nullable = false, updatable = false)
    private Instant registrationDate;

    @Column(name = "last_login_date")
    private Instant lastLoginDate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true; // 預設值與資料庫一致

    @Column(name = "role", nullable = false, length = 50)
    private String role = "member"; // 預設值與資料庫一致

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StockGroup> stockGroups = new HashSet<>();
}
