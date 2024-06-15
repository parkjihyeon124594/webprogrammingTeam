package webprogrammingTeam.matchingService.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import webprogrammingTeam.matchingService.domain.program.dto.response.CategoryAgeGroupListResponse;
import webprogrammingTeam.matchingService.domain.program.dto.response.MonthlyCategoryCountResponse;
import webprogrammingTeam.matchingService.domain.program.entity.Category;
import webprogrammingTeam.matchingService.domain.program.entity.Program;
import webprogrammingTeam.matchingService.domain.program.repository.ProgramRepository;
import webprogrammingTeam.matchingService.domain.program.service.ProgramService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
@Slf4j
public class testcontroller {

    private final ProgramService programService;
    private final ProgramRepository programRepository;

    @GetMapping
    public List<MonthlyCategoryCountResponse> test() {
        return programService.getMonthlyCategoryCounts();
    }
}
