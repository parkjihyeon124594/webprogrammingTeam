package webprogrammingTeam.matchingService.domain.review.entity;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import webprogrammingTeam.matchingService.domain.review.dto.request.ReviewUpdateRequest;
import webprogrammingTeam.matchingService.domain.board.entity.Board;
import webprogrammingTeam.matchingService.domain.member.entity.Member;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Review {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "rating")
    private Rating rating;

    @Column(name = "content")
    private String content;

    @Column(name = "date")
    private String date;

    @ManyToOne
    @JoinColumn(name = "boardId")
    private Board board;

    @ManyToOne
    @JoinColumn(name = "memberId")
    private Member member;

    @Builder
    public Review(Board board, Member member, String title, Rating rating, String content, String date)
    {
        this.board = board;
        this.member = member;
        this.title= title;
        this.rating = rating;
        this.content = content;
        this.date = date;
    }

    public void reviewUpdate(ReviewUpdateRequest reviewUpdateRequest)
    {
        this.rating = reviewUpdateRequest.rating();
        this.title = reviewUpdateRequest.title();
        this.content = reviewUpdateRequest.content();
    }
}
