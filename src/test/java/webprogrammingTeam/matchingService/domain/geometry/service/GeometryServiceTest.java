package webprogrammingTeam.matchingService.domain.geometry.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import webprogrammingTeam.matchingService.domain.Image.entity.Image;
import webprogrammingTeam.matchingService.domain.Image.repository.ImageRepository;
import webprogrammingTeam.matchingService.domain.geometry.dto.response.GeometryResponse;
import webprogrammingTeam.matchingService.domain.geometry.entity.Direction;
import webprogrammingTeam.matchingService.domain.geometry.entity.Location;
import webprogrammingTeam.matchingService.domain.geometry.util.GeometryUtil;
import webprogrammingTeam.matchingService.domain.program.entity.Category;
import webprogrammingTeam.matchingService.domain.program.entity.Open;
import webprogrammingTeam.matchingService.domain.program.entity.Program;
import webprogrammingTeam.matchingService.domain.program.repository.ProgramRepository;
import webprogrammingTeam.matchingService.domain.review.entity.Review;
import webprogrammingTeam.matchingService.domain.review.respository.ReviewRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class GeometryServiceTest {

    @Mock
    private ProgramRepository programRepository;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private GeometryService geometryService;

    @Mock
    private GeometryUtil geometryUtil;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindProgramsNearMember() {
        Program program = Program.builder()
                .latitude(23.0)
                .longitude(23.0)
                .build();
        program.setIdForTest(1L);
        
        when(programRepository.findByLatitudeBetweenAndLongitudeBetween(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(Arrays.asList(program));

        List<Program> programs = geometryService.findProgramsNearMember(37.5665, 126.9780, 5.0);

        assertNotNull(programs);
        assertEquals(1, programs.size());
        assertEquals(program.getId(), programs.get(0).getId());
    }





    @Test
    public void testCalculateAvgRating() {
        Program program = new Program();
        List<Review> reviews = Arrays.asList(Review.builder().rating(4).build(),
                Review.builder().rating(5).build());

        when(reviewRepository.findByProgram(program)).thenReturn(reviews);

        double avgRating = geometryService.calculateAvgRating(program);

        assertEquals(4.5, avgRating, 0.01);
        verify(reviewRepository).findByProgram(program);
    }

    @Test
    public void testWritingTimeToString() {
        LocalDateTime writingTime = LocalDateTime.of(2023, 6, 21, 14, 30);
        String expected = "2023-06-21 14:30";

        String result = GeometryService.writingTimeToString(writingTime);

        assertEquals(expected, result);
    }

    @Test
    public void testProgramToGeometryResponse() {
        Program program = createSampleProgram();
        Image image = Image.builder().url("http://example.com/image.jpg").build();
        Review review = Review.builder().rating(5).build();

        when(imageRepository.findAllByProgramId(program.getId())).thenReturn(Collections.singletonList(image));
        when(reviewRepository.findByProgram(program)).thenReturn(Collections.singletonList(review));
        when(reviewRepository.countReviewsByProgramId(program.getId())).thenReturn(1);

        List<GeometryResponse> responses = geometryService.programToGeometryResponse(Collections.singletonList(program));

        assertEquals(1, responses.size());
        GeometryResponse response = responses.get(0);
        assertEquals("Test Program", response.title());
        assertEquals("http://example.com/image.jpg", response.imageUrl());
        assertEquals(5.0, response.avgRating(), 0.01);
        assertEquals(1, response.ratingCnt());
    }

    @Test
    public void testRatingCnt() {
        Long programId = 1L;
        when(reviewRepository.countReviewsByProgramId(programId)).thenReturn(5);

        int count = geometryService.ratingCnt(programId);

        assertEquals(5, count);
        verify(reviewRepository).countReviewsByProgramId(programId);
    }



    private Program createSampleProgram() {
        Program program = Program.builder()
                .title("Test Program")
                .category(Category.COMPUTER)
                .open(Open.OPEN)
                .latitude(23.0)
                .longitude(23.0)
                .build();

        program.setCreateDate(LocalDateTime.of(2023, 6, 21, 14, 30));
        return program;
    }
}
