package pedroPathing.examples;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.localization.PoseUpdater;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.PathBuilder;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;
import com.pedropathing.util.Constants;
import com.pedropathing.util.DashboardPoseTracker;
import com.pedropathing.util.Drawing;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;
import subsystems.Func;
import subsystems.hardware.Motors;
import subsystems.hardware.Servos;


@Autonomous
public class TestAuto extends OpMode
{
    Follower follower;
    Telemetry telm;
    PathChain path;
    int pathState;
    Timer pathTimer;
    private double linkagePos;
    private double rotateAxisPos;
    private double rotateBodyPos;
    private double rotateHeadPos;
    private double rotateClawPos;
    private double clawPos;
    private double rotateBackBodyPos;
    private double rotateBackClawPos;
    private double backClawPos;
    private int verticalPos;
    private final Pose startPose = new Pose(8.26, 56, Math.toRadians(180));  // Starting position
    private final Pose scorePose = new Pose(26, 70, Math.toRadians(180)); // Scoring position
    private final Pose pickup1PoseControl= new Pose(30,44,Math.toRadians(270));

    private final Pose pickup1Pose = new Pose(57, 40, Math.toRadians(270));// Push Sample 1
    private final Pose scorePush1Pose = new Pose(22, 33, Math.toRadians(270));
    private final Pose scorePush1Control = new Pose(57, 28, Math.toRadians(270));
    private final Pose scoreAlign2Pose = new Pose(57, 30, Math.toRadians(270));
    private final Pose scorePush2Pose = new Pose(22, 23, Math.toRadians(270));
    private final Pose scoreAlign3Pose = new Pose(57.5, 21, Math.toRadians(270));
    private final Pose scorePush3Pose = new Pose(15, 17, Math.toRadians(270));

    public Path scorePreload, park;
    public PathChain grabPickup1,scorePush1,scoreAlign2,scorePush2,scoreAlign3,scorePush3;
    private void initHardware()
    {
        Motors.init(hardwareMap);
        Servos.init(hardwareMap);
    }
    @Override
    public void init()
    {
        pathTimer = new Timer();
        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        follower.setStartingPose(startPose);
        initHardware();
        buildPaths();
        rotateAxisPos = subsystems.Constants.ROTATE_AXIS.MID;
        rotateClawPos = subsystems.Constants.ROTATE_CLAW.INIT;
        rotateBodyPos = 0.73; //Constants.ROTATE_BODY.MAX;
        rotateHeadPos = subsystems.Constants.ROTATE_HEAD.INIT;
        verticalPos = subsystems.Constants.VERTICAL.MIN;
        linkagePos = subsystems.Constants.LINKAGE.CLOSED;
        rotateBackBodyPos = 0.07;
        rotateBackClawPos = 0.07;
        backClawPos=0.4;
        updatePositions();


    }

    @Override
    public void loop()
    {
        follower.update();
        autonomousPathUpdate();
        telemetry.addData("Path State", pathState);
        telemetry.addData("Position", follower.getPose().toString());
        telemetry.addData("Timer",pathTimer.getElapsedTimeSeconds());
        telemetry.update();
    }
    public void buildPaths(){
        scorePreload = new Path(new BezierLine(new Point(startPose), new Point(scorePose)));
        scorePreload.setLinearHeadingInterpolation(startPose.getHeading(), scorePose.getHeading());
        grabPickup1 = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(scorePose),new Point(pickup1PoseControl), new Point(pickup1Pose)))
                .setLinearHeadingInterpolation(scorePose.getHeading(), pickup1Pose.getHeading())
                .setPathEndTimeoutConstraint(10.0)
                .build();
        scorePush1 = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(pickup1Pose),new Point(scorePush1Control), new Point(scorePush1Pose)))
                .setLinearHeadingInterpolation(pickup1Pose.getHeading(), scorePush1Pose.getHeading())
                .setPathEndTimeoutConstraint(10.0)
                .build();
        scoreAlign2=follower.pathBuilder()
                .addPath(new BezierLine(new Point(scorePush1Pose), new Point(scoreAlign2Pose)))
                .setLinearHeadingInterpolation(scorePush1Pose.getHeading(),scoreAlign2Pose.getHeading())
                .build();
        scorePush2=follower.pathBuilder()
                .addPath(new BezierLine(new Point(scoreAlign2Pose), new Point(scorePush2Pose)))
                .setLinearHeadingInterpolation(scoreAlign2Pose.getHeading(),scorePush2Pose.getHeading())
                .build();
        scoreAlign3=follower.pathBuilder()
                .addPath(new BezierLine(new Point(scorePush2Pose), new Point(scoreAlign3Pose)))
                .setLinearHeadingInterpolation(scorePush2Pose.getHeading(),scoreAlign3Pose.getHeading())
                .build();
        scorePush3=follower.pathBuilder()
                .addPath(new BezierLine(new Point(scoreAlign3Pose), new Point(scorePush3Pose)))
                .setLinearHeadingInterpolation(scoreAlign3Pose.getHeading(),scorePush3Pose.getHeading())
                .build();

    }
    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }
    private void updatePositions()
    {
        Servos.linkageLeft.setPosition(linkagePos);
        Servos.linkageRight.setPosition(linkagePos);
        Servos.rotateAxis.setPosition(rotateAxisPos);
        Servos.rotateBody.setPosition(rotateBodyPos);
        Servos.rotateHead.setPosition(rotateHeadPos);
        Servos.rotateClaw.setPosition(rotateClawPos);
        Servos.claw.setPosition(clawPos);
        Servos.rotateBackBody.setPosition(rotateBackBodyPos);
        Servos.rotateBackClaw.setPosition(rotateBackClawPos);
        Servos.backClaw.setPosition(backClawPos);

        Func.SetMotorPosition(Motors.verticalLeft, verticalPos);
        Func.SetMotorPosition(Motors.verticalRight, verticalPos);
    }
    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0: // Move from start to scoring position
                verticalPos=1500;
                updatePositions();
                follower.followPath(scorePreload,true);
                setPathState(1);
                break;

            case 1: // Wait until the robot is near the scoring position
                if(pathTimer.getElapsedTimeSeconds()>3.0){
                    follower.followPath(grabPickup1,true);
                    setPathState(2);
                }
                if(!follower.isBusy()){
                    verticalPos=600;
                    backClawPos=0.55;
                    updatePositions();
                }
                break;

            case 2: // Wait until the robot is near the first sample pickup position
                verticalPos=0;
                updatePositions();
                if (!follower.isBusy()) {
                    follower.followPath(scorePush1,true);
                    setPathState(3);
                }
                break;
            case 3: // Wait until the robot is near the first sample pickup position
                if (!follower.isBusy()) {
                    follower.followPath(scoreAlign2,true);
                    setPathState(4);
                }
                break;
            case 4: // Wait until the robot is near the first sample pickup position
                if (!follower.isBusy()) {
                    follower.followPath(scorePush2,true);
                    setPathState(5);
                }
                break;
            case 5: // Wait until the robot is near the first sample pickup position
                if (!follower.isBusy()) {
                    follower.followPath(scoreAlign3,true);
                    setPathState(6);
                }
                break;
            case 6: // Wait until the robot is near the first sample pickup position
                if (!follower.isBusy()) {
                    follower.followPath(scorePush3,true);
                    setPathState(8);
                }
                break;
            case 8:
                if(!follower.isBusy()){
                    if(pathTimer.getElapsedTimeSeconds()>3.0){
                        requestOpModeStop();
                        setPathState(-1);
                    }
                }
        }
    }
}

