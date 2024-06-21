package webprogrammingTeam.matchingService.domain.member.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import webprogrammingTeam.matchingService.domain.member.dto.request.MemberCreateRequest;
import webprogrammingTeam.matchingService.domain.member.entity.Member;
import webprogrammingTeam.matchingService.domain.member.repository.MemberRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSignUpMember_Success() {
        MemberCreateRequest request = createMemberCreateRequest();

        when(memberRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("encodedPassword");
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> {
            Member member = invocation.getArgument(0);
            member.setIdForTest(1L);
            return member;
        });

        Long memberId = memberService.signUpMember(request);

        assertNotNull(memberId);
        assertEquals(1L, memberId);
        verifyInteractionsForSignUpMember(request);
    }

    @Test
    public void testSignUpMember_EmailAlreadyExists() {
        MemberCreateRequest request = createMemberCreateRequest();

        when(memberRepository.existsByEmail(request.email())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> memberService.signUpMember(request));

        assertEquals("member email이 이미 존재합니다", exception.getMessage());
        verify(memberRepository).existsByEmail(request.email());
        verifyNoFurtherInteractionsForSignUpMember();
    }

    @Test
    public void testGetMemberById_Success() {
        Member member = createMemberWithId(1L);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        Member result = memberService.getMemberById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(memberRepository).findById(1L);
    }

    @Test
    public void testGetMemberById_NotFound() {
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> memberService.getMemberById(1L));
        verify(memberRepository).findById(1L);
    }

    @Test
    public void testGetMemberByEmail_Success() {
        Member member = createMemberWithEmail("test@example.com");
        when(memberRepository.findByEmail("test@example.com")).thenReturn(Optional.of(member));

        Member result = memberService.getMemberByEmail("test@example.com");

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        verify(memberRepository).findByEmail("test@example.com");
    }

    @Test
    public void testGetMemberByEmail_NotFound() {
        when(memberRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> memberService.getMemberByEmail("test@example.com"));
        verify(memberRepository).findByEmail("test@example.com");
    }

    private MemberCreateRequest createMemberCreateRequest() {
        return new MemberCreateRequest(
                "test@example.com", "password", "John Doe", "MALE", 37.7749, 37.7749, 10
        );
    }

    private Member createMemberWithId(Long id) {
        Member member = Member.builder().build();
        member.setIdForTest(id);
        return member;
    }

    private Member createMemberWithEmail(String email) {
        Member member = Member.builder()
                .email(email)
                .build();
        return member;
    }

    private void verifyInteractionsForSignUpMember(MemberCreateRequest request) {
        verify(memberRepository).existsByEmail(request.email());
        verify(passwordEncoder).encode(request.password());
        verify(memberRepository).save(any(Member.class));
    }

    private void verifyNoFurtherInteractionsForSignUpMember() {
        verify(passwordEncoder, never()).encode(anyString());
        verify(memberRepository, never()).save(any(Member.class));
    }
}
