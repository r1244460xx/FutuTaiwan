package com.fututaiwan.member;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Member Service Tests")
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    private Member member1;
    private Member member2;

    @BeforeEach
    void setUp() {
        member1 = Member.builder()
                .id(1L)
                .name("Member One")
                .phoneNumber("0911111111")
                .nationalIdNumber("A111111111")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .email("member1@example.com")
                .passwordHash("hash1")
                .gender("Male")
                .address("Address 1")
                .isActive(true)
                .role("member")
                .build();

        member2 = Member.builder()
                .id(2L)
                .name("Member Two")
                .phoneNumber("0922222222")
                .nationalIdNumber("A222222222")
                .dateOfBirth(LocalDate.of(1991, 2, 2))
                .email("member2@example.com")
                .passwordHash("hash2")
                .gender("Female")
                .address("Address 2")
                .isActive(true)
                .role("member")
                .build();
    }

    @Test
    @DisplayName("Should return all members")
    void shouldReturnAllMembers() {
        when(memberRepository.findAll()).thenReturn(Arrays.asList(member1, member2));

        List<Member> members = memberService.getAllMembers();

        assertThat(members).hasSize(2);
        assertThat(members).containsExactly(member1, member2);
        verify(memberRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return member by ID")
    void shouldReturnMemberById() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member1));

        Optional<Member> foundMember = memberService.getMemberById(1L);

        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getName()).isEqualTo("Member One");
        verify(memberRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return empty optional if member not found by ID")
    void shouldReturnEmptyOptionalIfMemberNotFoundById() {
        when(memberRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Member> foundMember = memberService.getMemberById(99L);

        assertThat(foundMember).isNotPresent();
        verify(memberRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Should return member by email")
    void shouldReturnMemberByEmail() {
        when(memberRepository.findByEmail("member1@example.com")).thenReturn(Optional.of(member1));

        Optional<Member> foundMember = memberService.getMemberByEmail("member1@example.com");

        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getEmail()).isEqualTo("member1@example.com");
        verify(memberRepository, times(1)).findByEmail("member1@example.com");
    }

    @Test
    @DisplayName("Should return member by phone number")
    void shouldReturnMemberByPhoneNumber() {
        when(memberRepository.findByPhoneNumber("0911111111")).thenReturn(Optional.of(member1));

        Optional<Member> foundMember = memberService.getMemberByPhoneNumber("0911111111");

        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getPhoneNumber()).isEqualTo("0911111111");
        verify(memberRepository, times(1)).findByPhoneNumber("0911111111");
    }

    @Test
    @DisplayName("Should return member by national ID number")
    void shouldReturnMemberByNationalIdNumber() {
        when(memberRepository.findByNationalIdNumber("A111111111")).thenReturn(Optional.of(member1));

        Optional<Member> foundMember = memberService.getMemberByNationalIdNumber("A111111111");

        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getNationalIdNumber()).isEqualTo("A111111111");
        verify(memberRepository, times(1)).findByNationalIdNumber("A111111111");
    }

    @Test
    @DisplayName("Should create a new member")
    void shouldCreateNewMember() {
        when(memberRepository.save(any(Member.class))).thenReturn(member1);

        Member createdMember = memberService.createMember(member1);

        assertThat(createdMember).isNotNull();
        assertThat(createdMember.getName()).isEqualTo("Member One");
        verify(memberRepository, times(1)).save(member1);
    }

    @Test
    @DisplayName("Should update an existing member")
    void shouldUpdateExistingMember() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member1));
        when(memberRepository.save(any(Member.class))).thenReturn(member1);

        Member updatedDetails = Member.builder()
                .name("Updated Name")
                .phoneNumber("0933333333")
                .nationalIdNumber("A333333333")
                .dateOfBirth(LocalDate.of(1992, 3, 3))
                .email("updated@example.com")
                .passwordHash("newhash")
                .gender("Other")
                .address("Updated Address")
                .isActive(false)
                .role("admin")
                .build();

        Member result = memberService.updateMember(1L, updatedDetails);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getEmail()).isEqualTo("updated@example.com");
        assertThat(result.getPhoneNumber()).isEqualTo("0933333333");
        verify(memberRepository, times(1)).findById(1L);
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException when updating non-existent member")
    void shouldThrowExceptionWhenUpdatingNonExistentMember() {
        when(memberRepository.findById(99L)).thenReturn(Optional.empty());

        Member updatedDetails = Member.builder().name("Non Existent").build();

        assertThrows(RuntimeException.class, () -> memberService.updateMember(99L, updatedDetails));
        verify(memberRepository, times(1)).findById(99L);
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    @DisplayName("Should delete an existing member")
    void shouldDeleteExistingMember() {
        when(memberRepository.existsById(1L)).thenReturn(true);
        doNothing().when(memberRepository).deleteById(1L);

        memberService.deleteMember(1L);

        verify(memberRepository, times(1)).existsById(1L);
        verify(memberRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw RuntimeException when deleting non-existent member")
    void shouldThrowExceptionWhenDeletingNonExistentMember() {
        when(memberRepository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> memberService.deleteMember(99L));
        verify(memberRepository, times(1)).existsById(99L);
        verify(memberRepository, never()).deleteById(anyLong());
    }
}
