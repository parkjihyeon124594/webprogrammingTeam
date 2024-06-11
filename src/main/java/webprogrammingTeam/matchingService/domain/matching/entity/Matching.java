package webprogrammingTeam.matchingService.domain.matching.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import webprogrammingTeam.matchingService.domain.program.entity.Program;
import webprogrammingTeam.matchingService.domain.member.entity.Member;
import webprogrammingTeam.matchingService.global.entity.BaseTimeEntity;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Matching extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "program_member_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "program_id", referencedColumnName = "program_id")
    private Program program;

    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "member_id")
    private Member member;

}
