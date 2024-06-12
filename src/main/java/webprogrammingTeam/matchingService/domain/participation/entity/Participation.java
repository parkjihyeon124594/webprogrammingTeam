package webprogrammingTeam.matchingService.domain.participation.entity;

import jakarta.persistence.*;
import lombok.*;
import webprogrammingTeam.matchingService.domain.program.entity.Program;
import webprogrammingTeam.matchingService.domain.member.entity.Member;
@Entity
@Getter
@NoArgsConstructor
public class Participation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "paticipation_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "program_id", referencedColumnName = "program_id")
    private Program program;

    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "member_id")
    private Member member;

    @Builder
    public Participation(Program program, Member member)
    {
        this.program=program;
        this.member=member;
    }

}
