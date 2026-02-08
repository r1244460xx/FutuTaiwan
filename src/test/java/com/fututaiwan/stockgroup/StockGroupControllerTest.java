package com.fututaiwan.stockgroup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fututaiwan.member.Member;
import com.fututaiwan.stock.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StockGroupController.class)
@DisplayName("StockGroup Controller Tests")
class StockGroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StockGroupService stockGroupService;

    @Autowired
    private ObjectMapper objectMapper;

    private Member member1;
    private Stock stock1;
    private Stock stock2;
    private StockGroup stockGroup1;
    private StockGroup stockGroup2;

    @BeforeEach
    void setUp() {
        member1 = Member.builder().id(1L).name("Member One").email("member1@example.com").phoneNumber("0911111111").nationalIdNumber("A111111111").passwordHash("hash").build();
        stock1 = Stock.builder().id(101L).code("2330").name("台積電").build();
        stock2 = Stock.builder().id(102L).code("2454").name("聯發科").build();

        stockGroup1 = StockGroup.builder()
                .id(1L)
                .name("My Tech Stocks")
                .description("My favorite tech stocks")
                .member(member1)
                .stocks(new HashSet<>(Set.of(stock1)))
                .build();

        stockGroup2 = StockGroup.builder()
                .id(2L)
                .name("My Green Stocks")
                .description("My favorite green stocks")
                .member(member1)
                .stocks(new HashSet<>())
                .build();
    }

    @Test
    @DisplayName("GET /api/stock-groups should return all stock groups")
    void getAllStockGroups_shouldReturnAllStockGroups() throws Exception {
        when(stockGroupService.getAllStockGroups()).thenReturn(Arrays.asList(stockGroup1, stockGroup2));

        mockMvc.perform(get("/api/stock-groups")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("My Tech Stocks")))
                .andExpect(jsonPath("$[1].name", is("My Green Stocks")));

        verify(stockGroupService, times(1)).getAllStockGroups();
    }

    @Test
    @DisplayName("GET /api/stock-groups/{id} should return stock group by ID")
    void getStockGroupById_shouldReturnStockGroupById() throws Exception {
        when(stockGroupService.getStockGroupById(1L)).thenReturn(Optional.of(stockGroup1));

        mockMvc.perform(get("/api/stock-groups/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("My Tech Stocks")));

        verify(stockGroupService, times(1)).getStockGroupById(1L);
    }

    @Test
    @DisplayName("GET /api/stock-groups/{id} should return 404 if stock group not found")
    void getStockGroupById_shouldReturn404IfNotFound() throws Exception {
        when(stockGroupService.getStockGroupById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/stock-groups/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(stockGroupService, times(1)).getStockGroupById(99L);
    }

    @Test
    @DisplayName("POST /api/stock-groups/member/{memberId} should create a new stock group")
    void createStockGroup_shouldCreateNewStockGroup() throws Exception {
        StockGroup newStockGroup = StockGroup.builder()
                .name("New Group")
                .description("New Description")
                .build();
        when(stockGroupService.createStockGroup(any(StockGroup.class), anyLong())).thenReturn(stockGroup1);

        mockMvc.perform(post("/api/stock-groups/member/{memberId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newStockGroup)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("My Tech Stocks")));

        verify(stockGroupService, times(1)).createStockGroup(any(StockGroup.class), eq(1L));
    }

    @Test
    @DisplayName("POST /api/stock-groups/member/{memberId} should return 409 if name conflict")
    void createStockGroup_shouldReturn409IfNameConflict() throws Exception {
        StockGroup newStockGroup = StockGroup.builder().name("My Tech Stocks").build();
        when(stockGroupService.createStockGroup(any(StockGroup.class), anyLong())).thenThrow(new IllegalArgumentException("Stock group with name 'My Tech Stocks' already exists."));

        mockMvc.perform(post("/api/stock-groups/member/{memberId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newStockGroup)))
                .andExpect(status().isConflict());

        verify(stockGroupService, times(1)).createStockGroup(any(StockGroup.class), eq(1L));
    }

    @Test
    @DisplayName("POST /api/stock-groups/member/{memberId} should return 404 if member not found")
    void createStockGroup_shouldReturn404IfMemberNotFound() throws Exception {
        StockGroup newStockGroup = StockGroup.builder().name("New Group").build();
        when(stockGroupService.createStockGroup(any(StockGroup.class), anyLong())).thenThrow(new RuntimeException("Member not found with id 99"));

        mockMvc.perform(post("/api/stock-groups/member/{memberId}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newStockGroup)))
                .andExpect(status().isNotFound());

        verify(stockGroupService, times(1)).createStockGroup(any(StockGroup.class), eq(99L));
    }

    @Test
    @DisplayName("PUT /api/stock-groups/{id} should update an existing stock group")
    void updateStockGroup_shouldUpdateExistingStockGroup() throws Exception {
        StockGroup updatedDetails = StockGroup.builder().id(1L).name("Updated Group").description("Updated Desc").build();
        when(stockGroupService.updateStockGroup(anyLong(), any(StockGroup.class))).thenReturn(updatedDetails);

        mockMvc.perform(put("/api/stock-groups/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Group")));

        verify(stockGroupService, times(1)).updateStockGroup(anyLong(), any(StockGroup.class));
    }

    @Test
    @DisplayName("PUT /api/stock-groups/{id} should return 404 if stock group not found")
    void updateStockGroup_shouldReturn404IfNotFound() throws Exception {
        StockGroup updatedDetails = StockGroup.builder().name("Non Existent").build();
        when(stockGroupService.updateStockGroup(anyLong(), any(StockGroup.class))).thenThrow(new RuntimeException("Stock group not found"));

        mockMvc.perform(put("/api/stock-groups/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isNotFound());

        verify(stockGroupService, times(1)).updateStockGroup(anyLong(), any(StockGroup.class));
    }

    @Test
    @DisplayName("DELETE /api/stock-groups/{id} should delete a stock group")
    void deleteStockGroup_shouldDeleteStockGroup() throws Exception {
        doNothing().when(stockGroupService).deleteStockGroup(1L);

        mockMvc.perform(delete("/api/stock-groups/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(stockGroupService, times(1)).deleteStockGroup(1L);
    }

    @Test
    @DisplayName("DELETE /api/stock-groups/{id} should return 404 if stock group not found")
    void deleteStockGroup_shouldReturn404IfNotFound() throws Exception {
        doThrow(new RuntimeException("Stock group not found")).when(stockGroupService).deleteStockGroup(99L);

        mockMvc.perform(delete("/api/stock-groups/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(stockGroupService, times(1)).deleteStockGroup(99L);
    }

    @Test
    @DisplayName("GET /api/stock-groups/search/name should return stock group by name")
    void getStockGroupByName_shouldReturnStockGroupByName() throws Exception {
        when(stockGroupService.getStockGroupByName("My Tech Stocks")).thenReturn(Optional.of(stockGroup1));

        mockMvc.perform(get("/api/stock-groups/search/name")
                        .param("name", "My Tech Stocks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("My Tech Stocks")));

        verify(stockGroupService, times(1)).getStockGroupByName("My Tech Stocks");
    }

    @Test
    @DisplayName("GET /api/stock-groups/search/name should return 404 if stock group not found by name")
    void getStockGroupByName_shouldReturn404IfNotFound() throws Exception {
        when(stockGroupService.getStockGroupByName("Non Existent")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/stock-groups/search/name")
                        .param("name", "Non Existent")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(stockGroupService, times(1)).getStockGroupByName("Non Existent");
    }

    @Test
    @DisplayName("POST /api/stock-groups/{stockGroupId}/stocks/{stockId} should add stock to group")
    void addStockToGroup_shouldAddStockToGroup() throws Exception {
        StockGroup updatedGroup = StockGroup.builder()
                .id(1L)
                .name("My Tech Stocks")
                .description("My favorite tech stocks")
                .member(member1)
                .stocks(new HashSet<>(Set.of(stock1, stock2)))
                .build();
        when(stockGroupService.addStockToGroup(1L, 102L)).thenReturn(updatedGroup);

        mockMvc.perform(post("/api/stock-groups/{stockGroupId}/stocks/{stockId}", 1L, 102L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stocks", hasSize(2)));

        verify(stockGroupService, times(1)).addStockToGroup(1L, 102L);
    }

    @Test
    @DisplayName("POST /api/stock-groups/{stockGroupId}/stocks/{stockId} should return 404 if group or stock not found")
    void addStockToGroup_shouldReturn404IfGroupOrStockNotFound() throws Exception {
        when(stockGroupService.addStockToGroup(anyLong(), anyLong())).thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(post("/api/stock-groups/{stockGroupId}/stocks/{stockId}", 1L, 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(stockGroupService, times(1)).addStockToGroup(1L, 999L);
    }

    @Test
    @DisplayName("DELETE /api/stock-groups/{stockGroupId}/stocks/{stockId} should remove stock from group")
    void removeStockFromGroup_shouldRemoveStockFromGroup() throws Exception {
        StockGroup updatedGroup = StockGroup.builder()
                .id(1L)
                .name("My Tech Stocks")
                .description("My favorite tech stocks")
                .member(member1)
                .stocks(new HashSet<>()) // Empty set after removal
                .build();
        when(stockGroupService.removeStockFromGroup(1L, 101L)).thenReturn(updatedGroup);

        mockMvc.perform(delete("/api/stock-groups/{stockGroupId}/stocks/{stockId}", 1L, 101L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stocks", hasSize(0)));

        verify(stockGroupService, times(1)).removeStockFromGroup(1L, 101L);
    }

    @Test
    @DisplayName("DELETE /api/stock-groups/{stockGroupId}/stocks/{stockId} should return 404 if group or stock not found")
    void removeStockFromGroup_shouldReturn404IfGroupOrStockNotFound() throws Exception {
        when(stockGroupService.removeStockFromGroup(anyLong(), anyLong())).thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(delete("/api/stock-groups/{stockGroupId}/stocks/{stockId}", 1L, 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(stockGroupService, times(1)).removeStockFromGroup(1L, 999L);
    }

    @Test
    @DisplayName("GET /api/stock-groups/{stockGroupId}/stocks should return stocks in group")
    void getStocksInGroup_shouldReturnStocksInGroup() throws Exception {
        stockGroup1.setStocks(new HashSet<>(Arrays.asList(stock1, stock2))); // Ensure multiple stocks
        when(stockGroupService.getStockGroupById(1L)).thenReturn(Optional.of(stockGroup1));

        mockMvc.perform(get("/api/stock-groups/{stockGroupId}/stocks", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].code", is("2330")))
                .andExpect(jsonPath("$[1].code", is("2454"))); // Order might vary for Set

        verify(stockGroupService, times(1)).getStockGroupById(1L);
    }

    @Test
    @DisplayName("GET /api/stock-groups/{stockGroupId}/stocks should return 404 if group not found")
    void getStocksInGroup_shouldReturn404IfGroupNotFound() throws Exception {
        when(stockGroupService.getStockGroupById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/stock-groups/{stockGroupId}/stocks", 99L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(stockGroupService, times(1)).getStockGroupById(99L);
    }

    @Test
    @DisplayName("GET /api/stock-groups/member/{memberId} should return stock groups by member ID")
    void getStockGroupsByMemberId_shouldReturnStockGroupsByMemberId() throws Exception {
        List<StockGroup> memberGroups = Arrays.asList(stockGroup1, stockGroup2);
        when(stockGroupService.getStockGroupsByMemberId(1L)).thenReturn(memberGroups);

        mockMvc.perform(get("/api/stock-groups/member/{memberId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("My Tech Stocks")))
                .andExpect(jsonPath("$[1].name", is("My Green Stocks")));

        verify(stockGroupService, times(1)).getStockGroupsByMemberId(1L);
    }

    @Test
    @DisplayName("GET /api/stock-groups/member/{memberId} should return 404 if no groups found for member")
    void getStockGroupsByMemberId_shouldReturn404IfNoGroupsFound() throws Exception {
        when(stockGroupService.getStockGroupsByMemberId(99L)).thenReturn(List.of()); // Return empty list

        mockMvc.perform(get("/api/stock-groups/member/{memberId}", 99L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(stockGroupService, times(1)).getStockGroupsByMemberId(99L);
    }
}
