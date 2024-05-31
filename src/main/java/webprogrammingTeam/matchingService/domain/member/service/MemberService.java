package webprogrammingTeam.matchingService.domain.member.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webprogrammingTeam.matchingService.auth.dto.OAuth2DTO;
import webprogrammingTeam.matchingService.domain.member.dto.response.MemberIdReadResponse;
import webprogrammingTeam.matchingService.domain.member.entity.Member;
import webprogrammingTeam.matchingService.domain.member.repository.MemberRepository;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;


    @Transactional
    public Member saveMember(OAuth2DTO oAuth2DTO){
        Member member =oAuth2DTO.oAuth2DtoToMember(oAuth2DTO);
        return memberRepository.save(member);
    }

    public MemberIdReadResponse findOneMember(Long id) throws IOException{
        Member member = getMemberById(id);

        return MemberIdReadResponse.builder()
                .memberName(member.getMemberName())
                .role(member.getRole())
                .build();
    }

    public Member getMemberById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow();
        return member;
    }


}
