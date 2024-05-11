package webprogrammingTeam.matchingService.domain.Review.Entity;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import webprogrammingTeam.matchingService.domain.Board.Entity.Board;
import webprogrammingTeam.matchingService.domain.user.entity.User;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Review {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @Column(name = "rating")
    private float rating;

    @Column(name = "content")
    private String content;

    @Column(name = "date")
    private String date;

    @ManyToOne
    @JoinColumn(name = "boardId")
    private Board board;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @Builder
    public Review(Board board, User user, float rating, String content, String date)
    {
        this.board = board;
        this.user = user;
        this.rating = rating;
        this.content = content;
        this.date = date;
    }

}
