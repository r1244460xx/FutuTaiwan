package com.fututaiwan.stock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Stock Repository Tests")
class StockRepositoryTest {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Stock stock;

    @BeforeEach
    void setUp() {
        stock = Stock.builder()
                .code("2330")
                .name("台積電")
                .build();
    }

    @Test
    @DisplayName("Should save a stock")
    void shouldSaveStock() {
        Stock savedStock = stockRepository.save(stock);
        assertThat(savedStock).isNotNull();
        assertThat(savedStock.getId()).isNotNull();
        assertThat(savedStock.getCode()).isEqualTo("2330");
    }

    @Test
    @DisplayName("Should find stock by ID")
    void shouldFindStockById() {
        entityManager.persist(stock);
        entityManager.flush();

        Optional<Stock> foundStock = stockRepository.findById(stock.getId());
        assertThat(foundStock).isPresent();
        assertThat(foundStock.get().getCode()).isEqualTo("2330");
    }

    @Test
    @DisplayName("Should find stock by code")
    void shouldFindStockByCode() {
        entityManager.persist(stock);
        entityManager.flush();

        Optional<Stock> foundStock = stockRepository.findByCode("2330");
        assertThat(foundStock).isPresent();
        assertThat(foundStock.get().getCode()).isEqualTo("2330");
    }

    @Test
    @DisplayName("Should update a stock")
    void shouldUpdateStock() {
        entityManager.persist(stock);
        entityManager.flush();

        Stock foundStock = stockRepository.findById(stock.getId()).get();
        foundStock.setName("聯發科");
        foundStock.setCode("2454");

        Stock updatedStock = stockRepository.save(foundStock);
        assertThat(updatedStock.getName()).isEqualTo("聯發科");
        assertThat(updatedStock.getCode()).isEqualTo("2454");
    }

    @Test
    @DisplayName("Should delete a stock by ID")
    void shouldDeleteStockById() {
        entityManager.persist(stock);
        entityManager.flush();

        stockRepository.deleteById(stock.getId());
        Optional<Stock> deletedStock = stockRepository.findById(stock.getId());
        assertThat(deletedStock).isNotPresent();
    }
}
