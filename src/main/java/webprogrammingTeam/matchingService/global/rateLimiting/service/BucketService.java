package webprogrammingTeam.matchingService.global.rateLimiting.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class BucketService {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    // 요청자 IP 추출
    private String getHost(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getHeader("Host");
    }

    // 버킷 가져오기
    public Bucket resolveBucket(HttpServletRequest httpServletRequest) {
        return cache.computeIfAbsent(getHost(httpServletRequest), this::newBucket);
    }

    // 버킷 생성
    private Bucket newBucket(String apiKey) {
        return Bucket4j.builder()
                // 버켓의 충전 간격을 1일로로 지정하며, 한 번 충전할 때 마다 1개의 토큰을 충전한다.
                //.addLimit(Bandwidth.classic(1, Refill.intervally(1, Duration.ofDays(1))))
                .addLimit(Bandwidth.classic(1, Refill.intervally(2, Duration.ofSeconds(10))))
                .build();
    }


}
