package org.firstinspires.ftc.teamcode.helper.control;

import java.util.Map;
import java.util.TreeMap;

public class ShooterLookupTable {

    // Helper class to hold our output data
    public static class ShooterState {
        public final double rpm;
        public final double angle;

        public ShooterState(double rpm, double angle) {
            this.rpm = rpm;
            this.angle = angle;
        }
    }

    // TreeMap automatically sorts keys (distances) for us
    private final TreeMap<Double, ShooterState> table = new TreeMap<>();

    /**
     * Add a tuning point to the table.
     * @param distance Distance from target (in same units as you measure in TeleOp)
     * @param rpm Tuned flywheel RPM
     * @param angle Tuned hood angle
     */
    public void add(double distance, double rpm, double angle) {
        table.put(distance, new ShooterState(rpm, angle));
    }

    /**
     * Get the interpolated RPM and Angle for a specific distance.
     * @param distance Current distance to target
     * @return ShooterState containing calculated RPM and Angle
     */
    public ShooterState get(double distance) {
        if (table.isEmpty()) {
            return new ShooterState(0, 45); // Default safety
        }

        // Find the closest tuned distance below the current distance
        Map.Entry<Double, ShooterState> floor = table.floorEntry(distance);
        // Find the closest tuned distance above the current distance
        Map.Entry<Double, ShooterState> ceiling = table.ceilingEntry(distance);

        // Edge Case: Distance is smaller than our smallest data point
        if (floor == null) return ceiling.getValue();
        // Edge Case: Distance is larger than our largest data point
        if (ceiling == null) return floor.getValue();
        // Edge Case: Exact match
        if (floor.getKey().equals(ceiling.getKey())) return floor.getValue();

        // Linear Interpolation
        double lowerDist = floor.getKey();
        double upperDist = ceiling.getKey();

        // Calculate the percentage (t) of where we are between the two points (0.0 to 1.0)
        double t = (distance - lowerDist) / (upperDist - lowerDist);

        // Interpolate RPM
        double finalRpm = lerp(floor.getValue().rpm, ceiling.getValue().rpm, t);
        // Interpolate Angle
        double finalAngle = lerp(floor.getValue().angle, ceiling.getValue().angle, t);

        return new ShooterState(finalRpm, finalAngle);
    }

    // Standard Linear Interpolation formula: a + (b - a) * t
    private double lerp(double start, double end, double t) {
        return start + (end - start) * t;
    }
}