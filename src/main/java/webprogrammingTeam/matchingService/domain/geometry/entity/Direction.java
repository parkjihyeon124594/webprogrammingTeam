package webprogrammingTeam.matchingService.domain.geometry.entity;

public enum Direction {
    NORTH(0.0), // 각도 값 예시
    NORTHEAST(45.0),
    EAST(90.0),
    SOUTHEAST(135.0),
    SOUTH(180.0),
    SOUTHWEST(225.0),
    WEST(270.0),
    NORTHWEST(315.0);

    private final double bearing;

    Direction(double bearing) {
        this.bearing = bearing;
    }

    public double getBearing() {
        return bearing;
    }
}
