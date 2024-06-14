package webprogrammingTeam.matchingService.domain.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import webprogrammingTeam.matchingService.domain.member.entity.Member;
import webprogrammingTeam.matchingService.domain.member.repository.MemberRepository;
import webprogrammingTeam.matchingService.domain.notification.entity.Notification;
import webprogrammingTeam.matchingService.domain.notification.repository.EmitterRepository;
import webprogrammingTeam.matchingService.domain.notification.repository.NotificationRepository;

import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmitterRepository emitterRepository;
    private final MemberRepository memberRepository;

    private static final long TIMEOUT = 30 * 60 * 1000L;
    public SseEmitter subscribe(String email, String lastEventId) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new NoSuchElementException("member 이 없습니다"));
        Long memberId = member.getId();

        String emitterId = makeId(memberId);
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(TIMEOUT));
        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        String eventId = makeId(memberId);
        sendNotification(emitter, eventId, emitterId, "EventStream Created. [userId=" + memberId + "]");

        // 회원이 notification 을 수신하지 못 할 경우를 대비
        if (hasLostData(lastEventId)) {
            resendMissedEvents(lastEventId, memberId, emitterId, emitter);
        }

        return emitter;
    }

    private String makeId(Long memberId) {
        return memberId + "_" + System.currentTimeMillis();
    }

    private void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .data(data));
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
        }
    }

    //메세지를 보낼 때, 불려지는 로직
    public void send(Member member, String content, String url) {

        //notification 객체 저장
        Notification notification = Notification.builder()
                .member(member)
                .content(content)
                .url(url)
                .isRead(false)
                .build();

        notificationRepository.save(notification);

        String memberId = String.valueOf(member.getId());
        String eventId = memberId + "_" + System.currentTimeMillis();

        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterByMemberId(memberId);

        emitters.forEach(
                (key, emitter) -> {
                    //event 저장
                    emitterRepository.saveEventCache(key, notification);
                    sendNotification(emitter, eventId, key, notification);
                }
        );
    }

    private boolean hasLostData(String lastEventId) {
        return !lastEventId.isEmpty();
    }

    private void resendMissedEvents(String lastEventId, Long memberId, String emitterId, SseEmitter emitter) {
        Map<String, Object> missedEvents = emitterRepository.findAllEventCacheByMemberId(String.valueOf(memberId));
        missedEvents.entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                .forEach(entry -> sendNotification(emitter, entry.getKey(), emitterId, entry.getValue()));
    }



}
