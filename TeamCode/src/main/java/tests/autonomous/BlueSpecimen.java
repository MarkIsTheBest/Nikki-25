package tests.autonomous;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;
import com.pedropathing.util.Constants;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;
import subsystems.Actions;
import subsystems.Positions;
import subsystems.hardware.Motors;
import subsystems.hardware.Servos;

@Autonomous(name = "Blue Specimen", group = "Examples")
public class BlueSpecimen extends OpMode {

    private Follower follower;
    private Timer pathTimer, opmodeTimer;
    private int pathState;
    private int lastPathState;

    private final Pose startPose = new Pose(7.900, 66.400, Math.toRadians(180));
    private final Pose scorePreloadPose = new Pose(31.600, 66.300, Math.toRadians(180));
    private final Pose scorePose = new Pose(39.600, 66.300, Math.toRadians(180));
    private final Pose pickupSample1Pose = new Pose(25.300, 35.800, Math.toRadians(-40));
    private final Pose midPose2 = new Pose(25.400, 22.300, Math.toRadians(-40));
    private final Pose midPose3 = new Pose(41.200, 22.700, Math.toRadians(-75));
    private final Pose finalPose = new Pose(37.800, 81.900, Math.toRadians(180));
    private final Pose pickupSpecimenPose = new Pose( 30.200, 28.500, Math.toRadians(180));

    private PathChain scorePreload, pickupSample1, human1, pickupSample2, human2, pickupSample3,
            human3, pickupSpecimen1, score1, pickupSpecimen2, score2, pickupSpecimen3, score3,
            pickupSpecimen4, score4, park;

    private final Telemetry debug = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

