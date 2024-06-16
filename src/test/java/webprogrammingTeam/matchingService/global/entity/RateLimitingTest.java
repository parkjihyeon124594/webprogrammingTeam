package webprogrammingTeam.matchingService.global.entity;

import io.github.bucket4j.Bandwidth;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import webprogrammingTeam.matchingService.global.rateLimiting.entity.RateLimiting;
import webprogrammingTeam.matchingService.global.rateLimiting.exception.RateLimiterException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class RateLimitingTest {

    @Test
    @DisplayName("설정한 정책에 맞는 처리율 제한 장치 정책을 반환한다.")
    void resolvePlan() {
        // given
        String postRateLimit = "postRateLimit";
        String actionRateLimit = "actionRateLimit";

        // when
        Bandwidth postBandwidth = RateLimiting.resolvePlan(postRateLimit);
        Bandwidth actionBandwidth = RateLimiting.resolvePlan(actionRateLimit);

        // then
        assertAll(
                () -> assertThat(postBandwidth).isEqualTo(RateLimiting.POST_RATE_LIMIT.getLimit()),
                () -> assertThat(actionBandwidth).isEqualTo(RateLimiting.ACTION_RATE_LIMIT.getLimit())
        );
    }

    @Test
    @DisplayName("설정한 정책이 처리율 제한 장치에 없으면 예외가 발생한다.")
    void resolvePlanException() {
        // given
        String myPlan = "myPlan";

        // when // then
        assertThatThrownBy(() -> RateLimiting.resolvePlan(myPlan))
                .isInstanceOf(RateLimiterException.class)
                .hasMessage(RateLimiterException.RATE_LIMITE_NOT_FOUND);
    }
    @Test
    void getLimit() {
    }


    @Test
    void getRateLimitName() {
    }

    @Test
    void values() {
    }

    @Test
    void valueOf() {
    }
}