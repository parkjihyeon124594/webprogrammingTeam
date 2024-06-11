package webprogrammingTeam.matchingService.domain.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;
import webprogrammingTeam.matchingService.domain.program.entity.Program;
import webprogrammingTeam.matchingService.domain.review.entity.Review;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="memberName")
    private String memberName;

    @Column(name="member_email")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name="role")
    private Role role;

    @Column(name="birth")
    private String birth;

    @Column(name="gener")
    private String gender;

    @Column(name="latitude")
    private Double latitdue;

    @Column(name="longitude")
    private Double longitude;

    @Column(name = "password")
    private String password;

    @JsonIgnore
    @OneToMany(mappedBy = "member")
    private List<Program> board = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "member")
    private List<Review> review = new ArrayList<>();

    @Builder
    public Member(String memberName, Role role, String email,String birth,String gener,String password,Double latitdue,Double longitude){

        this.memberName=memberName;
        this.role=role;
        this.birth =birth;
        this.gender =gener;
        this.password=password;
        this.email=email;
        this.latitdue=latitdue;
        this.longitude=longitude;
    }



}