    public void buildPaths() {
        scorePreload = follower.pathBuilder()
                .addPath(new BezierLine(new Point(startPose), new Point(scorePreloadPose)))
                .setConstantHeadingInterpolation(startPose.getHeading())
                .build();

        pickupSample1 = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(scorePreloadPose), new Point(13.600, 43.900, Point.CARTESIAN), new Point(pickupSample1Pose)))
                .setLinearHeadingInterpolation(startPose.getHeading(), pickupSample1Pose.getHeading())
                .build();

        human1 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(pickupSample1Pose), new Point(25.000, 35.500, Point.CARTESIAN)))
                .setLinearHeadingInterpolation(pickupSample1Pose.getHeading(), Math.toRadians(-135))
                .build();

        pickupSample2 = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(25.000, 35.500, Point.CARTESIAN),
                        new Point(18.000, 28.900, Point.CARTESIAN),
                        new Point(midPose2)))
                .setLinearHeadingInterpolation(Math.toRadians(-135), midPose2.getHeading())
                .build();

        human2 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(midPose2), new Point(midPose2.getX() + 1, midPose2.getY() + 1)))
                .setLinearHeadingInterpolation(midPose2.getHeading(), Math.toRadians(-135))
                .build();

        pickupSample3 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(midPose2.getX() + 1, midPose2.getY() + 1),
                        new Point(midPose2.getX() + 0.5, midPose2.getY() - 1.5)))

                .setLinearHeadingInterpolation(Math.toRadians(-135), Math.toRadians(-55))
                .build();

        human3 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(midPose2.getX() + 0.5, midPose2.getY() - 1.5), new Point(31.600, 21.500, Point.CARTESIAN)))
                .setLinearHeadingInterpolation(Math.toRadians(-55), Math.toRadians(-165))
                .build();

        pickupSpecimen1 = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(31.600, 21.500, Point.CARTESIAN), new Point(35.200, 34.100, Point.CARTESIAN), new Point(pickupSpecimenPose)))
                .setLinearHeadingInterpolation(Math.toRadians(-165), Math.toRadians(180))
                .build();

        score1 = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(pickupSpecimenPose), new Point(27.500, 45.100, Point.CARTESIAN), new Point(scorePose)))
                .setConstantHeadingInterpolation(Math.toRadians(180))
                .build();

        pickupSpecimen2 = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(scorePose), new Point(27.800, 46.500, Point.CARTESIAN), new Point(pickupSpecimenPose)))
                .setConstantHeadingInterpolation(Math.toRadians(180))
                .build();

        score2 = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(pickupSpecimenPose), new Point(27.500, 45.100, Point.CARTESIAN), new Point(scorePose)))
                .setConstantHeadingInterpolation(Math.toRadians(180))
                .build();

        pickupSpecimen3 = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(scorePose), new Point(27.800, 46.500, Point.CARTESIAN), new Point(pickupSpecimenPose)))
                .setConstantHeadingInterpolation(Math.toRadians(180))
                .build();

        score3 = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(pickupSpecimenPose), new Point(27.500, 45.100, Point.CARTESIAN), new Point(scorePose)))
                .setConstantHeadingInterpolation(Math.toRadians(180))
                .build();

        pickupSpecimen4 = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(scorePose), new Point(27.800, 46.500, Point.CARTESIAN), new Point(pickupSpecimenPose)))
                .setConstantHeadingInterpolation(Math.toRadians(180))
                .build();

        score4 = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(pickupSpecimenPose), new Point(27.500, 45.100, Point.CARTESIAN), new Point(scorePose)))
                .setConstantHeadingInterpolation(Math.toRadians(180))
                .build();

        park = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(scorePose), new Point(33.800, 63.100, Point.CARTESIAN), new Point(20.700, 48.300, Point.CARTESIAN)))
                .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(-120))
                .build();
    }

    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                Actions.raiseVertical();
                Actions.leaveSpecimenRank();
                follower.setMaxPower(0.6);
                follower.followPath(scorePreload);

                if (lastPathState != pathState) pathTimer.resetTimer();
                lastPathState = pathState;
                pathTimer.resetTimer();
                setPathState(1);

                break;

            case 1:
                if (pathTimer.getElapsedTimeSeconds() < 1) return;
                Actions.lowerVertical();
                Actions.setAxisStraight();
                if (pathTimer.getElapsedTimeSeconds() < 1 + 0.25) return;
                follower.setMaxPower(1);
                Actions.openBackClaw(true);
                if (pathTimer.getElapsedTimeSeconds() < 1 + 0.25 + 0.5) return;
                follower.followPath(pickupSample1, true);

                if (lastPathState != pathState) pathTimer.resetTimer();
                lastPathState = pathState;
                setPathState(2);

                break;

            case 2:
                if (pathTimer.getElapsedTimeSeconds() < 1.25) return;
                Actions.prepareSample1();
                if (pathTimer.getElapsedTimeSeconds() < 1.25 + 1.25) return;
                Actions.pickupSampleAuto();
                if (pathTimer.getElapsedTimeSeconds() < 1.25 + 1.25 + 0.25) return;
                Actions.openFrontClaw(false);
                if (pathTimer.getElapsedTimeSeconds() < 1.25 + 1.25 + 0.25 + 0.25) return;
                Actions.endSampleAuto();
                if (pathTimer.getElapsedTimeSeconds() < 1.25 + 1.25 + 0.25 + 0.25 + 0.25) return;
                follower.followPath(human1, true);

                if (lastPathState != pathState) pathTimer.resetTimer();
                lastPathState = pathState;
                setPathState(3);

                break;

            case 3:
                if (pathTimer.getElapsedTimeSeconds() < 1) return;
                Actions.leaveObsvAuto();
                if (pathTimer.getElapsedTimeSeconds() < 1 + 0.5) return;
                Actions.openFrontClaw(true);
                if (pathTimer.getElapsedTimeSeconds() < 1 + 0.5 + 0.5) return;
                Actions.extendLinkages(subsystems.Constants.LINKAGE.CLOSED);
                if (pathTimer.getElapsedTimeSeconds() < 1 + 0.5 + 0.5 + 0.5) return;
                Actions.prepareSample2();
                follower.followPath(pickupSample2, true);

                if (lastPathState != pathState) pathTimer.resetTimer();
                lastPathState = pathState;
                setPathState(4);

                break;

            case 4:
                if (pathTimer.getElapsedTimeSeconds() < 1.5) return;
                Actions.pickupSampleAuto();
                if (pathTimer.getElapsedTimeSeconds() < 1.5 + 0.5) return;
                Actions.openFrontClaw(false);
                if (pathTimer.getElapsedTimeSeconds() < 1.5 + 0.5 + 0.25) return;
                Actions.endSampleAuto();
                follower.followPath(human2, true);
                if (lastPathState != pathState) pathTimer.resetTimer();
                lastPathState = pathState;
                setPathState(5);

                break;

            case 5:
                if (pathTimer.getElapsedTimeSeconds() < 1) return;
                Actions.leaveObsvAuto();
                if (pathTimer.getElapsedTimeSeconds() < 1 + 0.5) return;
                Actions.openFrontClaw(true);
                if (pathTimer.getElapsedTimeSeconds() < 1 + 0.5 + 0.5) return;
                Actions.extendLinkages(subsystems.Constants.LINKAGE.CLOSED);
                if (pathTimer.getElapsedTimeSeconds() < 1 + 0.5 + 0.5 + 0.5) return;
                Actions.prepareSample3();
                follower.followPath(pickupSample3, true);

                if (lastPathState != pathState) pathTimer.resetTimer();
                lastPathState = pathState;
                setPathState(6);

                break;

            case 6:
                if (pathTimer.getElapsedTimeSeconds() < 0.5) return;
                Actions.extendLinkages(0.5);
                if (pathTimer.getElapsedTimeSeconds() < 0.5 + 0.5) return;
                Actions.pickupSampleAuto();
                if (pathTimer.getElapsedTimeSeconds() < 0.5 + 0.5 + 0.25) return;
                Actions.openFrontClaw(false);
                if (pathTimer.getElapsedTimeSeconds() < 0.5 + 0.5 + 1 + 0.25 + 0.1) return;
                Actions.endSampleAuto();
                if (pathTimer.getElapsedTimeSeconds() < 0.5 + 0.5 + 1 + 0.25 + 0.1 + 0.1) return;
                follower.followPath(human3, true);

                if (lastPathState != pathState) pathTimer.resetTimer();
                lastPathState = pathState;
                setPathState(7);

                break;

            case 7:
                if (pathTimer.getElapsedTimeSeconds() < 1) return;
                Actions.leaveObsvAuto();
                if (pathTimer.getElapsedTimeSeconds() < 1 + 0.5) return;
                Actions.openFrontClaw(true);
                if (pathTimer.getElapsedTimeSeconds() < 1 + 0.5 + 0.25) return;
                Actions.prepareSpecimen();
                Actions.extendLinkages(subsystems.Constants.LINKAGE.CLOSED);
                follower.followPath(pickupSpecimen1, true);

                if (lastPathState != pathState) pathTimer.resetTimer();
                lastPathState = pathState;
                setPathState(8);

                break;

            case 8:
                if (pathTimer.getElapsedTimeSeconds() < 2) return;
                Actions.extendLinkages(subsystems.Constants.LINKAGE.OPENED);
                if (pathTimer.getElapsedTimeSeconds() < 2 + 1.25) return;
                Actions.openFrontClaw(false);
                if (pathTimer.getElapsedTimeSeconds() < 2 + 1.25 + 0.25) return;
                follower.followPath(score1, true);

                if (lastPathState != pathState) pathTimer.resetTimer();
                lastPathState = pathState;
                setPathState(9);

                break;

            case 9:
                if (pathTimer.getElapsedTimeSeconds() < 0.25) return;
                Actions.pickupSpecimen();
                if (pathTimer.getElapsedTimeSeconds() < 0.25 + 1) return;
                Actions.prepareTransfer();
                if (pathTimer.getElapsedTimeSeconds() < 0.25 + 1 + 1) return;
                Actions.openBackClaw(false);
                if (pathTimer.getElapsedTimeSeconds() < 0.25 + 1 + 1 + 0.25) return;
                Actions.openFrontClaw(true);
                if (pathTimer.getElapsedTimeSeconds() < 0.25 + 1 + 1 + 0.25 + 0.25) return;
                Actions.raiseVertical();
                if (pathTimer.getElapsedTimeSeconds() < 0.25 + 1 + 1 + 0.25 + 0.25 + 0.5) return;
                Actions.leaveSpecimenRankScoring();
                if (pathTimer.getElapsedTimeSeconds() < 0.25 + 1 + 1 + 0.25 + 0.25 + 0.5 + 1)
                    return;
                Actions.lowerVertical();
                if (pathTimer.getElapsedTimeSeconds() < 0.25 + 1 + 1 + 0.25 + 0.25 + 0.5 + 1 + 0.5)
                    return;
                Actions.openBackClaw(true);
                Actions.prepareSpecimen();

                follower.followPath(pickupSpecimen2, true);
                if (lastPathState != pathState) pathTimer.resetTimer();
                lastPathState = pathState;
                setPathState(10);

                break;

            case 10:
                if (pathTimer.getElapsedTimeSeconds() < 2) return;
                Actions.extendLinkages(subsystems.Constants.LINKAGE.OPENED);
                if (pathTimer.getElapsedTimeSeconds() < 2 + 1.25) return;
                Actions.openFrontClaw(false);
                if (pathTimer.getElapsedTimeSeconds() < 2 + 1.25 + 0.25) return;
                follower.followPath(score2, true);

                if (lastPathState != pathState) pathTimer.resetTimer();
                lastPathState = pathState;
                setPathState(11);

                break;

            case 11:
                if (pathTimer.getElapsedTimeSeconds() < 0.25) return;
                Actions.pickupSpecimen();
                if (pathTimer.getElapsedTimeSeconds() < 0.25 + 1) return;
                Actions.prepareTransfer();
                if (pathTimer.getElapsedTimeSeconds() < 0.25 + 1 + 1) return;
                Actions.openBackClaw(false);
                if (pathTimer.getElapsedTimeSeconds() < 0.25 + 1 + 1 + 0.25) return;
                Actions.openFrontClaw(true);
                if (pathTimer.getElapsedTimeSeconds() < 0.25 + 1 + 1 + 0.25 + 0.25) return;
                Actions.raiseVertical();
                if (pathTimer.getElapsedTimeSeconds() < 0.25 + 1 + 1 + 0.25 + 0.25 + 0.5) return;
                Actions.leaveSpecimenRankScoring();
                if (pathTimer.getElapsedTimeSeconds() < 0.25 + 1 + 1 + 0.25 + 0.25 + 0.5 + 1)
                    return;
                Actions.lowerVertical();
                if (pathTimer.getElapsedTimeSeconds() < 0.25 + 1 + 1 + 0.25 + 0.25 + 0.5 + 1 + 0.5)
                    return;
                Actions.openBackClaw(true);
                Actions.prepareSpecimen();

                follower.followPath(pickupSpecimen3, true);

                if (lastPathState != pathState) pathTimer.resetTimer();
                lastPathState = pathState;
                setPathState(12);

                break;

            case 12:
                if (pathTimer.getElapsedTimeSeconds() < 2) return;
                Actions.extendLinkages(subsystems.Constants.LINKAGE.OPENED);
                if (pathTimer.getElapsedTimeSeconds() < 2 + 1.25) return;
                Actions.openFrontClaw(false);
                if (pathTimer.getElapsedTimeSeconds() < 2 + 1.25 + 0.25) return;
                follower.followPath(score3, true);

                if (lastPathState != pathState) pathTimer.resetTimer();
                lastPathState = pathState;
                setPathState(13);

                break;

            case 13:
                if (pathTimer.getElapsedTimeSeconds() < 0.25) return;
                Actions.pickupSpecimen();
                if (pathTimer.getElapsedTimeSeconds() < 0.25 + 1) return;
                Actions.prepareTransfer();
                if (pathTimer.getElapsedTimeSeconds() < 0.25 + 1 + 1) return;
                Actions.openBackClaw(false);
                if (pathTimer.getElapsedTimeSeconds() < 0.25 + 1 + 1 + 0.25) return;
                Actions.openFrontClaw(true);
                if (pathTimer.getElapsedTimeSeconds() < 0.25 + 1 + 1 + 0.25 + 0.25) return;
                Actions.raiseVertical();
                if (pathTimer.getElapsedTimeSeconds() < 0.25 + 1 + 1 + 0.25 + 0.25 + 0.5) return;
                Actions.leaveSpecimenRankScoring();
                if (pathTimer.getElapsedTimeSeconds() < 0.25 + 1 + 1 + 0.25 + 0.25 + 0.5 + 1)
                    return;
                Actions.lowerVertical();
                if (pathTimer.getElapsedTimeSeconds() < 0.25 + 1 + 1 + 0.25 + 0.25 + 0.5 + 1 + 0.5)
                    return;
                Actions.openBackClaw(true);
                Actions.prepareSpecimen();

                follower.followPath(pickupSpecimen4, true);

                if (lastPathState != pathState) pathTimer.resetTimer();
                lastPathState = pathState;
                setPathState(14);

                break;

            case 14:
                if (pathTimer.getElapsedTimeSeconds() < 2) return;
                Actions.extendLinkages(subsystems.Constants.LINKAGE.OPENED);
                if (pathTimer.getElapsedTimeSeconds() < 2 + 1.25) return;
                Actions.openFrontClaw(false);
                if (pathTimer.getElapsedTimeSeconds() < 2 + 1.25 + 0.25) return;
                follower.followPath(score4, true);

                if (lastPathState != pathState) pathTimer.resetTimer();
                lastPathState = pathState;
                setPathState(15);

                break;

            case 15:
                if (pathTimer.getElapsedTimeSeconds() < 0.25) return;
                Actions.pickupSpecimen();
                if (pathTimer.getElapsedTimeSeconds() < 0.25 + 1) return;
                Actions.prepareTransfer();
                if (pathTimer.getElapsedTimeSeconds() < 0.25 + 1 + 1) return;
                Actions.openBackClaw(false);
                if (pathTimer.getElapsedTimeSeconds() < 0.25 + 1 + 1 + 0.25) return;
                Actions.openFrontClaw(true);
                if (pathTimer.getElapsedTimeSeconds() < 0.25 + 1 + 1 + 0.25 + 0.25) return;
                Actions.raiseVertical();
                if (pathTimer.getElapsedTimeSeconds() < 0.25 + 1 + 1 + 0.25 + 0.25 + 0.5) return;
                Actions.leaveSpecimenRankScoring();
                if (pathTimer.getElapsedTimeSeconds() < 0.25 + 1 + 1 + 0.25 + 0.25 + 0.5 + 1)
                    return;
                Actions.lowerVertical();
                if (pathTimer.getElapsedTimeSeconds() < 0.25 + 1 + 1 + 0.25 + 0.25 + 0.5 + 1 + 0.5)
                    return;
                Actions.openBackClaw(true);
                Actions.prepareSpecimen();

                if (pathTimer.getElapsedTimeSeconds() < 10) return;
                follower.followPath(park, true);

                if (lastPathState != pathState) pathTimer.resetTimer();
                lastPathState = pathState;
                setPathState(-1);

                break;
        }
    }

    public void setPathState(int pState) {
        pathState = pState;
    }

    @Override
    public void loop() {
        follower.update();
        autonomousPathUpdate();

        Positions.update();

        Pose currentPose = follower.getPose();
        debug.addData("isBusy", follower.isBusy());
        debug.addData("test1", follower.getCurrentPath().getClosestPoint(follower.getPose(), 1));
        debug.addData("test2", String.valueOf(follower.getCurrentPath().getEndTangent()));
        debug.addData("currentPathState", pathState);
        debug.addData("lastPathState", lastPathState);
        debug.addData("timer", pathTimer.getElapsedTimeSeconds());
        if (currentPose != null) {
            debug.addData("x", currentPose.getX());
            debug.addData("y", currentPose.getY());
            debug.addData("heading", Math.toDegrees(currentPose.getHeading()));
        }
        debug.update();
    }

    @Override
    public void init() {
        Servos.init(hardwareMap);
        Motors.init(hardwareMap);
        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();

        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        follower.setStartingPose(startPose);
        Actions.init();
        Actions.openBackClaw(false);
        Positions.update();
        buildPaths();
    }

    @Override
    public void start() {
        opmodeTimer.resetTimer();
        setPathState(0);
    }
}