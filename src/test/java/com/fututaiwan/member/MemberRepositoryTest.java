package com.fututaiwan.member;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Member Repository Tests")
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Member member;

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
        // Note: registrationDate and lastLoginDate are handled by @CreationTimestamp or manually set if needed
    }

    @Test
    @DisplayName("Should save a member")
    void shouldSaveMember() {
        Member savedMember = memberRepository.save(member);
        assertThat(savedMember).isNotNull();
        assertThat(savedMember.getId()).isNotNull();
        assertThat(savedMember.getName()).isEqualTo("Test Member");
    }

    @Test
    @DisplayName("Should find member by ID")
    void shouldFindMemberById() {
        entityManager.persist(member);
        entityManager.flush();

        Optional<Member> foundMember = memberRepository.findById(member.getId());
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getName()).isEqualTo("Test Member");
    }

    @Test
    @DisplayName("Should find member by email")
    void shouldFindMemberByEmail() {
        entityManager.persist(member);
        entityManager.flush();

        Optional<Member> foundMember = memberRepository.findByEmail("test@example.com");
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should find member by phone number")
    void shouldFindMemberByPhoneNumber() {
        entityManager.persist(member);
        entityManager.flush();

        Optional<Member> foundMember = memberRepository.findByPhoneNumber("0912345678");
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getPhoneNumber()).isEqualTo("0912345678");
    }

    @Test
    @DisplayName("Should find member by national ID number")
    void shouldFindMemberByNationalIdNumber() {
        entityManager.persist(member);
        entityManager.flush();

        Optional<Member> foundMember = memberRepository.findByNationalIdNumber("A123456789");
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getNationalIdNumber()).isEqualTo("A123456789");
    }

    @Test
    @DisplayName("Should update a member")
    void shouldUpdateMember() {
        entityManager.persist(member);
        entityManager.flush();

        Member foundMember = memberRepository.findById(member.getId()).get();
        foundMember.setName("Updated Name");
        foundMember.setEmail("updated@example.com");

        Member updatedMember = memberRepository.save(foundMember);
        assertThat(updatedMember.getName()).isEqualTo("Updated Name");
        assertThat(updatedMember.getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    @DisplayName("Should delete a member by ID")
    void shouldDeleteMemberById() {
        entityManager.persist(member);
        entityManager.flush();

        memberRepository.deleteById(member.getId());
        Optional<Member> deletedMember = memberRepository.findById(member.getId());
        assertThat(deletedMember).isNotPresent();
    }
}
