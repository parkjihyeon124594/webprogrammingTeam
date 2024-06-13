package webprogrammingTeam.matchingService.domain.recruitment.entity;

import jakarta.persistence.*;
import lombok.*;
import webprogrammingTeam.matchingService.domain.member.entity.Member;
import webprogrammingTeam.matchingService.domain.program.entity.Program;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Recruitment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recruitment_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "program_id", referencedColumnName = "program_id")
    private Program program;

    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "member_id")
    private Member member;

    @Builder
    public Recruitment(Program program, Member member)
    {
        this.program=program;
        this.member=member;
    }

}
