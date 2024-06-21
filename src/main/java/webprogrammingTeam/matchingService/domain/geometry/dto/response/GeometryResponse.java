package webprogrammingTeam.matchingService.domain.geometry.dto.response;

import webprogrammingTeam.matchingService.domain.program.entity.Category;
import webprogrammingTeam.matchingService.domain.program.entity.Open;

public record GeometryResponse(Long id, double latitude, double longitude, String title, Category category, Open open, String writingTime, String imageUrl, int recruitment, double avgRating, int ratingCnt) {
}
