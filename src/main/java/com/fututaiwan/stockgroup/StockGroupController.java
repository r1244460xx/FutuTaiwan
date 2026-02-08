package com.fututaiwan.stockgroup;

import com.fututaiwan.stock.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/stock-groups")
public class StockGroupController {

    private final StockGroupService stockGroupService;

    @Autowired
    public StockGroupController(StockGroupService stockGroupService) {
        this.stockGroupService = stockGroupService;
    }

    @GetMapping
    public ResponseEntity<List<StockGroup>> getAllStockGroups() {
        List<StockGroup> stockGroups = stockGroupService.getAllStockGroups();
        return ResponseEntity.ok(stockGroups);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockGroup> getStockGroupById(@PathVariable Long id) {
        return stockGroupService.getStockGroupById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 修改 createStockGroup 以接收 memberId
    @PostMapping("/member/{memberId}")
    public ResponseEntity<StockGroup> createStockGroup(@PathVariable Long memberId, @RequestBody StockGroup stockGroup) {
        try {
            StockGroup createdStockGroup = stockGroupService.createStockGroup(stockGroup, memberId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdStockGroup);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null); // 409 Conflict for duplicate name
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Member not found
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<StockGroup> updateStockGroup(@PathVariable Long id, @RequestBody StockGroup stockGroup) {
        try {
            StockGroup updatedStockGroup = stockGroupService.updateStockGroup(id, stockGroup);
            return ResponseEntity.ok(updatedStockGroup);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStockGroup(@PathVariable Long id) {
        try {
            stockGroupService.deleteStockGroup(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search/name")
    public ResponseEntity<StockGroup> getStockGroupByName(@RequestParam String name) {
        return stockGroupService.getStockGroupByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{stockGroupId}/stocks/{stockId}")
    public ResponseEntity<StockGroup> addStockToGroup(@PathVariable Long stockGroupId, @PathVariable Long stockId) {
        try {
            StockGroup updatedStockGroup = stockGroupService.addStockToGroup(stockGroupId, stockId);
            return ResponseEntity.ok(updatedStockGroup);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // StockGroup or Stock not found
        }
    }

    @DeleteMapping("/{stockGroupId}/stocks/{stockId}")
    public ResponseEntity<StockGroup> removeStockFromGroup(@PathVariable Long stockGroupId, @PathVariable Long stockId) {
        try {
            StockGroup updatedStockGroup = stockGroupService.removeStockFromGroup(stockGroupId, stockId);
            return ResponseEntity.ok(updatedStockGroup);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // StockGroup or Stock not found, or Stock not in group
        }
    }

    @GetMapping("/{stockGroupId}/stocks")
    public ResponseEntity<Set<Stock>> getStocksInGroup(@PathVariable Long stockGroupId) {
        return stockGroupService.getStockGroupById(stockGroupId)
                .map(stockGroup -> ResponseEntity.ok(stockGroup.getStocks()))
                .orElse(ResponseEntity.notFound().build());
    }

    // 新增端點以查詢特定會員的所有股票群組
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<StockGroup>> getStockGroupsByMemberId(@PathVariable Long memberId) {
        List<StockGroup> stockGroups = stockGroupService.getStockGroupsByMemberId(memberId);
        if (stockGroups.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(stockGroups);
    }
}
