package webprogrammingTeam.matchingService.domain.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;
import webprogrammingTeam.matchingService.domain.board.entity.Board;
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

    @Column(name="member_name")
    private String memberName;

    @Column(name="member_email")
    private String email;
    @Enumerated(EnumType.STRING)
    @Column(name="role")
    private Role role;

    @JsonIgnore
    @OneToMany(mappedBy = "member")
    private List<Board> board = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "member")
    private List<Review> review = new ArrayList<>();

    @Builder

    public Member(String memberName, Role role, String email){

        this.memberName=memberName;
        this.role=role;
        this.email=email;
    }



}
