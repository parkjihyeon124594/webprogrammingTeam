package webprogrammingTeam.matchingService.domain.subscription.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import webprogrammingTeam.matchingService.auth.principal.PrincipalDetails;
import webprogrammingTeam.matchingService.domain.Image.entity.Image;
import webprogrammingTeam.matchingService.domain.Image.repository.ImageRepository;
import webprogrammingTeam.matchingService.domain.Image.service.ImageService;
import webprogrammingTeam.matchingService.domain.channel.entity.Channel;
import webprogrammingTeam.matchingService.domain.channel.service.ChannelService;
import webprogrammingTeam.matchingService.domain.program.dto.response.ProgramAllReadResponse;
import webprogrammingTeam.matchingService.domain.program.entity.Program;
import webprogrammingTeam.matchingService.domain.program.repository.ProgramRepository;
import webprogrammingTeam.matchingService.domain.recruitment.entity.Recruitment;
import webprogrammingTeam.matchingService.domain.subscription.dto.MemberChannelSubscriptionDTO;
import webprogrammingTeam.matchingService.domain.subscription.dto.PrivateChannelsResponse;
import webprogrammingTeam.matchingService.domain.subscription.entity.MemberChannelSubscription;
import webprogrammingTeam.matchingService.domain.subscription.repository.MemberChannelSubscriptionRepository;
import webprogrammingTeam.matchingService.domain.member.entity.Member;
import webprogrammingTeam.matchingService.domain.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MemberChannelSubscriptionService {

    private final MemberChannelSubscriptionRepository memberChannelSubscriptionRepository;

    private final MemberService memberService;

    private final ChannelService channelService;

    private final ProgramRepository programRepository;

    private final ImageRepository imageRepository;

    public List<MemberChannelSubscriptionDTO> getAllSubscription() {
        return convertMemberChannelSubscriptionToMemberChannelSubscriptionDTO(memberChannelSubscriptionRepository.findAll());
    }

    private List<MemberChannelSubscriptionDTO> convertMemberChannelSubscriptionToMemberChannelSubscriptionDTO(List<MemberChannelSubscription> subscriptions) {
        return subscriptions.stream()
                .map(this::convertSubscriptionToSubscriptionDTO)
                .collect(Collectors.toList());
    }

    private MemberChannelSubscriptionDTO convertSubscriptionToSubscriptionDTO(MemberChannelSubscription subscription) {
        MemberChannelSubscriptionDTO subscriptionDTO = new MemberChannelSubscriptionDTO();
        subscriptionDTO.setMemberName(subscription.getMember().getMemberName());
        subscriptionDTO.setChannelId(subscription.getChannel().getChannelId());
        return subscriptionDTO;
    }

    public List<PrivateChannelsResponse> findChatIdsByMemberId(PrincipalDetails principalDetails) {
        List<Long> channelIds= memberChannelSubscriptionRepository.findByMember_Id(principalDetails.getMember().getId())
                .stream()
                .map(subscription -> subscription.getChannel().getChannelId())
                .collect(Collectors.toList());

        List<PrivateChannelsResponse> programList = new ArrayList<>();
        for(Long channelId : channelIds){
            log.info("나온다1 ; {}", channelId);
            Program program = programRepository.findProgramByPrivateChannel_ChannelId(channelId);
            Image image = imageRepository.findAllByProgramId(program.getId()).get(0);
            String imageUrl = image.getUrl();
            log.info("나온다2 ; {}", program.getId());
            programList.add(
                    new PrivateChannelsResponse(channelId, program.getId(), program.getTitle(), program.getCategory(), program.getProgramDate(), imageUrl)
            );
        }

        return programList;
    }

    public List<String> findMemberNamesByChannelId(Long channelId) {
        return memberChannelSubscriptionRepository.findByChannel_ChannelId(channelId)
                .stream()
                .map(subscription -> subscription.getMember().getMemberName())
                .collect(Collectors.toList());
    }

    //private channel 만들 때 다시 생각해보기

    /*public Long createPrivateChannelAndSubscription(String title, List<Long> memberIds) throws IOException {
        Channel newPrivateChannel = channelService.createPrivateChannel(title);

        for (Long memberId : memberIds) {
            createSubscription(memberId, newPrivateChannel.getChannelId());
        }

        return newPrivateChannel.getChannelId();
    }*/

    public Long createPublicSubscription(PrincipalDetails principalDetails, Long channelId) throws IOException {
        Member member = memberService.getMemberById(principalDetails.getMember().getId());
        Channel channel = channelService.getChannelById(channelId);
        MemberChannelSubscription subscription = new MemberChannelSubscription();
        subscription.setMember(member);
        subscription.setChannel(channel);

        MemberChannelSubscription newSubscription =  memberChannelSubscriptionRepository.save(subscription);
        return newSubscription.getSubscriptionId();
    }

    public void deleteSubscription(Long subscriptionId) {
        if (!memberChannelSubscriptionRepository.existsById(subscriptionId)) {
            throw new IllegalArgumentException("Subscription with ID " + subscriptionId + " does not exist");
        }
        memberChannelSubscriptionRepository.deleteById(subscriptionId);
    }

    public void deleteSubscriptionByMemberId(PrincipalDetails principalDetails) {
        memberChannelSubscriptionRepository.deleteByMember_Id(principalDetails.getMember().getId());
    }

    public void deleteSubscriptionByChannelId(Long channelId) {
        memberChannelSubscriptionRepository.deleteByChannel_ChannelId(channelId);
    }

    public boolean isSubscriber(Long channelId, Long senderId) {
        return memberChannelSubscriptionRepository.existsByChannelAndMember(
                channelService.getChannelById(channelId),
                memberService.getMemberById(senderId));
    }

    public Long createSubscription(Long memberId, Long channelId) {
        // 임시
        Member member = memberService.getMemberById(memberId);
        Channel channel = channelService.getChannelById(channelId);
        MemberChannelSubscription subscription = new MemberChannelSubscription();
        subscription.setMember(member);
        subscription.setChannel(channel);

        MemberChannelSubscription newSubscription =  memberChannelSubscriptionRepository.save(subscription);
        return newSubscription.getSubscriptionId();
    }

    @Transactional
    public Channel createPrivateChannelAndSubscriptions(Program program) {
        //private channel 생성
        Channel newPrivateChannel = channelService.createPrivateChannel(program.getTitle() + "private channel");
        Long newPrivateChannelId = newPrivateChannel.getChannelId();

        //개최자를 참여자 채팅에 추가
        createSubscription(program.getMember().getId(), newPrivateChannelId);

        //참여자들을 참여자 채팅에 추가
        List<Recruitment> recruitments = program.getRecruitments();
        for (Recruitment recruitment: recruitments) {
            createSubscription(recruitment.getMember().getId(), newPrivateChannelId);
        }
        return newPrivateChannel;
    }


}
