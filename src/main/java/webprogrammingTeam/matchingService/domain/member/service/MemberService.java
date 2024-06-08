package webprogrammingTeam.matchingService.domain.member.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import webprogrammingTeam.matchingService.domain.member.dto.request.MemberCreateRequest;
import webprogrammingTeam.matchingService.domain.member.entity.Member;
import webprogrammingTeam.matchingService.domain.member.entity.Role;
import webprogrammingTeam.matchingService.domain.member.repository.MemberRepository;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public Long signUpMember(MemberCreateRequest memberCreateRequest){

        String email=memberCreateRequest.email();

        if ( memberRepository.existsByEmail(email)){
            throw new RuntimeException("member email이 이미 존재합니다");
        }


        Member member=Member.builder()
                .memberName(memberCreateRequest.memberName())
                .email(memberCreateRequest.email())
                .password(passwordEncoder.encode(memberCreateRequest.password()))
                .birth(memberCreateRequest.birth())
                .gener(memberCreateRequest.gender())
                .latitdue(memberCreateRequest.latitude())
                .longitude(memberCreateRequest.longitude())
                .role(Role.ROLE_MEMBER)
                .build();

        memberRepository.save(member);
        return member.getId();
    }

/*
    public MemberIdReadResponse findOneMember(Long id) throws IOException{
        Member member = getMemberById(id);

        return MemberIdReadResponse.builder()
                .memberName(member.getMemberName())
                .role(member.getRole())
                .build();
    }*/

    public Member getMemberById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow();
        return member;
    }


}
