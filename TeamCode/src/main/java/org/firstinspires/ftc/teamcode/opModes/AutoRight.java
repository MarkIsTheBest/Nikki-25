package org.firstinspires.ftc.teamcode.opModes;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathBuilder;
import com.pedropathing.paths.PathChain;
import com.pedropathing.paths.PathConstraints;
import com.pedropathing.util.Timer;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystems.hardware.Motors;
import org.firstinspires.ftc.teamcode.subsystems.hardware.Other;
import org.firstinspires.ftc.teamcode.subsystems.hardware.Servos;

import static org.firstinspires.ftc.teamcode.subsystems.Positions.CLAW_CLOSED;
import static org.firstinspires.ftc.teamcode.subsystems.Positions.CLAW_OPENED;
import static org.firstinspires.ftc.teamcode.subsystems.Positions.EXTEND_SLIDER_FIRST_WORD;
import static org.firstinspires.ftc.teamcode.subsystems.Positions.EXTEND_SLIDER_MIN_EXTEND;
import static org.firstinspires.ftc.teamcode.subsystems.Positions.ROTATE_SLIDER_INIT;
import static org.firstinspires.ftc.teamcode.subsystems.Positions.ROTATE_SLIDER_INTAKE;
import static org.firstinspires.ftc.teamcode.subsystems.Positions.ROTATE_SLIDER_OUTTAKE_FIRST_WORD;

@Autonomous
public class AutoRight extends LinearOpMode {

    private Follower follower;
    private Timer pathTimer;
    private int currentState = 0;
    private int lastState = -1;

    //0 = left, center
    //1 = center, right
    //2 = left, right

    private int randomness = 2;

    public PathChain startLeft, startRight, startCenter, resetStartLeft,
            resetStartRight, resetStartCenter, prepareOuttake, endLeft,
            endRight, endCenter, resetEndLeft,
            resetEndRight, resetEndCenter, line6, line7;

    public void buildPaths() {

        double offsetStart = -7.5;

        startLeft = follower.pathBuilder(Constants.pathConstraints)
                .addPath(new BezierLine(new Pose(216-(57.982*1.5), 9.770*1.5), new Pose(216-(55.9*1.5), (22.938*1.5) +offsetStart)))
                .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(180-108))
                .build();

        resetStartLeft = follower.pathBuilder(Constants.pathConstraints)
                .addPath(new BezierLine(new Pose(216-(57.982*1.5), 9.770*1.5), new Pose(216-(55.9*1.5), (22.938*1.5) +offsetStart)))
                .setLinearHeadingInterpolation(Math.toRadians(180-(180-120)), Math.toRadians(180-90))
                .build();

        startRight = follower.pathBuilder(Constants.pathConstraints)
                .addPath(new BezierLine(new Pose(216-(57.982*1.5), 9.770*1.5), new Pose(216-(55.9*1.5), (22.938*1.5) +offsetStart)))
                .setLinearHeadingInterpolation(Math.toRadians(180-90), Math.toRadians(180-60))
                .build();

        resetStartRight = follower.pathBuilder(Constants.pathConstraints)
                .addPath(new BezierLine(new Pose(216-(57.982*1.5), 9.770*1.5), new Pose(216-(55.9*1.5), (22.938*1.5) +offsetStart)))
                .setLinearHeadingInterpolation(Math.toRadians(180-60), Math.toRadians(90))
                .build();

