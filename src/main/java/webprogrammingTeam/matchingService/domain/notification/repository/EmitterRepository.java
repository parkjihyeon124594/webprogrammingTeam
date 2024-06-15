package webprogrammingTeam.matchingService.domain.notification.repository;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
@NoArgsConstructor
//참고 https://gilssang97.tistory.com/69
public class EmitterRepository {

    //어떤 회원이 어떤 emitter을 가지고 있는지 저장
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    //어떤 이벤트들이 현재 까지 발생했는지 저장
    private final Map<String, Object> eventCache = new ConcurrentHashMap<>();


    //sseEmitter 저장
    public SseEmitter save(String emitterId, SseEmitter sseEmitter) {
        emitters.put(emitterId, sseEmitter);
        return sseEmitter;
    }

    //이벤트 저장
    public void saveEventCache(String eventCacheId, Object event) {
        eventCache.put(eventCacheId, event);
    }

    //해당 회원과 관련된 모든 emitter 찾기
    public Map<String, SseEmitter> findAllEmitterByMemberId(String memberId) {
        return emitters.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(memberId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    //해당 회원과 관련된 모든 eventCache 찾기
    public Map<String, Object> findAllEventCacheByMemberId(String memberId) {
        return eventCache.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(memberId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    //해당 emitter 삭제
    public void deleteById(String id) {
        emitters.remove(id);
    }


    //회원과 관련된 모든 emitter 삭제
    public void deleteAllEmitterByMemberId(String memberId) {
        emitters.forEach(
                (key, emitter) -> {
                    if (key.startsWith(memberId)) {
                        emitters.remove(key);
                    }
                }
        );
    }

    //회원과 관련된 모든 이벤트 삭제
    public void deleteAllEventCacheByMemberId(String memberId) {
        eventCache.forEach(
                (key, emitter) -> {
                    if (key.startsWith(memberId)) {
                        eventCache.remove(key);
                    }
                }
        );
    }


}
