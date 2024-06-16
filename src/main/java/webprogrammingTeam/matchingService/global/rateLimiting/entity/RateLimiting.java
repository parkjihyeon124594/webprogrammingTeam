/*
package webprogrammingTeam.matchingService.global.rateLimiting.entity;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Refill;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import webprogrammingTeam.matchingService.global.rateLimiting.exception.RateLimiterException;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum RateLimiting {

   */
/* @Bean
    public Bucket bucket() {

        //60초에 3개의 토큰씩 충전
        final Refill refill = Refill.intervally(3, Duration.ofSeconds(60));

        //버킷의 크기는 3개
        final Bandwidth limit = Bandwidth.classic(3, refill);

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }*//*


    POST_RATE_LIMIT("postRateLimit") {
        @Override
        public Bandwidth getLimit() {
            // 버켓의 충전 간격을 30분으로 지정하며, 한 번 충전할 때 마다 1개의 토큰을 충전한다.
            return Bandwidth.classic(1, Refill.intervally(1, Duration.ofMinutes(30)));
        }
    },

    ACTION_RATE_LIMIT("actionRateLimit"){
        @Override
        public Bandwidth getLimit() {
            return Bandwidth.classic(1, Refill.intervally(1, Duration.ofMinutes(10)));
        }
    };

    public abstract Bandwidth getLimit();

    private final String rateLimitName;

    private static final Map<String, RateLimiting> PLAN_MAP = new HashMap<>();

    static {
        for (RateLimiting rateLimiting : RateLimiting.values()) {
            PLAN_MAP.put(rateLimiting.getRateLimitName(), rateLimiting);
        }
    }

    public static Bandwidth resolvePlan(String targetPlan) {
        RateLimiting plan = PLAN_MAP.get(targetPlan);
        if (plan != null) {
            return plan.getLimit();
        }
        throw new RateLimiterException(RateLimiterException.RATE_LIMITE_NOT_FOUND);
    }


}
*/
