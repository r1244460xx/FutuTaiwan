package com.fututaiwan.stock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Stock Service Tests")
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private StockService stockService;

    private Stock stock1;
    private Stock stock2;

    @BeforeEach
    void setUp() {
        stock1 = Stock.builder().id(1L).code("2330").name("台積電").build();
        stock2 = Stock.builder().id(2L).code("2454").name("聯發科").build();
    }

    @Test
    @DisplayName("Should return all stocks")
    void shouldReturnAllStocks() {
        when(stockRepository.findAll()).thenReturn(Arrays.asList(stock1, stock2));

        List<Stock> stocks = stockService.getAllStocks();

        assertThat(stocks).hasSize(2);
        assertThat(stocks).containsExactly(stock1, stock2);
        verify(stockRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return stock by ID")
    void shouldReturnStockById() {
        when(stockRepository.findById(1L)).thenReturn(Optional.of(stock1));

        Optional<Stock> foundStock = stockService.getStockById(1L);

        assertThat(foundStock).isPresent();
        assertThat(foundStock.get().getCode()).isEqualTo("2330");
        verify(stockRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return empty optional if stock not found by ID")
    void shouldReturnEmptyOptionalIfStockNotFoundById() {
        when(stockRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Stock> foundStock = stockService.getStockById(99L);

        assertThat(foundStock).isNotPresent();
        verify(stockRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Should return stock by code")
    void shouldReturnStockByCode() {
        when(stockRepository.findByCode("2330")).thenReturn(Optional.of(stock1));

        Optional<Stock> foundStock = stockService.getStockByCode("2330");

        assertThat(foundStock).isPresent();
        assertThat(foundStock.get().getCode()).isEqualTo("2330");
        verify(stockRepository, times(1)).findByCode("2330");
    }

    @Test
    @DisplayName("Should create a new stock")
    void shouldCreateNewStock() {
        when(stockRepository.findByCode("1101")).thenReturn(Optional.empty());
        when(stockRepository.save(any(Stock.class))).thenReturn(Stock.builder().id(3L).code("1101").name("台泥").build());

        Stock newStock = Stock.builder().code("1101").name("台泥").build();
        Stock createdStock = stockService.createStock(newStock);

        assertThat(createdStock).isNotNull();
        assertThat(createdStock.getCode()).isEqualTo("1101");
        verify(stockRepository, times(1)).findByCode("1101");
        verify(stockRepository, times(1)).save(newStock);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if stock with code already exists when creating")
    void shouldThrowExceptionIfStockCodeExistsOnCreate() {
        when(stockRepository.findByCode("2330")).thenReturn(Optional.of(stock1));

        Stock newStock = Stock.builder().code("2330").name("重複台積電").build();

        assertThrows(IllegalArgumentException.class, () -> stockService.createStock(newStock));
        verify(stockRepository, times(1)).findByCode("2330");
        verify(stockRepository, never()).save(any(Stock.class));
    }

    @Test
    @DisplayName("Should update an existing stock")
    void shouldUpdateExistingStock() {
        when(stockRepository.findById(1L)).thenReturn(Optional.of(stock1));
        when(stockRepository.findByCode("2454")).thenReturn(Optional.empty()); // New code is unique
        when(stockRepository.save(any(Stock.class))).thenReturn(stock1);

        Stock updatedDetails = Stock.builder().code("2454").name("聯發科新").build();
        Stock result = stockService.updateStock(1L, updatedDetails);

        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("2454");
        assertThat(result.getName()).isEqualTo("聯發科新");
        verify(stockRepository, times(1)).findById(1L);
        verify(stockRepository, times(1)).findByCode("2454");
        verify(stockRepository, times(1)).save(any(Stock.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException when updating non-existent stock")
    void shouldThrowExceptionWhenUpdatingNonExistentStock() {
        when(stockRepository.findById(99L)).thenReturn(Optional.empty());

        Stock updatedDetails = Stock.builder().code("9999").name("不存在").build();

        assertThrows(RuntimeException.class, () -> stockService.updateStock(99L, updatedDetails));
        verify(stockRepository, times(1)).findById(99L);
        verify(stockRepository, never()).findByCode(anyString());
        verify(stockRepository, never()).save(any(Stock.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if new code already exists when updating")
    void shouldThrowExceptionIfNewStockCodeExistsOnUpdate() {
        when(stockRepository.findById(1L)).thenReturn(Optional.of(stock1));
        when(stockRepository.findByCode("2454")).thenReturn(Optional.of(stock2)); // New code exists for another stock

        Stock updatedDetails = Stock.builder().code("2454").name("重複聯發科").build();

        assertThrows(IllegalArgumentException.class, () -> stockService.updateStock(1L, updatedDetails));
        verify(stockRepository, times(1)).findById(1L);
        verify(stockRepository, times(1)).findByCode("2454");
        verify(stockRepository, never()).save(any(Stock.class));
    }

    @Test
    @DisplayName("Should delete an existing stock")
    void shouldDeleteExistingStock() {
        when(stockRepository.existsById(1L)).thenReturn(true);
        doNothing().when(stockRepository).deleteById(1L);

        stockService.deleteStock(1L);

        verify(stockRepository, times(1)).existsById(1L);
        verify(stockRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw RuntimeException when deleting non-existent stock")
    void shouldThrowExceptionWhenDeletingNonExistentStock() {
        when(stockRepository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> stockService.deleteStock(99L));
        verify(stockRepository, times(1)).existsById(99L);
        verify(stockRepository, never()).deleteById(anyLong());
    }
}
