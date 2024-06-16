package webprogrammingTeam.matchingService.domain.notification.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import webprogrammingTeam.matchingService.domain.member.entity.Member;
import webprogrammingTeam.matchingService.domain.member.entity.Role;
import webprogrammingTeam.matchingService.domain.notification.entity.Notification;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EmitterRepositoryTest {

    @Mock
    private EmitterRepository emitterRepository;

    private final Long DEFAULT_TIMEOUT = 60L * 1000L * 60L;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("새로운 Emitter를 추가한다.")
    public void save() throws Exception {
        //given
        Long memberId = 1L;
        String emitterId =  memberId + "_" + System.currentTimeMillis();
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);

        //when, then
        Assertions.assertDoesNotThrow(() -> emitterRepository.save(emitterId, sseEmitter));
    }

    @Test
    @DisplayName("수신한 이벤트를 캐시에 저장한다.")
    public void saveEventCache() throws Exception {
        //given
        Long memberId = 1L;
        String eventCacheId =  memberId + "_" + System.currentTimeMillis();
        Notification notification = new Notification(new Member("gyuri", Role.ROLE_MEMBER, "haha0888@naver.com", 29,"여성", "rbfl1234",39.9, 30.0)
                , false, "신청이 왔습니다.", "url");
        //when, then
        Assertions.assertDoesNotThrow(() -> emitterRepository.saveEventCache(eventCacheId, notification));
    }
    @Test
    @DisplayName("회원의 아이디로 모든 Emitter를 찾는다")
    public void findAllEmitterByMemberId() throws Exception {
        //given
        Long memberId = 1L;
        String emitterId1 = memberId + "_" + System.currentTimeMillis();
        SseEmitter sseEmitter1 = new SseEmitter(DEFAULT_TIMEOUT);
        String emitterId2 = memberId + "_" + System.currentTimeMillis() + 100;
        SseEmitter sseEmitter2 = new SseEmitter(DEFAULT_TIMEOUT);
        String emitterId3 = memberId + "_" + System.currentTimeMillis() + 200;
        SseEmitter sseEmitter3 = new SseEmitter(DEFAULT_TIMEOUT);

        Map<String, SseEmitter> mockResult = new HashMap<>();
        mockResult.put(emitterId1, sseEmitter1);
        mockResult.put(emitterId2, sseEmitter2);
        mockResult.put(emitterId3, sseEmitter3);

        Mockito.doAnswer(invocation -> {
            String id = invocation.getArgument(0);
            SseEmitter emitter = invocation.getArgument(1);
            mockResult.put(id, emitter);
            return null;
        }).when(emitterRepository).save(Mockito.anyString(), Mockito.any(SseEmitter.class));

        Mockito.when(emitterRepository.findAllEmitterByMemberId(Mockito.anyString())).thenReturn(mockResult);

        //when
        emitterRepository.save(emitterId1, sseEmitter1);
        Thread.sleep(100);
        emitterRepository.save(emitterId2, sseEmitter2);
        Thread.sleep(100);
        emitterRepository.save(emitterId3, sseEmitter3);

        Map<String, SseEmitter> actualResult = emitterRepository.findAllEmitterByMemberId(String.valueOf(memberId));

        //then
        Assertions.assertEquals(3, actualResult.size());
    }


    @Test
    @DisplayName("어떤 회원에게 수신된 이벤트를 캐시에서 모두 찾는다.")
    public void findAllEventCacheByMemberId() throws Exception {
        //given
        Long memberId = 1L;
        String eventCacheId1 =  memberId + "_" + System.currentTimeMillis();
        Notification notification1 = new Notification(new Member("gyuri", Role.ROLE_MEMBER, "haha0888@naver.com", 29,"여성", "rbfl1234",39.9, 30.0), false, "신청이 왔습니다.", "url");
        emitterRepository.saveEventCache(eventCacheId1, notification1);

        Thread.sleep(100);
        String eventCacheId2 =  memberId + "_" + System.currentTimeMillis();
        Notification notification2 = new Notification(new Member("gyuri", Role.ROLE_MEMBER, "haha0888@naver.com", 29,"여성", "rbfl1234",39.9, 30.0), false, "신청이 왔습니다.", "url");
        emitterRepository.saveEventCache(eventCacheId2, notification2);

        Thread.sleep(100);
        String eventCacheId3 =  memberId + "_" + System.currentTimeMillis();
        Notification notification3 = new Notification(new Member("gyuri", Role.ROLE_MEMBER, "haha0888@naver.com", 29,"여성", "rbfl1234",39.9, 30.0), false, "신청이 왔습니다.", "url");
        emitterRepository.saveEventCache(eventCacheId3, notification3);

        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put(eventCacheId1, notification1);
        mockResult.put(eventCacheId2, notification2);
        mockResult.put(eventCacheId3, notification3);

        Mockito.when(emitterRepository.findAllEventCacheByMemberId(String.valueOf(memberId))).thenReturn(mockResult);

        //when
        Map<String, Object> actualResult = emitterRepository.findAllEventCacheByMemberId(String.valueOf(memberId));

        //then
        Assertions.assertEquals(3, actualResult.size());
    }


    @Test
    @DisplayName("ID를 통해 Emitter를 Repository에서 제거한다.")
    public void deleteById() throws Exception {
        //given
        Long memberId = 1L;
        String emitterId =  memberId + "_" + System.currentTimeMillis();
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);

        //when
        emitterRepository.save(emitterId, sseEmitter);
        emitterRepository.deleteById(emitterId);

        //then
        Assertions.assertEquals(0, emitterRepository.findAllEmitterByMemberId(emitterId).size());
    }

    @Test
    @DisplayName("저장된 모든 Emitter를 제거한다.")
    public void deleteAllEmitterStartWithId() throws Exception {
        //given
        Long memberId = 1L;
        String emitterId1 = memberId + "_" + System.currentTimeMillis();
        emitterRepository.save(emitterId1, new SseEmitter(DEFAULT_TIMEOUT));

        Thread.sleep(100);
        String emitterId2 = memberId + "_" + System.currentTimeMillis();
        emitterRepository.save(emitterId2, new SseEmitter(DEFAULT_TIMEOUT));

        //when
        emitterRepository.deleteAllEmitterByMemberId(String.valueOf(memberId));

        //then
        Assertions.assertEquals(0, emitterRepository.findAllEmitterByMemberId(String.valueOf(memberId)).size());
    }

    @Test
    @DisplayName("수신한 이벤트를 캐시에 저장한다.")
    public void deleteAllEventCacheStartWithId() throws Exception {
        //given
        Long memberId = 1L;
        String eventCacheId1 =  memberId + "_" + System.currentTimeMillis();
        Notification notification1 = new Notification(new Member("gyuri", Role.ROLE_MEMBER, "haha0888@naver.com", 29,"여성", "rbfl1234",39.9, 30.0)
                , false, "신청이 왔습니다.", "url");
        emitterRepository.saveEventCache(eventCacheId1, notification1);

        Thread.sleep(100);
        String eventCacheId2 =  memberId + "_" + System.currentTimeMillis();
        Notification notification2 = new Notification(new Member("gyuri", Role.ROLE_MEMBER, "haha0888@naver.com", 29,"여성", "rbfl1234",39.9, 30.0)
                , false, "신청이 왔습니다.", "url");
        emitterRepository.saveEventCache(eventCacheId2, notification2);

        //when
        emitterRepository.deleteAllEventCacheByMemberId(String.valueOf(memberId));

        //then
        Assertions.assertEquals(0, emitterRepository.findAllEventCacheByMemberId(String.valueOf(memberId)).size());
    }

}