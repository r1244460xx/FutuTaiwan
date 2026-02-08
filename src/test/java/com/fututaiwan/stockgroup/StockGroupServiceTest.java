package com.fututaiwan.stockgroup;

import com.fututaiwan.member.Member;
import com.fututaiwan.member.MemberService;
import com.fututaiwan.stock.Stock;
import com.fututaiwan.stock.StockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StockGroup Service Tests")
class StockGroupServiceTest {

    @Mock
    private StockGroupRepository stockGroupRepository;

    @Mock
    private StockService stockService;

    @Mock
    private MemberService memberService;

    @InjectMocks
    private StockGroupService stockGroupService;

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
    @DisplayName("Should return all stock groups")
    void shouldReturnAllStockGroups() {
        when(stockGroupRepository.findAll()).thenReturn(Arrays.asList(stockGroup1, stockGroup2));

        List<StockGroup> stockGroups = stockGroupService.getAllStockGroups();

        assertThat(stockGroups).hasSize(2);
        assertThat(stockGroups).containsExactly(stockGroup1, stockGroup2);
        verify(stockGroupRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return stock group by ID")
    void shouldReturnStockGroupById() {
        when(stockGroupRepository.findById(1L)).thenReturn(Optional.of(stockGroup1));

        Optional<StockGroup> foundGroup = stockGroupService.getStockGroupById(1L);

        assertThat(foundGroup).isPresent();
        assertThat(foundGroup.get().getName()).isEqualTo("My Tech Stocks");
        verify(stockGroupRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return empty optional if stock group not found by ID")
    void shouldReturnEmptyOptionalIfStockGroupNotFoundById() {
        when(stockGroupRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<StockGroup> foundGroup = stockGroupService.getStockGroupById(99L);

        assertThat(foundGroup).isNotPresent();
        verify(stockGroupRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Should return stock group by name")
    void shouldReturnStockGroupByName() {
        when(stockGroupRepository.findByName("My Tech Stocks")).thenReturn(Optional.of(stockGroup1));

        Optional<StockGroup> foundGroup = stockGroupService.getStockGroupByName("My Tech Stocks");

        assertThat(foundGroup).isPresent();
        assertThat(foundGroup.get().getName()).isEqualTo("My Tech Stocks");
        verify(stockGroupRepository, times(1)).findByName("My Tech Stocks");
    }

    @Test
    @DisplayName("Should return stock groups by member ID")
    void shouldReturnStockGroupsByMemberId() {
        when(memberService.getMemberById(1L)).thenReturn(Optional.of(member1));
        when(stockGroupRepository.findByMember_Id(1L)).thenReturn(Arrays.asList(stockGroup1, stockGroup2));

        List<StockGroup> foundGroups = stockGroupService.getStockGroupsByMemberId(1L);

        assertThat(foundGroups).hasSize(2);
        assertThat(foundGroups).containsExactly(stockGroup1, stockGroup2);
        verify(memberService, times(1)).getMemberById(1L);
        verify(stockGroupRepository, times(1)).findByMember_Id(1L);
    }

    @Test
    @DisplayName("Should throw RuntimeException if member not found when getting stock groups by member ID")
    void shouldThrowExceptionIfMemberNotFoundWhenGettingStockGroupsByMemberId() {
        when(memberService.getMemberById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> stockGroupService.getStockGroupsByMemberId(99L));
        verify(memberService, times(1)).getMemberById(99L);
        verify(stockGroupRepository, never()).findByMember_Id(anyLong());
    }

    @Test
    @DisplayName("Should create a new stock group")
    void shouldCreateNewStockGroup() {
        StockGroup newStockGroup = StockGroup.builder()
                .name("New Group")
                .description("New Description")
                .build(); // Member will be set by service
        when(stockGroupRepository.findByName("New Group")).thenReturn(Optional.empty());
        when(memberService.getMemberById(1L)).thenReturn(Optional.of(member1));
        when(stockGroupRepository.save(any(StockGroup.class))).thenReturn(newStockGroup);

        StockGroup createdGroup = stockGroupService.createStockGroup(newStockGroup, 1L);

        assertThat(createdGroup).isNotNull();
        assertThat(createdGroup.getName()).isEqualTo("New Group");
        assertThat(createdGroup.getMember()).isEqualTo(member1);
        verify(stockGroupRepository, times(1)).findByName("New Group");
        verify(memberService, times(1)).getMemberById(1L);
        verify(stockGroupRepository, times(1)).save(any(StockGroup.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if stock group name already exists on create")
    void shouldThrowExceptionIfStockGroupNameExistsOnCreate() {
        StockGroup newStockGroup = StockGroup.builder().name("My Tech Stocks").build();
        when(stockGroupRepository.findByName("My Tech Stocks")).thenReturn(Optional.of(stockGroup1));

        assertThrows(IllegalArgumentException.class, () -> stockGroupService.createStockGroup(newStockGroup, 1L));
        verify(stockGroupRepository, times(1)).findByName("My Tech Stocks");
        verify(memberService, never()).getMemberById(anyLong());
        verify(stockGroupRepository, never()).save(any(StockGroup.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException if member not found on create")
    void shouldThrowExceptionIfMemberNotFoundOnCreate() {
        StockGroup newStockGroup = StockGroup.builder().name("New Group").build();
        when(stockGroupRepository.findByName("New Group")).thenReturn(Optional.empty());
        when(memberService.getMemberById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> stockGroupService.createStockGroup(newStockGroup, 99L));
        verify(stockGroupRepository, times(1)).findByName("New Group");
        verify(memberService, times(1)).getMemberById(99L);
        verify(stockGroupRepository, never()).save(any(StockGroup.class));
    }

    @Test
    @DisplayName("Should update an existing stock group")
    void shouldUpdateExistingStockGroup() {
        when(stockGroupRepository.findById(1L)).thenReturn(Optional.of(stockGroup1));
        when(stockGroupRepository.findByName("Updated Name")).thenReturn(Optional.empty()); // New name is unique
        when(stockGroupRepository.save(any(StockGroup.class))).thenReturn(stockGroup1);

        StockGroup updatedDetails = StockGroup.builder().name("Updated Name").description("Updated Desc").build();
        StockGroup result = stockGroupService.updateStockGroup(1L, updatedDetails);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getDescription()).isEqualTo("Updated Desc");
        verify(stockGroupRepository, times(1)).findById(1L);
        verify(stockGroupRepository, times(1)).findByName("Updated Name");
        verify(stockGroupRepository, times(1)).save(any(StockGroup.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException when updating non-existent stock group")
    void shouldThrowExceptionWhenUpdatingNonExistentStockGroup() {
        when(stockGroupRepository.findById(99L)).thenReturn(Optional.empty());

        StockGroup updatedDetails = StockGroup.builder().name("Non Existent").build();

        assertThrows(RuntimeException.class, () -> stockGroupService.updateStockGroup(99L, updatedDetails));
        verify(stockGroupRepository, times(1)).findById(99L);
        verify(stockGroupRepository, never()).findByName(anyString());
        verify(stockGroupRepository, never()).save(any(StockGroup.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if new name already exists when updating")
    void shouldThrowExceptionIfNewStockGroupNameExistsOnUpdate() {
        StockGroup existingGroupWithNewName = StockGroup.builder().id(3L).name("Existing Other Group").build();
        when(stockGroupRepository.findById(1L)).thenReturn(Optional.of(stockGroup1));
        when(stockGroupRepository.findByName("Existing Other Group")).thenReturn(Optional.of(existingGroupWithNewName)); // New name exists for another group

        StockGroup updatedDetails = StockGroup.builder().name("Existing Other Group").description("Updated Desc").build();

        assertThrows(IllegalArgumentException.class, () -> stockGroupService.updateStockGroup(1L, updatedDetails));
        verify(stockGroupRepository, times(1)).findById(1L);
        verify(stockGroupRepository, times(1)).findByName("Existing Other Group");
        verify(stockGroupRepository, never()).save(any(StockGroup.class));
    }

    @Test
    @DisplayName("Should delete an existing stock group")
    void shouldDeleteExistingStockGroup() {
        when(stockGroupRepository.existsById(1L)).thenReturn(true);
        doNothing().when(stockGroupRepository).deleteById(1L);

        stockGroupService.deleteStockGroup(1L);

        verify(stockGroupRepository, times(1)).existsById(1L);
        verify(stockGroupRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw RuntimeException when deleting non-existent stock group")
    void shouldThrowExceptionWhenDeletingNonExistentStockGroup() {
        when(stockGroupRepository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> stockGroupService.deleteStockGroup(99L));
        verify(stockGroupRepository, times(1)).existsById(99L);
        verify(stockGroupRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should add stock to group")
    void shouldAddStockToGroup() {
        when(stockGroupRepository.findById(1L)).thenReturn(Optional.of(stockGroup1));
        when(stockService.getStockById(102L)).thenReturn(Optional.of(stock2));
        when(stockGroupRepository.save(any(StockGroup.class))).thenReturn(stockGroup1);

        StockGroup result = stockGroupService.addStockToGroup(1L, 102L);

        assertThat(result.getStocks()).contains(stock1, stock2);
        verify(stockGroupRepository, times(1)).findById(1L);
        verify(stockService, times(1)).getStockById(102L);
        verify(stockGroupRepository, times(1)).save(any(StockGroup.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException if stock group not found when adding stock")
    void shouldThrowExceptionIfStockGroupNotFoundWhenAddingStock() {
        when(stockGroupRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> stockGroupService.addStockToGroup(99L, 101L));
        verify(stockGroupRepository, times(1)).findById(99L);
        verify(stockService, never()).getStockById(anyLong());
        verify(stockGroupRepository, never()).save(any(StockGroup.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException if stock not found when adding stock")
    void shouldThrowExceptionIfStockNotFoundWhenAddingStock() {
        when(stockGroupRepository.findById(1L)).thenReturn(Optional.of(stockGroup1));
        when(stockService.getStockById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> stockGroupService.addStockToGroup(1L, 999L));
        verify(stockGroupRepository, times(1)).findById(1L);
        verify(stockService, times(1)).getStockById(999L);
        verify(stockGroupRepository, never()).save(any(StockGroup.class));
    }

    @Test
    @DisplayName("Should remove stock from group")
    void shouldRemoveStockFromGroup() {
        when(stockGroupRepository.findById(1L)).thenReturn(Optional.of(stockGroup1));
        when(stockService.getStockById(101L)).thenReturn(Optional.of(stock1));
        when(stockGroupRepository.save(any(StockGroup.class))).thenReturn(stockGroup1);

        StockGroup result = stockGroupService.removeStockFromGroup(1L, 101L);

        assertThat(result.getStocks()).doesNotContain(stock1);
        verify(stockGroupRepository, times(1)).findById(1L);
        verify(stockService, times(1)).getStockById(101L);
        verify(stockGroupRepository, times(1)).save(any(StockGroup.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException if stock not in group when removing")
    void shouldThrowExceptionIfStockNotInGroupWhenRemoving() {
        stockGroup1.setStocks(new HashSet<>()); // Ensure stock1 is not in the group
        when(stockGroupRepository.findById(1L)).thenReturn(Optional.of(stockGroup1));
        when(stockService.getStockById(101L)).thenReturn(Optional.of(stock1));

        assertThrows(RuntimeException.class, () -> stockGroupService.removeStockFromGroup(1L, 101L));
        verify(stockGroupRepository, times(1)).findById(1L);
        verify(stockService, times(1)).getStockById(101L);
        verify(stockGroupRepository, never()).save(any(StockGroup.class));
    }
}
