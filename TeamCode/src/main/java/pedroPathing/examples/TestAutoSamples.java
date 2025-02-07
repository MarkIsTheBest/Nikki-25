package pedroPathing.examples;

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
import subsystems.Func;
import subsystems.hardware.Motors;
import subsystems.hardware.Servos;


@Autonomous
public class TestAutoSamples extends OpMode
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
    private final Pose startPose = new Pose(8.26, 112.13361169102296, Math.toRadians(0));  // Starting position
    private final Pose scorePose = new Pose(14.580375782881001, 128.36743215031316, Math.toRadians(310)); // Scoring position

    private final Pose pickup1Pose = new Pose(60, 96.65135699373695, Math.toRadians(90));// Push Sample 1
    private final Pose pickup1PoseControl = new Pose(50, 130, Math.toRadians(90));


    public Path scorePreload, park;
    public PathChain grabPickup1;
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
        rotateBackBodyPos = 0.37;
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
                rotateBackBodyPos = 0.17;
                rotateBackClawPos = 0.17;
                verticalPos = 3250;
                linkagePos= subsystems.Constants.LINKAGE.CLOSED;
                updatePositions();
                follower.followPath(scorePreload,true);
                setPathState(1);
                break;

            case 1: // Wait until the robot is near the scoring position
                if(pathTimer.getElapsedTimeSeconds()>4.0){
                    follower.followPath(grabPickup1,true);
                    verticalPos=0;
                    updatePositions();
                    setPathState(8);
                }
                if(pathTimer.getElapsedTimeSeconds()>2.0){
                    backClawPos=0.5;
                    updatePositions();
                }
                break;
                case 8:
                        if (!follower.isBusy()) {
                            verticalPos=0;
                            rotateBackBodyPos = 0.15;
                            rotateBackClawPos = 0.3;
                            updatePositions();
                            setPathState(-1);
                        }
        }
    }
}

