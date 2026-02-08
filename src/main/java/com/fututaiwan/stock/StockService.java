package com.fututaiwan.stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StockService {

    private final StockRepository stockRepository;

    @Autowired
    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    public Optional<Stock> getStockById(Long id) {
        return stockRepository.findById(id);
    }

    public Optional<Stock> getStockByCode(String code) { // 將 getStockBySymbol 改為 getStockByCode
        return stockRepository.findByCode(code); // 將 findBySymbol 改為 findByCode
    }

    @Transactional
    public Stock createStock(Stock stock) {
        if (stockRepository.findByCode(stock.getCode()).isPresent()) { // 將 findBySymbol 和 getSymbol 改為 findByCode 和 getCode
            throw new IllegalArgumentException("Stock with code '" + stock.getCode() + "' already exists."); // 將 symbol 改為 code
        }
        return stockRepository.save(stock);
    }

    @Transactional
    public Stock updateStock(Long id, Stock updatedStock) {
        return stockRepository.findById(id).map(stock -> {
            stock.setCode(updatedStock.getCode()); // 將 setSymbol 和 getSymbol 改為 setCode 和 getCode
            stock.setName(updatedStock.getName());
            return stockRepository.save(stock);
        }).orElseThrow(() -> new RuntimeException("Stock not found with id " + id));
    }

    @Transactional
    public void deleteStock(Long id) {
        if (stockRepository.existsById(id)) {
            stockRepository.deleteById(id);
        } else {
            throw new RuntimeException("Stock not found with id " + id);
        }
    }
}
