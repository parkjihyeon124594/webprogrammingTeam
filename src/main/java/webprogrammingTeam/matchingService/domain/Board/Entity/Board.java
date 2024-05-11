package webprogrammingTeam.matchingService.domain.Board.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import webprogrammingTeam.matchingService.User.User;
import webprogrammingTeam.matchingService.domain.Image.Entity.Image;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Board {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name="title")
    private String title;

    @Column(name="content")
    private String content;

    @Column(name="date")
    private String date;

    @OneToMany(mappedBy = "board",cascade = CascadeType.ALL)
    @JsonIgnore // JSON 직렬화 과정에서 무시
    private List<Image> images = new ArrayList<>();

    @Builder
    public Board(User user, String title, String content, String date){
        this.user = user;
        this.title=title;
        this.content=content;
        this.date=date;
    }

    public void addImageList(Image image){
        images.add(image);
        image.setBoard(this);
    }
}
