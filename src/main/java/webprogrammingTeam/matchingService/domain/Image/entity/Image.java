package webprogrammingTeam.matchingService.domain.Image.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import webprogrammingTeam.matchingService.domain.program.entity.Program;


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
    @JoinColumn(name = "program_id")
    private Program program;

    private String url;


    @Builder
    public Image(String fileName,String url){
        this.fileName=fileName;
        this.url =url;
    }

}

