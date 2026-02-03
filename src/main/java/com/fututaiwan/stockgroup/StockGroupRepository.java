package com.fututaiwan.stockgroup;

import com.fututaiwan.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 個股群組資料儲存庫 (Stock Group Repository)
 * 繼承 JpaRepository 提供基本的 CRUD 操作
 */
@Repository
public interface StockGroupRepository extends JpaRepository<StockGroup, Long> {

    /**
     * 根據群組名稱和所屬會員查找個股群組
     * @param groupName 群組名稱
     * @param member 所屬會員
     * @return 包含個股群組實體的 Optional
     */
    Optional<StockGroup> findByGroupNameAndMember(String groupName, Member member);

    /**
     * 根據所屬會員查找所有個股群組
     * @param member 所屬會員
     * @return 包含個股群組實體的列表
     */
    List<StockGroup> findByMember(Member member);
}