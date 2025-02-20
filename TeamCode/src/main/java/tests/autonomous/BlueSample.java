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

@Autonomous(name = "Blue Sample", group = "Autonomous")
public class BlueSample extends OpMode {

    private Follower follower;
    private Timer pathTimer, opmodeTimer;
    private int pathState;
    private int lastPathState;

    private final Pose startPose = new Pose(8.761, 105.292, Math.toRadians(0));
    private final Pose scorePose = new Pose(15.451, 129.027, Math.toRadians(-45));
    private final Pose pickup1Pose = new Pose(21.345, 126.796, Math.toRadians(-21));
    private final Pose pickup2Pose = new Pose(24.372, 131.894, Math.toRadians(0));
    private final Pose pickup3Pose = new Pose(45.398, 124.726, Math.toRadians(90));
    private final Pose level1Pose = new Pose(59.894, 95.575, Math.toRadians(-90));

    private Path scorePreload, level1Ascend;
    private PathChain grabPickup1, grabPickup2, grabPickup3, scorePickup1, scorePickup2, scorePickup3;
    private final Telemetry debug = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

    public void buildPaths() {
        scorePreload = new Path(new BezierCurve(new Point(startPose), new Point(31.221, 116.761, Point.CARTESIAN), new Point(scorePose)));
        scorePreload.setLinearHeadingInterpolation(startPose.getHeading(), scorePose.getHeading());

        grabPickup1 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(scorePose), new Point(pickup1Pose)))
                .setLinearHeadingInterpolation(scorePose.getHeading(), pickup1Pose.getHeading())
                .build();

        scorePickup1 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(pickup1Pose), new Point(scorePose)))
                .setLinearHeadingInterpolation(pickup1Pose.getHeading(), scorePose.getHeading())
                .build();

        grabPickup2 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(scorePose), new Point(pickup2Pose)))
                .setLinearHeadingInterpolation(scorePose.getHeading(), pickup2Pose.getHeading())
                .build();

        scorePickup2 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(pickup2Pose), new Point(scorePose)))
                .setLinearHeadingInterpolation(pickup2Pose.getHeading(), scorePose.getHeading())
                .build();

        grabPickup3 = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(scorePose), new Point(27.876, 118.513, Point.CARTESIAN), new Point(pickup3Pose)))
                .setLinearHeadingInterpolation(scorePose.getHeading(), pickup3Pose.getHeading())
                .build();

        scorePickup3 = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(pickup3Pose), new Point(31.062, 117.717, Point.CARTESIAN), new Point(scorePose)))
                .setLinearHeadingInterpolation(pickup3Pose.getHeading(), scorePose.getHeading())
                .build();

        level1Ascend = new Path(new BezierCurve(new Point(scorePose), new Point(63.717, 121.062, Point.CARTESIAN), new Point(level1Pose)));
        level1Ascend.setLinearHeadingInterpolation(scorePose.getHeading(), level1Pose.getHeading());
    }

    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                Actions.leaveSampleBasket();
                follower.followPath(scorePreload);

                if (follower.isBusy()) return;
                if(lastPathState != pathState) pathTimer.resetTimer();
                lastPathState = pathState;
                setPathState(1);

                break;

            case 1:
                Actions.openBackClaw(true);

                if(pathTimer.getElapsedTimeSeconds() <= 0.2) return;
                follower.followPath(grabPickup1, true);

                if(pathTimer.getElapsedTimeSeconds() <= 0.4) return;
                Actions.prepareSample();
                Actions.extendLinkages(subsystems.Constants.LINKAGE.OPENED);

                if(follower.isBusy()) return;
                if(lastPathState != pathState) pathTimer.resetTimer();
                lastPathState = pathState;
                setPathState(2);

                break;

            case 2:
                executePickupRoutine();

                follower.followPath(scorePickup1, true);

                if (follower.isBusy()) return;
                if (lastPathState != pathState) pathTimer.resetTimer();
                lastPathState = pathState;
                setPathState(3);

                break;

            case 3:
                Actions.openBackClaw(true);

                if(pathTimer.getElapsedTimeSeconds() <= 0.2) return;
                follower.followPath(grabPickup2, true);

                if(pathTimer.getElapsedTimeSeconds() <= 0.4) return;
                Actions.prepareSample();
                Actions.extendLinkages(subsystems.Constants.LINKAGE.OPENED);

                if(follower.isBusy()) return;
                if(lastPathState != pathState) pathTimer.resetTimer();
                lastPathState = pathState;
                setPathState(4);

                break;

            case 4:
                executePickupRoutine();

                follower.followPath(scorePickup2, true);

                if (follower.isBusy()) return;
                if (lastPathState != pathState) pathTimer.resetTimer();
                lastPathState = pathState;
                setPathState(5);

                break;

            case 5:
                Actions.openBackClaw(true);

                if(pathTimer.getElapsedTimeSeconds() <= 0.2) return;
                follower.followPath(grabPickup3, true);

                if(pathTimer.getElapsedTimeSeconds() <= 0.4) return;
                Actions.prepareSample();
                Actions.extendLinkages(subsystems.Constants.LINKAGE.OPENED);

                if(follower.isBusy()) return;
                if(lastPathState != pathState) pathTimer.resetTimer();
                lastPathState = pathState;
                setPathState(6);

                break;

            case 6:
                executePickupRoutine();

                follower.followPath(scorePickup3, true);

                if (follower.isBusy()) return;
                if (lastPathState != pathState) pathTimer.resetTimer();
                lastPathState = pathState;
                setPathState(7);

                break;

            case 7:
                Actions.openBackClaw(true);

                if(pathTimer.getElapsedTimeSeconds() <= 0.2) return;
                follower.followPath(level1Ascend, true);

                if(pathTimer.getElapsedTimeSeconds() <= 0.4) return;
                Actions.prepareSpecimen();
                Actions.raiseVertical();

                if(follower.isBusy()) return;
                if(lastPathState != pathState) pathTimer.resetTimer();
                lastPathState = pathState;
                setPathState(-1);

                break;
        }
    }

    public void setPathState(int pState) {
        pathState = pState;
    }

    private void executePickupRoutine() {
        Actions.pickupSample();
        Actions.raiseVertical();

        if (pathTimer.getElapsedTimeSeconds() <= 0.2) return;
        Actions.openFrontClaw(true);

        if (pathTimer.getElapsedTimeSeconds() <= 0.5) return;
        //Actions.transfer();

        if (pathTimer.getElapsedTimeSeconds() <= 0.7) return;
        //Actions.transfer2();

        if (pathTimer.getElapsedTimeSeconds() <= 0.9) return;
        Actions.doTransfer();

        if (pathTimer.getElapsedTimeSeconds() <= 1) return;
        Actions.leaveSampleBasket();
    }

    @Override
    public void loop() {
        follower.update();
        autonomousPathUpdate();

        Positions.update();

        Pose currentPose = follower.getPose();
        if (currentPose != null) {
            debug.addData("x", currentPose.getX());
            debug.addData("y", currentPose.getY());
            debug.addData("heading", currentPose.getHeading());
        }
        debug.update();
    }

    @Override
    public void init() {
        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();

        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        follower.setStartingPose(startPose);
        Actions.openBackClaw(false);
        buildPaths();
    }

    @Override
    public void start() {
        opmodeTimer.resetTimer();
        setPathState(0);
    }
}