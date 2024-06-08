package webprogrammingTeam.matchingService.domain.program.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import webprogrammingTeam.matchingService.domain.Image.entity.Image;
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

    @Column(name = "recruitment_start_date")
    private String recruitmentStartDate;

    @Column(name = "recruitment_end_date")
    private String recruitmentEndDate;

    @Column(name = "program_date")
    private String programDate;

    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL)  // Referencing the correct field
    @JsonIgnore // JSON 직렬화 과정에서 무시
    private List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Review> reviews = new ArrayList<>();

    @Builder
    public Program(Member member, String title, String content, String writingTime, Category category, String recruitmentStartDate, String recruitmentEndDate, String programDate) {
        this.member = member;
        this.title = title;
        this.content = content;
        this.writingTime = writingTime;
        this.category = category;
        this.recruitmentStartDate = recruitmentStartDate;
        this.recruitmentEndDate = recruitmentEndDate;
        this.programDate = programDate;
    }

    public void addImageList(Image image) {
        images.add(image);
        image.setProgram(this);
    }

    public void addReviewList(Review review) {
        reviews.add(review);
        review.setProgram(this);
    }

    public void updateProgram(ProgramUpdateRequest programUpdateRequest) {
        this.title = programUpdateRequest.title();
        this.content = programUpdateRequest.content();
    }
}
