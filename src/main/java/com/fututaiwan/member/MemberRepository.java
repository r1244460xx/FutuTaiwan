package com.fututaiwan.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 會員資料儲存庫 (Member Repository)
 * 繼承 JpaRepository 提供基本的 CRUD 操作
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // 根據手機號碼查找會員
    Optional<Member> findByPhoneNumber(String phoneNumber);

    // 根據電子郵件查找會員
    Optional<Member> findByEmail(String email);
}