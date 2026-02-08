package com.fututaiwan.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
@DisplayName("Member Controller Tests")
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;

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
    @DisplayName("GET /api/members should return all members")
    void getAllMembers_shouldReturnAllMembers() throws Exception {
        when(memberService.getAllMembers()).thenReturn(Arrays.asList(member1, member2));

        mockMvc.perform(get("/api/members")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Member One")))
                .andExpect(jsonPath("$[1].name", is("Member Two")));

        verify(memberService, times(1)).getAllMembers();
    }

    @Test
    @DisplayName("GET /api/members/{id} should return member by ID")
    void getMemberById_shouldReturnMemberById() throws Exception {
        when(memberService.getMemberById(1L)).thenReturn(Optional.of(member1));

        mockMvc.perform(get("/api/members/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Member One")));

        verify(memberService, times(1)).getMemberById(1L);
    }

    @Test
    @DisplayName("GET /api/members/{id} should return 404 if member not found")
    void getMemberById_shouldReturn404IfNotFound() throws Exception {
        when(memberService.getMemberById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/members/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(memberService, times(1)).getMemberById(99L);
    }

    @Test
    @DisplayName("POST /api/members should create a new member")
    void createMember_shouldCreateNewMember() throws Exception {
        Member newMember = Member.builder()
                .name("New Member")
                .phoneNumber("0933333333")
                .nationalIdNumber("A333333333")
                .dateOfBirth(LocalDate.of(1995, 5, 5))
                .email("new@example.com")
                .passwordHash("newhash")
                .gender("Female")
                .address("New Address")
                .isActive(true)
                .role("member")
                .build();
        when(memberService.createMember(any(Member.class))).thenReturn(member1); // Return member1 for simplicity

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMember)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Member One"))); // Expect member1's name

        verify(memberService, times(1)).createMember(any(Member.class));
    }

    @Test
    @DisplayName("PUT /api/members/{id} should update an existing member")
    void updateMember_shouldUpdateExistingMember() throws Exception {
        Member updatedDetails = Member.builder()
                .id(1L)
                .name("Updated Member")
                .phoneNumber("0944444444")
                .nationalIdNumber("A444444444")
                .dateOfBirth(LocalDate.of(1996, 6, 6))
                .email("updated@example.com")
                .passwordHash("updatedhash")
                .gender("Other")
                .address("Updated Address")
                .isActive(false)
                .role("admin")
                .build();
        when(memberService.updateMember(anyLong(), any(Member.class))).thenReturn(updatedDetails);

        mockMvc.perform(put("/api/members/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Member")));

        verify(memberService, times(1)).updateMember(anyLong(), any(Member.class));
    }

    @Test
    @DisplayName("PUT /api/members/{id} should return 404 if member not found")
    void updateMember_shouldReturn404IfNotFound() throws Exception {
        Member updatedDetails = Member.builder().name("Non Existent").build();
        when(memberService.updateMember(anyLong(), any(Member.class))).thenThrow(new RuntimeException("Member not found"));

        mockMvc.perform(put("/api/members/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isNotFound());

        verify(memberService, times(1)).updateMember(anyLong(), any(Member.class));
    }

    @Test
    @DisplayName("DELETE /api/members/{id} should delete a member")
    void deleteMember_shouldDeleteMember() throws Exception {
        doNothing().when(memberService).deleteMember(1L);

        mockMvc.perform(delete("/api/members/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(memberService, times(1)).deleteMember(1L);
    }

    @Test
    @DisplayName("DELETE /api/members/{id} should return 404 if member not found")
    void deleteMember_shouldReturn404IfNotFound() throws Exception {
        doThrow(new RuntimeException("Member not found")).when(memberService).deleteMember(99L);

        mockMvc.perform(delete("/api/members/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(memberService, times(1)).deleteMember(99L);
    }

    @Test
    @DisplayName("GET /api/members/search/email should return member by email")
    void getMemberByEmail_shouldReturnMemberByEmail() throws Exception {
        when(memberService.getMemberByEmail("member1@example.com")).thenReturn(Optional.of(member1));

        mockMvc.perform(get("/api/members/search/email")
                        .param("email", "member1@example.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("member1@example.com")));

        verify(memberService, times(1)).getMemberByEmail("member1@example.com");
    }

    @Test
    @DisplayName("GET /api/members/search/email should return 404 if member not found by email")
    void getMemberByEmail_shouldReturn404IfNotFound() throws Exception {
        when(memberService.getMemberByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/members/search/email")
                        .param("email", "nonexistent@example.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(memberService, times(1)).getMemberByEmail("nonexistent@example.com");
    }

    @Test
    @DisplayName("GET /api/members/search/phone should return member by phone number")
    void getMemberByPhoneNumber_shouldReturnMemberByPhoneNumber() throws Exception {
        when(memberService.getMemberByPhoneNumber("0911111111")).thenReturn(Optional.of(member1));

        mockMvc.perform(get("/api/members/search/phone")
                        .param("phoneNumber", "0911111111")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phoneNumber", is("0911111111")));

        verify(memberService, times(1)).getMemberByPhoneNumber("0911111111");
    }

    @Test
    @DisplayName("GET /api/members/search/nationalId should return member by national ID number")
    void getMemberByNationalIdNumber_shouldReturnMemberByNationalIdNumber() throws Exception {
        when(memberService.getMemberByNationalIdNumber("A111111111")).thenReturn(Optional.of(member1));

        mockMvc.perform(get("/api/members/search/nationalId")
                        .param("nationalIdNumber", "A111111111")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nationalIdNumber", is("A111111111")));

        verify(memberService, times(1)).getMemberByNationalIdNumber("A111111111");
    }
}
