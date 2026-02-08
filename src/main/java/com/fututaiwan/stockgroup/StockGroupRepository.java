package com.fututaiwan.stockgroup;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockGroupRepository extends JpaRepository<StockGroup, Long> {
    Optional<StockGroup> findByName(String name);
    List<StockGroup> findByMember_Id(Long memberId); // 新增：根據會員ID查詢股票群組
}
