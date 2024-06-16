package webprogrammingTeam.matchingService.domain.program.service;

import org.junit.jupiter.api.Test;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import webprogrammingTeam.matchingService.domain.program.dto.response.CategoryAgeGroupListResponse;
import webprogrammingTeam.matchingService.domain.program.dto.response.CategoryAgeGroupResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBatchTest
class ProgramServiceTest {

    @Autowired
    private ProgramService programService;

    @Test
    void findByCityAndCategory() {
        // Given
        String city = "서울";
        String category = "computer";

        // When
        CategoryAgeGroupListResponse response = programService.findByCityAndCategory(city, category);
        List<CategoryAgeGroupResponse> ageGroupDTOs = response.ageGroups();

        // Then
        assertNotNull(response);
        assertNotNull(ageGroupDTOs);
        assertEquals(1, ageGroupDTOs.size()); // 예상되는 결과가 하나의 카테고리만 있을 경우

    }
}