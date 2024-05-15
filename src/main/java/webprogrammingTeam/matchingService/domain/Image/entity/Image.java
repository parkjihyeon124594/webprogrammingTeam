package webprogrammingTeam.matchingService.domain.Image.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import webprogrammingTeam.matchingService.domain.board.entity.Board;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long image_id;

    private String fileName;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    private String url;


    @Builder
    public Image(String fileName,String url){
        this.fileName=fileName;
        this.url =url;
    }

}

