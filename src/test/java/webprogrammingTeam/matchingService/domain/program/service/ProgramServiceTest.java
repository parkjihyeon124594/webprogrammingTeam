package webprogrammingTeam.matchingService.domain.program.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;
import webprogrammingTeam.matchingService.domain.Image.entity.Image;
import webprogrammingTeam.matchingService.domain.Image.repository.ImageRepository;
import webprogrammingTeam.matchingService.domain.Image.service.ImageService;
import webprogrammingTeam.matchingService.domain.channel.entity.Channel;
import webprogrammingTeam.matchingService.domain.channel.service.ChannelService;
import webprogrammingTeam.matchingService.domain.member.entity.Member;
import webprogrammingTeam.matchingService.domain.member.repository.MemberRepository;
import webprogrammingTeam.matchingService.domain.message.service.MessageService;
import webprogrammingTeam.matchingService.domain.program.dto.request.ProgramSaveRequest;
import webprogrammingTeam.matchingService.domain.program.dto.request.ProgramUpdateRequest;
import webprogrammingTeam.matchingService.domain.program.dto.response.ProgramAllReadResponse;
import webprogrammingTeam.matchingService.domain.program.dto.response.ProgramIdReadResponse;
import webprogrammingTeam.matchingService.domain.program.entity.Category;
import webprogrammingTeam.matchingService.domain.program.entity.Open;
import webprogrammingTeam.matchingService.domain.program.entity.Program;
import webprogrammingTeam.matchingService.domain.program.repository.ProgramRepository;
import webprogrammingTeam.matchingService.domain.review.entity.Review;
import webprogrammingTeam.matchingService.domain.review.respository.ReviewRepository;
import webprogrammingTeam.matchingService.domain.subscription.service.MemberChannelSubscriptionService;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProgramServiceTest {

    @Mock private ProgramRepository programRepository;
    @Mock private ImageRepository imageRepository;
    @Mock private ImageService imageService;
    @Mock private MemberRepository memberRepository;
    @Mock private ReviewRepository reviewRepository;
    @Mock private ChannelService channelService;
    @Mock private MessageService messageService;
    @Mock private MemberChannelSubscriptionService memberChannelSubscriptionService;
    @InjectMocks private ProgramService programService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveProgram() throws IOException {
        ProgramSaveRequest request = createProgramSaveRequest();
        MultipartFile[] imageList = new MultipartFile[0];
        String email = "test@example.com";

        Member member = Member.builder().build();
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        Channel channel = new Channel();
        when(channelService.createPublicChannel(anyString())).thenReturn(channel);
        when(programRepository.save(any(Program.class))).thenAnswer(invocation -> {
            Program program = invocation.getArgument(0);
            program.setIdForTest(1L);
            return program;
        });

        Long programId = programService.saveProgram(request, imageList, email);

        assertEquals(1L, programId);
        verifyInteractionsForSaveProgram(email);
    }

    @Test
    public void testSaveProgram_MemberNotFound() {
        ProgramSaveRequest request = createProgramSaveRequest();
        MultipartFile[] imageList = new MultipartFile[0];
        String email = "test@example.com";

        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> programService.saveProgram(request, imageList, email));
        verify(memberRepository).findByEmail(email);
        verifyNoInteractionsForSaveProgram();
    }

    @Test
    public void testUpdateProgram_Success() throws IOException {
        ProgramUpdateRequest request = createProgramUpdateRequest();
        MultipartFile[] newImageList = new MultipartFile[0];
        Long programId = 1L;
        String email = "test@example.com";

        Program program = createProgramWithMemberAndPublicChannel(email, programId);
        when(programRepository.findById(programId)).thenReturn(Optional.of(program));

        Long updatedProgramId = programService.updateProgram(request, newImageList, programId, email);

        assertEquals(programId, updatedProgramId);
        verifyInteractionsForUpdateProgram(programId, newImageList);
    }

    @Test
    public void testUpdateProgram_AccessDenied() throws IOException {
        ProgramUpdateRequest request = createProgramUpdateRequest();
        MultipartFile[] newImageList = new MultipartFile[0];
        Long programId = 1L;
        String email = "test@example.com";

        Program program = createProgramWithMemberAndPublicChannel("different@example.com", programId);
        when(programRepository.findById(programId)).thenReturn(Optional.of(program));

        assertThrows(AccessDeniedException.class, () -> programService.updateProgram(request, newImageList, programId, email));
        verify(programRepository).findById(programId);
        verifyNoInteractionsForUpdateProgram();
    }

    @Test
    public void testDeleteProgram_Success() throws AccessDeniedException {
        Long programId = 1L;
        String email = "test@example.com";

        Program program = createProgramWithMemberAndPublicChannel(email, programId);
        Channel publicChannel = createPublicChannel();
        when(programRepository.findById(programId)).thenReturn(Optional.of(program));

        programService.deleteProgram(programId, email);

        verifyInteractionsForDeleteProgram(programId, publicChannel);
    }

    @Test
    public void testDeleteProgram_AccessDenied() {
        Long programId = 1L;
        String email = "test@example.com";

        Program program = createProgramWithMemberAndPublicChannel("different@example.com", programId);
        when(programRepository.findById(programId)).thenReturn(Optional.of(program));

        assertThrows(AccessDeniedException.class, () -> programService.deleteProgram(programId, email));
        verify(programRepository).findById(programId);
        verifyNoInteractionsForDeleteProgram();
    }

    @Test
    public void testFindAllMyPrograms_Success() throws IOException {
        String email = "test@example.com";
        Member member = Member.builder().build();
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));

        Program program = createSampleProgram();
        Image image = createSampleImage();
        when(programRepository.findAllByMember(member)).thenReturn(Collections.singletonList(program));
        when(imageRepository.findAllByProgramId(program.getId())).thenReturn(Collections.singletonList(image));

        List<ProgramAllReadResponse> responseList = programService.findAllMyPrograms(email);

        assertNotNull(responseList);
        assertEquals(1, responseList.size());
        assertEquals("Test Program", responseList.get(0).title());
        assertEquals("http://example.com/image.jpg", responseList.get(0).imageUrl());
        verifyInteractionsForFindAllMyPrograms(email, program.getId());
    }

    @Test
    public void testFindAllMyPrograms_MemberNotFound() throws IOException {
        String email = "test@example.com";
        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

        List<ProgramAllReadResponse> responseList = programService.findAllMyPrograms(email);

        assertNull(responseList);
        verify(memberRepository).findByEmail(email);
        verifyNoInteractionsForFindAllMyPrograms();
    }

    @Test
    public void testFindAllProgram_Success() {
        Program program = createSampleProgram();
        Image image = createSampleImage();
        when(programRepository.findAll()).thenReturn(Collections.singletonList(program));
        when(imageRepository.findAllByProgramId(program.getId())).thenReturn(Collections.singletonList(image));

        List<ProgramAllReadResponse> responseList = programService.findAllProgram();

        assertNotNull(responseList);
        assertEquals(1, responseList.size());
        assertEquals("Test Program", responseList.get(0).title());
        assertEquals("http://example.com/image.jpg", responseList.get(0).imageUrl());
        verify(programRepository).findAll();
        verify(imageRepository).findAllByProgramId(program.getId());
    }

    @Test
    public void testFindAllProgram_Failure() {
        when(programRepository.findAll()).thenThrow(new RuntimeException("Failed to load programs"));

        List<ProgramAllReadResponse> responseList = programService.findAllProgram();

        assertNull(responseList);
        verify(programRepository).findAll();
    }

    @Test
    public void testCalculateAvgRating() {
        Program program = new Program();
        List<Review> reviews = Arrays.asList(Review.builder().rating(4).build(),
                Review.builder().rating(5).build());

        when(reviewRepository.findByProgram(program)).thenReturn(reviews);

        double avgRating = programService.calculateAvgRating(program);

        assertEquals(4.5, avgRating, 0.01);
        verify(reviewRepository).findByProgram(program);
    }

    @Test
    public void testCalculateAvgRating_NoReviews() {
        Program program = new Program();
        when(reviewRepository.findByProgram(program)).thenReturn(Collections.emptyList());

        double avgRating = programService.calculateAvgRating(program);

        assertTrue(Double.isNaN(avgRating));
        verify(reviewRepository).findByProgram(program);
    }

    @Test
    public void testRatingCnt() {
        Long programId = 1L;
        when(reviewRepository.countReviewsByProgramId(programId)).thenReturn(5);

        int count = programService.ratingCnt(programId);

        assertEquals(5, count);
        verify(reviewRepository).countReviewsByProgramId(programId);
    }

    @Test
    public void testFindOneProgram_Success() throws IOException {
        Long programId = 1L;
        Program program = createSampleProgramWithMember();
        List<Image> images = Arrays.asList(createSampleImage("http://example.com/image1.jpg"), createSampleImage("http://example.com/image2.jpg"));
        when(programRepository.findById(programId)).thenReturn(Optional.of(program));
        when(imageService.getImageList(Optional.of(program))).thenReturn(images);

        ProgramIdReadResponse response = programService.findOneProgram(programId);

        assertNotNull(response);
        assertEquals(programId, response.programId());
        assertEquals("test@example.com", response.memberEmail());
        assertEquals("John Doe", response.memberName());
        assertEquals(Arrays.asList("http://example.com/image1.jpg", "http://example.com/image2.jpg"), response.images());
        verify(programRepository).findById(programId);
        verify(imageService).getImageList(Optional.of(program));
    }

    @Test
    public void testFindOneProgram_NotFound() {
        Long programId = 1L;
        when(programRepository.findById(programId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> programService.findOneProgram(programId));
        verify(programRepository).findById(programId);
        verifyNoInteractionsForFindOneProgram();
    }

    private ProgramSaveRequest createProgramSaveRequest() {
        return new ProgramSaveRequest(
                "Test Title", "Test Content", Category.ART, 10,
                "2024-01-01T10:00", "2024-01-10T10:00", "2024-01-20T10:00",
                Open.OPEN, 37.7749, -122.4194, "Test Address");
    }

    private ProgramUpdateRequest createProgramUpdateRequest() {
        return new ProgramUpdateRequest(
                "Updated Title", "Updated Content", Category.COMPUTER, 20,
                "2024-02-01T10:00", "2024-02-10T10:00", "2024-02-20T10:00",
                Open.CLOSED, 38.7749, -123.4194, "Updated Address");
    }

    private Program createSavedProgram() {
        Program savedProgram = Program.builder()
                .build();
        savedProgram.setIdForTest(1L);
        return savedProgram;
    }

    private Program createProgramWithMemberAndPublicChannel(String email, Long programId) {
        Channel publicChannel = createPublicChannel();
        Member member = Member.builder()
                .email(email)
                .build();
        Program program = Program.builder()
                .member(member)
                .latitude(23.0)
                .longitude(23.0)
                .publicChannel(publicChannel)
                .build();
        program.setIdForTest(programId);
        return program;
    }

    private Channel createPublicChannel() {
        Channel publicChannel = new Channel();
        publicChannel.setChannelId(1L);
        return publicChannel;
    }

    private Program createSampleProgram() {
        Channel publicChannel = createPublicChannel();
        Program program = Program.builder()
                .title("Test Program")
                .category(Category.COMPUTER)
                .open(Open.OPEN)
                .recruitment(0)
                .publicChannel(publicChannel)
                .latitude(23.0)
                .longitude(23.0)
                .build();
        program.setIdForTest(1L);
        program.setCreateDate(LocalDateTime.now());

        return program;
    }

    private Program createSampleProgramWithMember() {
        Member member = Member.builder()
                .email("test@example.com")
                .memberName("John Doe")
                .build();
        Channel channel = new Channel();
        channel.setChannelId(1L);
        Program program = Program.builder()
                .member(member)
                .latitude(23.0)
                .longitude(23.0)
                .publicChannel(channel)
                .build();
        program.setIdForTest(1L);
        program.setCreateDate(LocalDateTime.now());
        return program;
    }

    private Image createSampleImage() {
        return Image.builder().url("http://example.com/image.jpg").build();
    }

    private Image createSampleImage(String url) {
        return Image.builder().url(url).build();
    }

    private void verifyInteractionsForSaveProgram(String email) {
        verify(memberRepository).findByEmail(email);
        verify(channelService).createPublicChannel(anyString());
        verify(programRepository).save(any(Program.class));
    }

    private void verifyNoInteractionsForSaveProgram() {
        verify(channelService, never()).createPublicChannel(anyString());
        verify(programRepository, never()).save(any(Program.class));
    }

    private void verifyInteractionsForUpdateProgram(Long programId, MultipartFile[] newImageList) throws IOException {
        verify(programRepository).findById(programId);
        verify(imageRepository).deleteAllByProgramId(programId);
        verify(imageService).uploadImages(any(Program.class), eq(newImageList));
        verify(programRepository).save(any(Program.class));
    }

    private void verifyNoInteractionsForUpdateProgram() throws IOException {
        verify(imageRepository, never()).deleteAllByProgramId(anyLong());
        verify(imageService, never()).uploadImages(any(Program.class), any());
        verify(programRepository, never()).save(any(Program.class));
    }

    private void verifyInteractionsForDeleteProgram(Long programId, Channel publicChannel) {
        verify(programRepository).findById(programId);
        verify(messageService).deleteAllMessageByChannelId(publicChannel.getChannelId());
        verify(programRepository).delete(any(Program.class));
    }

    private void verifyNoInteractionsForDeleteProgram() {
        verify(messageService, never()).deleteAllMessageByChannelId(anyLong());
        verify(programRepository, never()).delete(any(Program.class));
    }

    private void verifyInteractionsForFindAllMyPrograms(String email, Long programId) {
        verify(memberRepository).findByEmail(email);
        verify(programRepository).findAllByMember(any(Member.class));
        verify(imageRepository).findAllByProgramId(programId);
    }

    private void verifyNoInteractionsForFindAllMyPrograms() {
        verify(programRepository, never()).findAllByMember(any(Member.class));
        verify(imageRepository, never()).findAllByProgramId(anyLong());
    }

    private void verifyNoInteractionsForFindOneProgram() {
        verify(imageService, never()).getImageList(any());
    }
}
