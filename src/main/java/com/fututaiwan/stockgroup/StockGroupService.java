package com.fututaiwan.stockgroup;

import com.fututaiwan.member.Member;
import com.fututaiwan.member.MemberService;
import com.fututaiwan.stock.Stock;
import com.fututaiwan.stock.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StockGroupService {

    private final StockGroupRepository stockGroupRepository;
    private final StockService stockService;
    private final MemberService memberService; // 注入 MemberService

    @Autowired
    public StockGroupService(StockGroupRepository stockGroupRepository, StockService stockService, MemberService memberService) {
        this.stockGroupRepository = stockGroupRepository;
        this.stockService = stockService;
        this.memberService = memberService;
    }

    public List<StockGroup> getAllStockGroups() {
        return stockGroupRepository.findAll();
    }

    public Optional<StockGroup> getStockGroupById(Long id) {
        return stockGroupRepository.findById(id);
    }

    public Optional<StockGroup> getStockGroupByName(String name) {
        return stockGroupRepository.findByName(name);
    }

    public List<StockGroup> getStockGroupsByMemberId(Long memberId) {
        // 檢查會員是否存在，如果不存在則拋出異常
        memberService.getMemberById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id " + memberId));
        return stockGroupRepository.findByMember_Id(memberId);
    }

    @Transactional
    public StockGroup createStockGroup(StockGroup stockGroup, Long memberId) {
        if (stockGroupRepository.findByName(stockGroup.getName()).isPresent()) {
            throw new IllegalArgumentException("Stock group with name '" + stockGroup.getName() + "' already exists.");
        }
        Member member = memberService.getMemberById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id " + memberId));

        stockGroup.setMember(member);
        return stockGroupRepository.save(stockGroup);
    }

    @Transactional
    public StockGroup updateStockGroup(Long id, StockGroup updatedStockGroup) {
        return stockGroupRepository.findById(id).map(stockGroup -> {
            stockGroup.setName(updatedStockGroup.getName());
            // 針對重複名稱拋出例外
            stockGroup.setDescription(updatedStockGroup.getDescription());
            return stockGroupRepository.save(stockGroup);
        }).orElseThrow(() -> new RuntimeException("Stock group not found with id " + id));
    }

    @Transactional
    public void deleteStockGroup(Long id) {
        if (stockGroupRepository.existsById(id)) {
            stockGroupRepository.deleteById(id);
        } else {
            throw new RuntimeException("Stock group not found with id " + id);
        }
    }

    @Transactional
    public StockGroup addStockToGroup(Long stockGroupId, Long stockId) {
        StockGroup stockGroup = stockGroupRepository.findById(stockGroupId)
                .orElseThrow(() -> new RuntimeException("Stock group not found with id " + stockGroupId));

        Stock stock = stockService.getStockById(stockId)
                .orElseThrow(() -> new RuntimeException("Stock not found with id " + stockId));

        stockGroup.getStocks().add(stock);
        return stockGroupRepository.save(stockGroup);
    }

    @Transactional
    public StockGroup removeStockFromGroup(Long stockGroupId, Long stockId) {
        StockGroup stockGroup = stockGroupRepository.findById(stockGroupId)
                .orElseThrow(() -> new RuntimeException("Stock group not found with id " + stockGroupId));

        Stock stock = stockService.getStockById(stockId)
                .orElseThrow(() -> new RuntimeException("Stock not found with id " + stockId));

        if (!stockGroup.getStocks().contains(stock)) {
            throw new RuntimeException("Stock with id " + stockId + " is not in stock group with id " + stockGroupId);
        }

        stockGroup.getStocks().remove(stock);
        return stockGroupRepository.save(stockGroup);
    }
}
