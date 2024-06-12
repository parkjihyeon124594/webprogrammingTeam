package webprogrammingTeam.matchingService.domain.program.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import webprogrammingTeam.matchingService.domain.Image.entity.Image;
import webprogrammingTeam.matchingService.domain.Image.repository.ImageRepository;
import webprogrammingTeam.matchingService.domain.participation.entity.Participation;
import webprogrammingTeam.matchingService.domain.recruitment.entity.Recruitment;
import webprogrammingTeam.matchingService.domain.review.entity.Review;
import webprogrammingTeam.matchingService.domain.program.dto.request.ProgramUpdateRequest;

import webprogrammingTeam.matchingService.domain.member.entity.Member;
import webprogrammingTeam.matchingService.global.entity.BaseTimeEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Program extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="program_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

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

    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL)
    private List<Participation> participations = new ArrayList<>();

    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL)
    private List<Recruitment> recruitments = new ArrayList<>();

    @Builder
    public Program(Member member, String title, String content, Category category, int maximum, String recruitmentStartDate, String recruitmentEndDate, String programDate) {
        this.member = member;
        this.title = title;
        this.content = content;
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