        startCenter = follower.pathBuilder(Constants.pathConstraints)
                .addPath(new BezierLine(new Pose(216-(57.982*1.5), 9.770*1.5), new Pose(216-(55.9*1.5), (22.938*1.5) +offsetStart)))
                .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(90))
                .build();

        resetStartCenter = follower.pathBuilder(Constants.pathConstraints)
                .addPath(new BezierLine(new Pose(216-(57.982*1.5), 9.770*1.5), new Pose(216-(55.9*1.5), (22.938*1.5) +offsetStart)))
                .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(90))
                .build();

        double outtakeOffset = 13.8;

        prepareOuttake = follower.pathBuilder(Constants.pathConstraints)
                .addPath(
                        new BezierCurve(
                                new Pose(216-(55.9*1.5), (22.938*1.5) +offsetStart),
                                new Pose(216-(12.818*1.5), 18.978*1.5),
                                new Pose(216-((45.75*1.5) + outtakeOffset), (49.110*1.5)+outtakeOffset)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(180-(180-135)))
                .build();

        endRight = follower.pathBuilder(Constants.pathConstraints)
                .addPath(new BezierLine(new Pose(216-(45.75*1.5), 49.110*1.5), new Pose(216-(46.77*1.5), 48.111*1.5)))
                .setLinearHeadingInterpolation(Math.toRadians(-145), Math.toRadians(180+45))
                .build();

        endLeft = follower.pathBuilder(Constants.pathConstraints)
                .addPath(new BezierLine(new Pose(216-(45.75*1.5), 49.110*1.5), new Pose(216-(46.77*1.5), 48.111*1.5)))
                .setLinearHeadingInterpolation(Math.toRadians(-145), Math.toRadians(180+60))
                .build();

        endCenter = follower.pathBuilder(Constants.pathConstraints)
                .addPath(new BezierLine(new Pose(216-(45.75*1.5), 49.110*1.5), new Pose(216-(46.77*1.5), 48.111*1.5)))
                .setLinearHeadingInterpolation(Math.toRadians(135), Math.toRadians(180+82))
                .build();

        resetEndRight = follower.pathBuilder(Constants.pathConstraints)
                .addPath(new BezierLine(new Pose(216-(45.75*1.5), 49.110*1.5), new Pose(216-(46.77*1.5), 48.111*1.5)))
                .setLinearHeadingInterpolation(Math.toRadians(180+45), Math.toRadians(180+45))
                .build();

        resetEndLeft = follower.pathBuilder(Constants.pathConstraints)
                .addPath(new BezierLine(new Pose(216-(45.75*1.5), 49.110*1.5), new Pose(216-(46.77*1.5), 48.111*1.5)))
                .setLinearHeadingInterpolation(Math.toRadians(180+60), Math.toRadians(180+45))
                .build();

        resetEndCenter = follower.pathBuilder(Constants.pathConstraints)
                .addPath(new BezierLine(new Pose(216-(45.75*1.5), 49.110*1.5), new Pose(216-(46.77*1.5), 48.111*1.5)))
                .setLinearHeadingInterpolation(Math.toRadians(180+82), Math.toRadians(180+30))
                .build();

        line6 = follower.pathBuilder(Constants.pathConstraints)
                .addPath(new BezierLine(new Pose(216-(46.77*1.5), 48.111*1.5), new Pose(216-((45.75*1.5) + outtakeOffset + 3), (49.110*1.5)+outtakeOffset - 3)))
                .setLinearHeadingInterpolation(Math.toRadians(180+30), Math.toRadians(180-55))
                .build();

        line7 = follower.pathBuilder(Constants.pathConstraints)
                .addPath(
                        new BezierLine(new Pose(216-((45.75*1.5) + outtakeOffset), (49.110*1.5)+outtakeOffset), new Pose(216-(30.797*1.5), 32.962*1.5))
                )
                .setLinearHeadingInterpolation(Math.toRadians(180-55), Math.toRadians(180-(180+45)))
                .build();
    }


    @Override
    public void runOpMode()
    {
        initialize();
        waitForStart();

        while(!opModeIsActive())
        {
            double tx = 0;

            LLResult result = Other.Limelight.getLatestResult();
            if (result != null && result.isValid()) {
                tx = result.getTx();
            }

            /*if (tx < -5) randomness = 0;
            if (tx > 5) randomness = 1;
            if (tx >= -5 && tx <= 5) randomness = 2;
*/
            telemetry.addData("randomness", randomness);
            telemetry.update();
            if (isStopRequested()) return;

        }
        while (opModeIsActive())
        {
            update();
        }
    }

    public void setPathState(int pState) {
        currentState = pState;
        pathTimer.resetTimer();
    }

    private void initialize() {
        follower = Constants.createFollower(hardwareMap);
        pathTimer = new Timer();

        Pose startPose = new Pose(216-((144-86.018)*1.5), 9.770*1.5, Math.toRadians(90));
        follower.setStartingPose(startPose);
        buildPaths();

        Other.init(hardwareMap);
        Other.Limelight.setPollRateHz(100);
        Other.Limelight.start();
        Other.Limelight.pipelineSwitch(1);

        Motors.init(hardwareMap);
        Servos.init(hardwareMap);
        Motors.setPosition(Motors.extendSlider, EXTEND_SLIDER_MIN_EXTEND, 1);
        Motors.goToPosition(Motors.rotateSlider, ROTATE_SLIDER_INIT, 1);
        Servos.claw.setPosition(CLAW_OPENED);

    }

    private void update() {

        telemetry.addData("isBusyFollower", follower.isBusy());
        telemetry.addData("isExtendBusy", Motors.extendSlider.isBusy());
        telemetry.addData("isRotateBusy", Motors.rotateSlider.isBusy());
        telemetry.addData("timer", pathTimer.getElapsedTimeSeconds());
        telemetry.addData("state", currentState);
        telemetry.addData("randomness", randomness);
        telemetry.update();

        follower.update();

        switch (currentState)
        {
            case 0:
                Motors.goToPosition(Motors.rotateSlider, ROTATE_SLIDER_INTAKE,1 );

                switch (randomness)
                {
                    case 0:
                        follower.followPath(startCenter, true);
                        break;
                    case 1:
                        follower.followPath(startCenter, true);
                        break;
                    case 2:
                        follower.followPath(startLeft, true);
                        break;
                }
                setPathState(1);

                break;

            case 1:
                if (!follower.isBusy()) {
                    Motors.setPosition(Motors.extendSlider, 1700,1 ); //Change Extend Pos
                    setPathState(2);
                }
                break;

            case 2:
                if (!Motors.extendSlider.isBusy())
                {
                    Servos.claw.setPosition(CLAW_CLOSED);
                    setPathState(3);
                }
                break;

            case 3:
                if(pathTimer.getElapsedTimeSeconds() > 0.5)
                {
                    Motors.setPosition(Motors.extendSlider, EXTEND_SLIDER_MIN_EXTEND,1 );
                    Motors.goToPosition(Motors.rotateSlider, 0,1 );
                    setPathState(-1);
                }
                break;

            case -1:
                switch (randomness)
                {
                    case 0:
                        follower.followPath(resetStartCenter, true);
                        break;
                    case 1:
                        follower.followPath(resetStartCenter, true);
                        break;
                    case 2:
                        follower.followPath(resetStartLeft, true);
                        break;
                }
                setPathState(4);
                break;

            case 4:
                if (!follower.isBusy())
                {
                    follower.followPath(prepareOuttake);
                    Motors.goToPosition(Motors.rotateSlider, ROTATE_SLIDER_OUTTAKE_FIRST_WORD, 1);
                    setPathState(5);
                }
                break;

            case 5:
                if(!follower.isBusy() || pathTimer.getElapsedTimeSeconds() > 5)
                {
                    Motors.setPosition(Motors.extendSlider, EXTEND_SLIDER_FIRST_WORD, 1);
                    setPathState(7);
                }
                break;

            case 7:
                if (!Motors.extendSlider.isBusy())
                {
                    Servos.claw.setPosition(CLAW_OPENED);
                    setPathState(8);
                }
                break;

            case 8:
                if(pathTimer.getElapsedTimeSeconds() > 0.5)
                {
                    Motors.setPosition(Motors.extendSlider, EXTEND_SLIDER_MIN_EXTEND, 1);
                    setPathState(9);
                }
                break;

            case 9:
                if (!Motors.extendSlider.isBusy() && !Motors.rotateSlider.isBusy())
                {
                    Motors.goToPosition(Motors.rotateSlider, ROTATE_SLIDER_INTAKE, 1);
                    switch (randomness)
                    {
                        case 0:
                            follower.followPath(endLeft, true);
                            break;
                        case 1:
                            follower.followPath(endRight, true);
                            break;
                        case 2:
                            follower.followPath(endRight, true);
                            break;
                    }
                    setPathState(10);
                }
                break;

            case 10:
                if (!follower.isBusy()) {
                    Motors.setPosition(Motors.extendSlider, 2000,1 ); //Change Extend Pos
                    setPathState(11);
                }
                break;

            case 11:
                if (!Motors.extendSlider.isBusy())
                {
                    Servos.claw.setPosition(CLAW_CLOSED);
                    setPathState(12);
                }
                break;

            case 12:
                if(pathTimer.getElapsedTimeSeconds() > 0.5)
                {
                    Motors.setPosition(Motors.extendSlider, EXTEND_SLIDER_MIN_EXTEND,1 );
                    setPathState(13);
                }
                break;

            case 13:
                if (!Motors.extendSlider.isBusy())
                {
                    switch (randomness)
                    {
                        case 0:
                            follower.followPath(resetEndLeft, true);
                            break;
                        case 1:
                            follower.followPath(resetEndRight, true);
                            break;
                        case 2:
                            follower.followPath(resetEndRight, true);
                            break;
                    }
                    setPathState(14);
                }
                break;

            case 14:
                if(!follower.isBusy())
                {
                    follower.followPath(line6);
                    Motors.goToPosition(Motors.rotateSlider, ROTATE_SLIDER_OUTTAKE_FIRST_WORD, 1);
                    setPathState(15);
                }
                break;

            case 15:
                if(!follower.isBusy() && !Motors.rotateSlider.isBusy())
                {
                    Motors.setPosition(Motors.extendSlider, EXTEND_SLIDER_FIRST_WORD, 1);
                    setPathState(17);
                }
                break;

            case 17:
                if (!Motors.extendSlider.isBusy())
                {
                    Servos.claw.setPosition(CLAW_OPENED);
                    setPathState(18);
                }
                break;

            case 18:
                if(pathTimer.getElapsedTimeSeconds() > 0.5)
                {
                    Motors.setPosition(Motors.extendSlider, EXTEND_SLIDER_MIN_EXTEND, 1);
                    Motors.goToPosition(Motors.rotateSlider, ROTATE_SLIDER_INIT, 1);
                    setPathState(19);
                }
                break;

            case 19:
                if (!Motors.extendSlider.isBusy() && !Motors.rotateSlider.isBusy())
                {
                    follower.followPath(line7);
                    setPathState(20);
                }

                break;

            case 20:
                if(!follower.isBusy())
                {

                }
        }
    }
}
