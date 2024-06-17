package webprogrammingTeam.matchingService.domain.program.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import webprogrammingTeam.matchingService.domain.Image.entity.Image;
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
public class Program extends BaseTimeEntity{

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
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(name = "maximum")
    private int maximum;

    @Column(name = "recruitment_start_date")
    private String recruitmentStartDate;

    @Column(name = "recruitment_end_date")
    private String recruitmentEndDate;

    @Column(name = "program_date")
    private String programDate;

    @Column(name = "open")
    @Enumerated(EnumType.STRING)
    private Open open;

    @Column(name="latitude")
    private Double latitude;

    @Column(name="longitude")
    private Double longitude;

    @Column(name = "program_address")
    private String programAddress;

    @Column(name = "recruitment")
    private int recruitment;

    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL)  // Referencing the correct field
    private List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL)
    private List<Recruitment> recruitments = new ArrayList<>();

    @Builder
    public Program(Member member, String title, String content, Category category, int maximum, String recruitmentStartDate, String recruitmentEndDate, String programDate, Open open,Double latitude,Double longitude, int recruitment, String programAddress) {
        this.member = member;
        this.title = title;
        this.content = content;
        this.category = category;
        this.maximum = maximum;
        this.recruitmentStartDate = recruitmentStartDate;
        this.recruitmentEndDate = recruitmentEndDate;
        this.programDate = programDate;
        this.open = open;
        this.latitude=latitude;
        this.longitude=longitude;
        this.recruitment = recruitment;
        this.programAddress = programAddress;
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
        this.open = programUpdateRequest.open();
        this.longitude = programUpdateRequest.longitude();
        this.latitude = programUpdateRequest.latitude();
        this.programAddress = programUpdateRequest.programAddress();

    }

    public void updateOpen(Open open){
        this.open=open;
    }
    public void increaseRecruitment(){ this.recruitment++;}
    public void decreaseRecruitment(){this.recruitment--;}
}
