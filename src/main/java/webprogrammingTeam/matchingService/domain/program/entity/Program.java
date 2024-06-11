package webprogrammingTeam.matchingService.domain.program.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import webprogrammingTeam.matchingService.domain.Image.entity.Image;
import webprogrammingTeam.matchingService.domain.Image.repository.ImageRepository;
import webprogrammingTeam.matchingService.domain.review.entity.Review;
import webprogrammingTeam.matchingService.domain.program.dto.request.ProgramUpdateRequest;
import webprogrammingTeam.matchingService.domain.member.entity.Member;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Program {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "writing_time")
    private String writingTime;

    @Column(name = "category")
    private Category category;

    @Column(name = "maximum")
    private int maximum;

    @Column(name = "recruitment_start_date")
    private String recruitmentStartDate;

    @Column(name = "recruitment_end_date")
    private String recruitmentEndDate;

    @Column(name = "program_date")
    private String programDate;

    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL)  // Referencing the correct field
    private List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    @Builder
    public Program(Member member, String title, String content, String writingTime, Category category, int maximum, String recruitmentStartDate, String recruitmentEndDate, String programDate) {
        this.member = member;
        this.title = title;
        this.content = content;
        this.writingTime = writingTime;
        this.category = category;
        this.maximum = maximum;
        this.recruitmentStartDate = recruitmentStartDate;
        this.recruitmentEndDate = recruitmentEndDate;
        this.programDate = programDate;
    }

    public void addImageList(Image image) {
        images.add(image);
        image.setProgram(this);
    }



    public void updateProgram(ProgramUpdateRequest programUpdateRequest) {
        this.title = programUpdateRequest.title();
        this.content = programUpdateRequest.content();
        this.category = programUpdateRequest.category();
        this.maximum = programUpdateRequest.maximum();
        this.recruitmentStartDate = programUpdateRequest.recruitmentStartDate();
        this.recruitmentEndDate = programUpdateRequest.recruitmentEndDate();
        this.programDate = programUpdateRequest.programDate();
    }

}