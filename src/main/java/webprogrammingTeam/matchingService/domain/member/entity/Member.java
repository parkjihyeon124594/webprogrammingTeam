package webprogrammingTeam.matchingService.domain.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;

import webprogrammingTeam.matchingService.domain.notification.entity.Notification;
import webprogrammingTeam.matchingService.domain.program.entity.Program;
import webprogrammingTeam.matchingService.domain.recruitment.entity.Recruitment;
import webprogrammingTeam.matchingService.domain.review.entity.Review;
import webprogrammingTeam.matchingService.global.entity.BaseTimeEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="member_id")
    private Long id;

    @Column(name="memberName")
    private String memberName;

    @Column(name="member_email")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name="role")
    private Role role;

    @Column(name="age")
    private int age;

    @Column(name="gender")
    private String gender;

    @Column(name="latitude")
    private Double latitude;

    @Column(name="longitude")
    private Double longitude;


    @Column(name = "password")
    private String password;

    @JsonIgnore
    @OneToMany(mappedBy = "member")
    private List<Program> program = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "member")
    private List<Review> review = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Recruitment> recruitments = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "member")
    private List<Notification> notifications = new ArrayList<>();

    @Builder
    public Member(String memberName, Role role, String email,int age,String gender,String password,Double latitude,Double longitude){


        this.memberName=memberName;
        this.role=role;
        this.age =age;
        this.gender =gender;
        this.password=password;
        this.email=email;
        this.latitude=latitude;
        this.longitude=longitude;
    }

    public void setIdForTest(Long id) {
        this.id = id;
    }
}
