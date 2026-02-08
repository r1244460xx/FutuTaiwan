package com.fututaiwan.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Optional<Member> getMemberById(Long id) {
        return memberRepository.findById(id);
    }

    public Optional<Member> getMemberByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public Optional<Member> getMemberByPhoneNumber(String phoneNumber) {
        return memberRepository.findByPhoneNumber(phoneNumber);
    }

    public Optional<Member> getMemberByNationalIdNumber(String nationalIdNumber) {
        return memberRepository.findByNationalIdNumber(nationalIdNumber);
    }

    @Transactional
    public Member createMember(Member member) {
        // 在這裡可以添加業務邏輯，例如檢查 email, phone number, national ID number 是否重複
        // 為了簡化，目前直接保存
        return memberRepository.save(member);
    }

    @Transactional
    public Member updateMember(Long id, Member updatedMember) {
        return memberRepository.findById(id).map(member -> {
            member.setName(updatedMember.getName());
            member.setPhoneNumber(updatedMember.getPhoneNumber());
            member.setNationalIdNumber(updatedMember.getNationalIdNumber());
            member.setDateOfBirth(updatedMember.getDateOfBirth());
            member.setEmail(updatedMember.getEmail());
            // 密碼通常不會直接更新，需要單獨的密碼重設流程
            // member.setPasswordHash(updatedMember.getPasswordHash());
            member.setGender(updatedMember.getGender());
            member.setAddress(updatedMember.getAddress());
            member.setLastLoginDate(updatedMember.getLastLoginDate());
            member.setIsActive(updatedMember.getIsActive());
            member.setRole(updatedMember.getRole());
            return memberRepository.save(member);
        }).orElseThrow(() -> new RuntimeException("Member not found with id " + id));
    }

    @Transactional
    public void deleteMember(Long id) {
        if (memberRepository.existsById(id)) {
            memberRepository.deleteById(id);
        } else {
            throw new RuntimeException("Member not found with id " + id);
        }
    }
}
