package webprogrammingTeam.matchingService.domain.matching.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import webprogrammingTeam.matchingService.domain.program.entity.Program;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Matching {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;





}
