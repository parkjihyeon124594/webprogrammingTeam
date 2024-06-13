package webprogrammingTeam.matchingService.domain.geometry.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webprogrammingTeam.matchingService.domain.Image.repository.ImageRepository;
import webprogrammingTeam.matchingService.domain.geometry.dto.response.GeometryResponse;
import webprogrammingTeam.matchingService.domain.geometry.entity.Direction;
import webprogrammingTeam.matchingService.domain.geometry.entity.Location;
import webprogrammingTeam.matchingService.domain.geometry.util.GeometryUtil;
import webprogrammingTeam.matchingService.domain.program.entity.Program;
import webprogrammingTeam.matchingService.domain.program.repository.ProgramRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeometryService {

    private final ProgramRepository programRepository;
    private final ImageRepository imageRepository;

    @Transactional(readOnly = true)
    public List<Program> findProgramsNearMember(Double memberLatitude, Double memberLongitude) {
        // 5km 반경 내에 있는 프로그램들을 찾기 위한 위도와 경도의 범위 계산
        Double distanceKm = 5.0; // 반경 5km

        // 사용자 위치를 중심으로 반경 5km의 경계 상자를 계산합니다.
        double radius = 5.0; // 반경 5km
        Location north = GeometryUtil.calculate(memberLatitude, memberLongitude, radius, Direction.NORTH.getBearing());
        Location south = GeometryUtil.calculate(memberLatitude, memberLongitude, radius, Direction.SOUTH.getBearing());
        Location east = GeometryUtil.calculate(memberLatitude, memberLongitude, radius, Direction.EAST.getBearing());
        Location west = GeometryUtil.calculate(memberLatitude, memberLongitude, radius, Direction.WEST.getBearing());

        // 경계 상자의 경계 좌표를 얻습니다.
        double minLat = south.getLatitude();
        double maxLat = north.getLatitude();
        double minLon = west.getLongitude();
        double maxLon = east.getLongitude();

        // 경계 상자 내에 있는 프로그램들을 조회합니다.
        List<Program> programs = programRepository.findByLatitudeBetweenAndLongitudeBetween(minLat, maxLat, minLon, maxLon);

        return programs;
    }

    public List<GeometryResponse> programToGeometryResponse(List<Program> programs){

        return programs.stream()
                        .map(program -> new GeometryResponse(
                                program.getId(),
                                program.getLatitude(),
                                program.getLongitude(),
                                imageRepository.findFirstImageByProgram(program.getId()).getUrl()
                        ))
                        .toList();

    }



}
