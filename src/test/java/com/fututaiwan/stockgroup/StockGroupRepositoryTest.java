package com.fututaiwan.stockgroup;

import com.fututaiwan.member.Member;
import com.fututaiwan.member.MemberRepository;
import com.fututaiwan.stock.Stock;
import com.fututaiwan.stock.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("StockGroup Repository Tests")
class StockGroupRepositoryTest {

    @Autowired
    private StockGroupRepository stockGroupRepository;

    @Autowired
    private MemberRepository memberRepository; // For member dependency

    @Autowired
    private StockRepository stockRepository; // For stock dependency

    @Autowired
    private TestEntityManager entityManager;

    private Member member;
    private Stock stock1;
    private Stock stock2;
    private StockGroup stockGroup;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .name("Test Member")
                .phoneNumber("0912345678")
                .nationalIdNumber("A123456789")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .email("test@example.com")
                .passwordHash("hashedpassword")
                .gender("Male")
                .address("Test Address")
                .isActive(true)
                .role("member")
                .build();
        entityManager.persist(member);

        stock1 = Stock.builder().code("2330").name("台積電").build();
        stock2 = Stock.builder().code("2454").name("聯發科").build();
        entityManager.persist(stock1);
        entityManager.persist(stock2);

        stockGroup = StockGroup.builder()
                .name("My First Group")
                .description("Description for first group")
                .member(member)
                .build();
        stockGroup.getStocks().add(stock1); // Add stock to group
        entityManager.persist(stockGroup);
        entityManager.flush(); // Ensure all entities are persisted and IDs are generated
        entityManager.clear(); // Clear persistence context to ensure fresh load
    }

    @Test
    @DisplayName("Should save a stock group")
    void shouldSaveStockGroup() {
        StockGroup newStockGroup = StockGroup.builder()
                .name("New Group")
                .description("New Description")
                .member(member)
                .build();
        StockGroup savedStockGroup = stockGroupRepository.save(newStockGroup);

        assertThat(savedStockGroup).isNotNull();
        assertThat(savedStockGroup.getId()).isNotNull();
        assertThat(savedStockGroup.getName()).isEqualTo("New Group");
        assertThat(savedStockGroup.getMember().getId()).isEqualTo(member.getId());
    }

    @Test
    @DisplayName("Should find stock group by ID")
    void shouldFindStockGroupById() {
        Optional<StockGroup> foundStockGroup = stockGroupRepository.findById(stockGroup.getId());
        assertThat(foundStockGroup).isPresent();
        assertThat(foundStockGroup.get().getName()).isEqualTo("My First Group");
        assertThat(foundStockGroup.get().getMember().getId()).isEqualTo(member.getId());
        assertThat(foundStockGroup.get().getStocks()).contains(stock1);
    }

    @Test
    @DisplayName("Should find stock group by name")
    void shouldFindStockGroupByName() {
        Optional<StockGroup> foundStockGroup = stockGroupRepository.findByName("My First Group");
        assertThat(foundStockGroup).isPresent();
        assertThat(foundStockGroup.get().getName()).isEqualTo("My First Group");
    }

    @Test
    @DisplayName("Should find stock groups by member ID")
    void shouldFindStockGroupsByMemberId() {
        // Create another stock group for the same member
        StockGroup stockGroup2 = StockGroup.builder()
                .name("My Second Group")
                .description("Another group")
                .member(member)
                .build();
        entityManager.persist(stockGroup2);
        entityManager.flush();
        entityManager.clear();

        List<StockGroup> foundGroups = stockGroupRepository.findByMember_Id(member.getId());
        assertThat(foundGroups).hasSize(2);
        assertThat(foundGroups).extracting(StockGroup::getName).containsExactlyInAnyOrder("My First Group", "My Second Group");
    }

    @Test
    @DisplayName("Should update a stock group")
    void shouldUpdateStockGroup() {
        StockGroup foundStockGroup = stockGroupRepository.findById(stockGroup.getId()).get();
        foundStockGroup.setName("Updated Group Name");
        foundStockGroup.setDescription("Updated Description");

        StockGroup updatedStockGroup = stockGroupRepository.save(foundStockGroup);
        assertThat(updatedStockGroup.getName()).isEqualTo("Updated Group Name");
        assertThat(updatedStockGroup.getDescription()).isEqualTo("Updated Description");
    }

    @Test
    @DisplayName("Should add a stock to a stock group")
    void shouldAddStockToStockGroup() {
        StockGroup foundStockGroup = stockGroupRepository.findById(stockGroup.getId()).get();
        assertThat(foundStockGroup.getStocks()).hasSize(1);
        assertThat(foundStockGroup.getStocks()).contains(stock1);

        foundStockGroup.getStocks().add(stock2);
        StockGroup updatedStockGroup = stockGroupRepository.save(foundStockGroup);
        entityManager.flush();
        entityManager.clear(); // Clear persistence context to ensure fresh load

        Optional<StockGroup> reloadedGroup = stockGroupRepository.findById(stockGroup.getId());
        assertThat(reloadedGroup).isPresent();
        Set<Stock> stocksInGroup = reloadedGroup.get().getStocks();
        assertThat(stocksInGroup).hasSize(2);
        assertThat(stocksInGroup).containsExactlyInAnyOrder(stock1, stock2);
    }

    @Test
    @DisplayName("Should remove a stock from a stock group")
    void shouldRemoveStockFromStockGroup() {
        // Add stock2 first to ensure it's in the group
        StockGroup initialGroup = stockGroupRepository.findById(stockGroup.getId()).get();
        initialGroup.getStocks().add(stock2);
        stockGroupRepository.save(initialGroup);
        entityManager.flush();
        entityManager.clear();

        StockGroup foundStockGroup = stockGroupRepository.findById(stockGroup.getId()).get();
        assertThat(foundStockGroup.getStocks()).hasSize(2);
        assertThat(foundStockGroup.getStocks()).contains(stock1, stock2);

        foundStockGroup.getStocks().remove(stock1);
        StockGroup updatedStockGroup = stockGroupRepository.save(foundStockGroup);
        entityManager.flush();
        entityManager.clear();

        Optional<StockGroup> reloadedGroup = stockGroupRepository.findById(stockGroup.getId());
        assertThat(reloadedGroup).isPresent();
        Set<Stock> stocksInGroup = reloadedGroup.get().getStocks();
        assertThat(stocksInGroup).hasSize(1);
        assertThat(stocksInGroup).containsExactly(stock2);
    }

    @Test
    @DisplayName("Should delete a stock group by ID")
    void shouldDeleteStockGroupById() {
        stockGroupRepository.deleteById(stockGroup.getId());
        Optional<StockGroup> deletedStockGroup = stockGroupRepository.findById(stockGroup.getId());
        assertThat(deletedStockGroup).isNotPresent();
    }
}
