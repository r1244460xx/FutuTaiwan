package com.fututaiwan.stock;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StockController.class)
@DisplayName("Stock Controller Tests")
class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StockService stockService;

    @Autowired
    private ObjectMapper objectMapper;

    private Stock stock1;
    private Stock stock2;

    @BeforeEach
    void setUp() {
        stock1 = Stock.builder().id(1L).code("2330").name("台積電").build();
        stock2 = Stock.builder().id(2L).code("2454").name("聯發科").build();
    }

    @Test
    @DisplayName("GET /api/stocks should return all stocks")
    void getAllStocks_shouldReturnAllStocks() throws Exception {
        when(stockService.getAllStocks()).thenReturn(Arrays.asList(stock1, stock2));

        mockMvc.perform(get("/api/stocks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].code", is("2330")))
                .andExpect(jsonPath("$[1].code", is("2454")));

        verify(stockService, times(1)).getAllStocks();
    }

    @Test
    @DisplayName("GET /api/stocks/{id} should return stock by ID")
    void getStockById_shouldReturnStockById() throws Exception {
        when(stockService.getStockById(1L)).thenReturn(Optional.of(stock1));

        mockMvc.perform(get("/api/stocks/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.code", is("2330")));

        verify(stockService, times(1)).getStockById(1L);
    }

    @Test
    @DisplayName("GET /api/stocks/{id} should return 404 if stock not found")
    void getStockById_shouldReturn404IfNotFound() throws Exception {
        when(stockService.getStockById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/stocks/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(stockService, times(1)).getStockById(99L);
    }

    @Test
    @DisplayName("POST /api/stocks should create a new stock")
    void createStock_shouldCreateNewStock() throws Exception {
        Stock newStock = Stock.builder().code("1101").name("台泥").build();
        when(stockService.createStock(any(Stock.class))).thenReturn(newStock);

        mockMvc.perform(post("/api/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newStock)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code", is("1101")));

        verify(stockService, times(1)).createStock(any(Stock.class));
    }

    @Test
    @DisplayName("POST /api/stocks should return 409 if stock code already exists")
    void createStock_shouldReturn409IfCodeExists() throws Exception {
        Stock newStock = Stock.builder().code("2330").name("重複台積電").build();
        when(stockService.createStock(any(Stock.class))).thenThrow(new IllegalArgumentException("Stock with code '2330' already exists."));

        mockMvc.perform(post("/api/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newStock)))
                .andExpect(status().isConflict());

        verify(stockService, times(1)).createStock(any(Stock.class));
    }

    @Test
    @DisplayName("PUT /api/stocks/{id} should update an existing stock")
    void updateStock_shouldUpdateExistingStock() throws Exception {
        Stock updatedDetails = Stock.builder().id(1L).code("2454").name("聯發科新").build();
        when(stockService.updateStock(anyLong(), any(Stock.class))).thenReturn(updatedDetails);

        mockMvc.perform(put("/api/stocks/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.code", is("2454")));

        verify(stockService, times(1)).updateStock(anyLong(), any(Stock.class));
    }

    @Test
    @DisplayName("PUT /api/stocks/{id} should return 404 if stock not found")
    void updateStock_shouldReturn404IfNotFound() throws Exception {
        Stock updatedDetails = Stock.builder().code("9999").name("不存在").build();
        when(stockService.updateStock(anyLong(), any(Stock.class))).thenThrow(new RuntimeException("Stock not found"));

        mockMvc.perform(put("/api/stocks/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isNotFound());

        verify(stockService, times(1)).updateStock(anyLong(), any(Stock.class));
    }

    @Test
    @DisplayName("DELETE /api/stocks/{id} should delete a stock")
    void deleteStock_shouldDeleteStock() throws Exception {
        doNothing().when(stockService).deleteStock(1L);

        mockMvc.perform(delete("/api/stocks/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(stockService, times(1)).deleteStock(1L);
    }

    @Test
    @DisplayName("DELETE /api/stocks/{id} should return 404 if stock not found")
    void deleteStock_shouldReturn404IfNotFound() throws Exception {
        doThrow(new RuntimeException("Stock not found")).when(stockService).deleteStock(99L);

        mockMvc.perform(delete("/api/stocks/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(stockService, times(1)).deleteStock(99L);
    }

    @Test
    @DisplayName("GET /api/stocks/search/code should return stock by code")
    void getStockByCode_shouldReturnStockByCode() throws Exception {
        when(stockService.getStockByCode("2330")).thenReturn(Optional.of(stock1));

        mockMvc.perform(get("/api/stocks/search/code")
                        .param("code", "2330")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("2330")));

        verify(stockService, times(1)).getStockByCode("2330");
    }

    @Test
    @DisplayName("GET /api/stocks/search/code should return 404 if stock not found by code")
    void getStockByCode_shouldReturn404IfNotFound() throws Exception {
        when(stockService.getStockByCode("9999")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/stocks/search/code")
                        .param("code", "9999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(stockService, times(1)).getStockByCode("9999");
    }
}
