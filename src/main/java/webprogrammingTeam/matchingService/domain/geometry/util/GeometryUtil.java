package webprogrammingTeam.matchingService.domain.geometry.util;

import webprogrammingTeam.matchingService.domain.geometry.entity.Location;

public class GeometryUtil {

    // 특정 위치에서 일정 거리와 특정 방향으로 이동했을 때의 새로운 위치를 계산
    public static Location calculate(Double baselatitude,Double baseLongitude,Double distance,Double bearing){

        Double radianLatitude = toRadian(baselatitude);
        Double radianLognitude = toRadian(baseLongitude);
        Double radianAngle = toRadian(bearing);
        Double distanaceRadius = distance / 6371.01;

        Double latitude = Math.asin(sin(radianLatitude) * cos(distanaceRadius))
                +cos(radianLatitude) * sin(distanaceRadius) * cos(radianAngle);

        Double longitude = radianLognitude + Math.atan2(sin(radianAngle) * sin(distanaceRadius) *
                cos(radianLatitude), cos(distanaceRadius) - sin(radianLatitude)*sin(latitude));

        longitude = normalizeLongitude(longitude);
        return new Location(toDegree(latitude),toDegree(longitude));
    }

    private static Double toRadian(Double coordinate) {
        return coordinate * Math.PI / 180.0;
    }

    private static Double toDegree(Double coordinate) {
        return coordinate * 180.0 / Math.PI;
    }

    private static Double sin(Double coordinate) {
        return Math.sin(coordinate);
    }

    private static Double cos(Double coordinate) {
        return Math.cos(coordinate);
    }

    private static Double normalizeLongitude(Double longitude) {
        return (longitude + 540) % 360 - 180;
    }
}
