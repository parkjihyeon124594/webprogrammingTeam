package webprogrammingTeam.matchingService.domain.program.entity;

import lombok.Getter;

@Getter
public enum Category {
    SPORTS, COMPUTER, ART;

    public static Category[] getAllCategories() {
        return values();
    }
}
