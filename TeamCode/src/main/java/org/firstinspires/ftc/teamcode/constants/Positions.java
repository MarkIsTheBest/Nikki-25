package org.firstinspires.ftc.teamcode.constants;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.geometry.Pose;

public class Positions {

    @Configurable
    public static class Field {
        public static final Pose RED_GOAL = new Pose(132,137);
        public static final Pose BLUE_GOAL = RED_GOAL.mirror();

        public static final Pose RED_BASE = new Pose(38.5, 33.5, Math.toRadians(90));
        public static final Pose BLUE_BASE = RED_BASE.mirror();
    }

    @Configurable
    public static class Motor {

    }

    @Configurable
    public static class Servo {
        public final static double MIN_HOOD = 0.2;
        public final static double MAX_HOOD = 0.625;

        public static final double BARRIER_CLOSED = 0.5;
        public static final double BARRIER_OPENED = 0.1;
    }
}