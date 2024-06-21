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

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindProgramsNearMember() {
        Double memberLatitude = 37.7749;
        Double memberLongitude = -122.4194;
        double radius = 5.0;

        Location north = new Location(38.0, -122.4194);
        Location south = new Location(37.5, -122.4194);
        Location east = new Location(37.7749, -122.0);
        Location west = new Location(37.7749, -122.8);

        mockGeometryUtilCalculations(memberLatitude, memberLongitude, radius, north, south, east, west);

        List<Program> programs = Arrays.asList(new Program(), new Program());
        when(programRepository.findByLatitudeBetweenAndLongitudeBetween(south.getLatitude(), north.getLatitude(), west.getLongitude(), east.getLongitude()))
                .thenReturn(programs);

        List<Program> result = geometryService.findProgramsNearMember(memberLatitude, memberLongitude, radius);

        assertEquals(2, result.size());
        verify(programRepository).findByLatitudeBetweenAndLongitudeBetween(south.getLatitude(), north.getLatitude(), west.getLongitude(), east.getLongitude());
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

    private void mockGeometryUtilCalculations(Double memberLatitude, Double memberLongitude, double radius, Location north, Location south, Location east, Location west) {
        when(GeometryUtil.calculate(memberLatitude, memberLongitude, radius, Direction.NORTH.getBearing())).thenReturn(north);
        when(GeometryUtil.calculate(memberLatitude, memberLongitude, radius, Direction.SOUTH.getBearing())).thenReturn(south);
        when(GeometryUtil.calculate(memberLatitude, memberLongitude, radius, Direction.EAST.getBearing())).thenReturn(east);
        when(GeometryUtil.calculate(memberLatitude, memberLongitude, radius, Direction.WEST.getBearing())).thenReturn(west);
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
