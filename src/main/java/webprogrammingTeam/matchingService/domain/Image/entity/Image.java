package webprogrammingTeam.matchingService.domain.Image.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import webprogrammingTeam.matchingService.domain.program.entity.Program;
import webprogrammingTeam.matchingService.global.entity.BaseTimeEntity;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class Image extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long image_id;

    private String fileName;

    @ManyToOne
    @JoinColumn(name = "program_id")
    private Program program;

    private String url;


    @Builder
    public Image(String fileName,String url){
        this.fileName=fileName;
        this.url =url;
    }

    public void update() {

    }
}

