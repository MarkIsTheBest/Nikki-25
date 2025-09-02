package org.firstinspires.ftc.teamcode.opModes;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathBuilder;
import com.pedropathing.paths.PathChain;
import com.pedropathing.paths.PathConstraints;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class AutoLeft extends OpMode {

    private Follower follower;
    private Timer pathTimer;
    private int pathState;

    public static PathBuilder builder;

    public static PathChain line1 = builder
            .addPath(new BezierLine(new Pose(86.018, 9.770), new Pose(88.100, 22.938)))
            .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(90))
            .build();

    public static PathChain line2 = builder
            .addPath(
                    new BezierCurve(
                            new Pose(88.100, 22.938),
                            new Pose(131.182, 18.978),
                            new Pose(98.250, 49.110)
                    )
            )
            .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(135))
            .build();

    public static PathChain line3 = builder
            .addPath(new BezierLine(new Pose(98.250, 49.110), new Pose(97.230, 48.111)))
            .setLinearHeadingInterpolation(Math.toRadians(135), Math.toRadians(-135))
            .build();

    public static PathChain line4 = builder
            .addPath(new BezierLine(new Pose(97.230, 48.111), new Pose(98.220, 49.110)))
            .setLinearHeadingInterpolation(Math.toRadians(-135), Math.toRadians(135))
            .build();

    public static PathChain line5 = builder
            .addPath(
                    new BezierLine(new Pose(98.220, 49.110), new Pose(113.203, 32.962))
            )
            .setLinearHeadingInterpolation(Math.toRadians(135), Math.toRadians(-45))
            .build();

    @Override
    public void init() {
        builder = new PathBuilder(follower);
    }

    @Override
    public void loop() {

    }
}
