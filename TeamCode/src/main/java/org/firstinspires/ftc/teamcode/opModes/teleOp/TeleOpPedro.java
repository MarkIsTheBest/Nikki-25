package org.firstinspires.ftc.teamcode.opModes.teleOp;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.constants.Positions;
import org.firstinspires.ftc.teamcode.helper.IntakeHelper;
import org.firstinspires.ftc.teamcode.helper.LaunchHelper;
import org.firstinspires.ftc.teamcode.helper.Launchers;
import org.firstinspires.ftc.teamcode.helper.general.FpsCounter;
import org.firstinspires.ftc.teamcode.helper.hardware.Hardware;
import org.firstinspires.ftc.teamcode.helper.hardware.sensors.LEDs;
import org.firstinspires.ftc.teamcode.helper.pid.HeadingPID;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

import java.util.List;

@Configurable
@TeleOp
public class TeleOpPedro extends LinearOpMode {

    FpsCounter fps = new FpsCounter();
    HeadingPID headingPID = new HeadingPID();
    IntakeHelper intakeHelper;
    LaunchHelper launchHelper;
    Launchers launcherHelper;

    private Follower follower;
    public static Pose startingPose = Positions.Auto.START_POSE;

    private Pose currentGoalPosition = Positions.Field.RED_GOAL;
    private Pose currentBasePosition = Positions.Field.RED_BASE;

    private boolean intaking = false;
    private boolean lockMode = false;

    Timer opModeTimer = new Timer();

    // Optimization: List for Bulk Reads
    private List<LynxModule> allHubs;

    public void runOpMode() throws InterruptedException {
        initialize();
        waitForStart();
        play();
        if (isStopRequested()) return;
        while (opModeIsActive()) update();
    }

    public void initialize() {
        // Optimization: Initialize Bulk Reads
        allHubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }

        Hardware.init();
        for(Servo Led : LEDs.AllLEDs()) {
            LEDs.setEmpty(Led);
        }
        initPedro();
        initHelpers();
    }

    private void initHelpers() {
        Launchers.INSTANCE = new Launchers();
        launcherHelper = Launchers.INSTANCE;

        IntakeHelper.INSTANCE = new IntakeHelper();
        intakeHelper = IntakeHelper.INSTANCE;

        LaunchHelper.INSTANCE = new LaunchHelper();
        launchHelper = LaunchHelper.INSTANCE;
    }

    private void initPedro() {
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(startingPose == null ? new Pose() : startingPose);
        follower.update();
    }

    public void play() {
        follower.startTeleopDrive(true);
        opModeTimer.resetTimer();
    }

    public void update() {
        // Optimization: Clear cache at the start of loop.
        // All subsequent .getVoltage() or encoder reads are now instant.
        for (LynxModule hub : allHubs) {
            hub.clearBulkCache();
        }

        if(opModeTimer.getElapsedTimeSeconds() > 120) return;

        launchHelper.update();
        fps.update();
        follower.update();

        movement(turnInput());
        manipulation();

        handleModeSwitch();
        handleAutoPark();

        showTelemetry(); // Ensure this is lightweight

        if(intaking) {
            intakeHelper.update();
        }
        // Moved HandleIntakeSpin into update() to reduce method call overhead
        // and it is now optimized internally in IntakeHelper
        intakeHelper.HandleIntakeSpin();
    }

    private void movement(double turnInput) {
        follower.setTeleOpDrive(
                -gamepad1.left_stick_y,
                -gamepad1.left_stick_x,
                turnInput,
                true
        );
    }

    private void handleModeSwitch() {
        if (gamepad1.rightBumperWasPressed()) {
            lockMode = !lockMode;
            headingPID.reset();
        }
    }

    private void manipulation() {
        // Optimization: Direct boolean assignment is faster than method calls
        if(gamepad1.aWasPressed()) {
            intaking = true;
            intakeHelper.spinIntake(true);
        }
        if(gamepad1.bWasPressed()) {
            intaking = false;
            intakeHelper.spinIntake(false);
        }

        if(gamepad1.xWasPressed()) {
            launchHelper.startLaunchSequence();
        }
    }

    private void handleAutoPark() {
        if (gamepad1.leftBumperWasPressed()) {
            goToBase();
        }
    }

    private double turnInput() {
        if (lockMode) {
            return headingPID.update(
                    getAngleToGoal(),
                    follower.getPose().getHeading()
            );
        }
        return -gamepad1.right_stick_x * 1.1f;
    }

    private double getAngleToGoal() {
        double dx = currentGoalPosition.getX() - follower.getPose().getX();
        double dy = currentGoalPosition.getY() - follower.getPose().getY();
        return Math.atan2(dy, dx);
    }

    private void goToBase() {
        Pose curPos = follower.getPose();
        PathChain basePath = follower.pathBuilder()
                .addPath(new BezierLine(curPos, currentBasePosition))
                .setLinearHeadingInterpolation(curPos.getHeading(), currentBasePosition.getHeading())
                .build();
        follower.followPath(basePath, true);
    }

    private void showTelemetry() {
        // Keep telemetry minimal or use a timer to update it only every 200ms
        // if fps is still an issue.
    }
}