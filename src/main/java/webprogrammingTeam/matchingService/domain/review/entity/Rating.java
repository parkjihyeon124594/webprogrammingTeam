package webprogrammingTeam.matchingService.domain.review.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Rating {
    ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5);

    private final int value;

    Rating(int value) {
        this.value = value;
    }

}
