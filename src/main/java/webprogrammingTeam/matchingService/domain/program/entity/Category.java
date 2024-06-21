package webprogrammingTeam.matchingService.domain.program.entity;

import lombok.Getter;

@Getter
public enum Category {
    SPORTS, COMPUTER, ART, COOKING, MUSIC, ETC;

    public static Category[] getAllCategories() {
        return values();
    }
}
