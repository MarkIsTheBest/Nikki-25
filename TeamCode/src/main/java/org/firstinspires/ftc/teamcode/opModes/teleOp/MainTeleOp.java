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
import org.firstinspires.ftc.teamcode.constants.Timers;
import org.firstinspires.ftc.teamcode.constants.enums.AllianceColor;
import org.firstinspires.ftc.teamcode.constants.enums.Motif;
import org.firstinspires.ftc.teamcode.helper.IntakeHelper;
import org.firstinspires.ftc.teamcode.helper.LaunchHelper;
import org.firstinspires.ftc.teamcode.helper.Launchers;
import org.firstinspires.ftc.teamcode.helper.general.Debug;
import org.firstinspires.ftc.teamcode.helper.general.FpsCounter;
import org.firstinspires.ftc.teamcode.helper.hardware.Hardware;
import org.firstinspires.ftc.teamcode.helper.hardware.Servos;
import org.firstinspires.ftc.teamcode.helper.hardware.sensors.LEDs;
import org.firstinspires.ftc.teamcode.helper.pid.HeadingPID;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

import java.util.List;

public class MainTeleOp {

    LinearOpMode opMode;

    FpsCounter fps = new FpsCounter();
    HeadingPID headingPID = new HeadingPID();
    Debug debug;

    Servos servos = new Servos();
    IntakeHelper intakeHelper;
    LaunchHelper launchHelper;
    Launchers launchers;

    private Follower follower;
    public Pose startingPose;

    private Pose currentGoalPosition;
    private Pose currentBasePosition;

    private boolean intaking = false;
    private boolean lockMode = false;

    Timer opModeTimer = new Timer();

    // Optimization: List for Bulk Reads
    private List<LynxModule> allHubs;

    public MainTeleOp(AllianceColor allianceColor, LinearOpMode opMode, Pose startingPose) {
        switch (allianceColor) {
            case RED:
                currentGoalPosition = Positions.Field.RED_GOAL;
                currentBasePosition = Positions.Field.RED_BASE;
                break;
            case BLUE:
                currentGoalPosition = Positions.Field.BLUE_GOAL;
                currentBasePosition = Positions.Field.BLUE_BASE;
                break;
        }

        this.opMode = opMode;
        this.startingPose = startingPose;
        debug = new Debug(opMode.telemetry);
    }

    public void initialize() {
        allHubs = opMode.hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }

        servos.init();
        Hardware.init();
        for(Servo Led : LEDs.AllLEDs()) {
            LEDs.setEmpty(Led);
        }
        initPedro();
        initHelpers();
    }

    private void initHelpers() {
        launchers = new Launchers(debug);
        launchHelper = new LaunchHelper(debug, launchers, this.servos);
        intakeHelper = new IntakeHelper(debug, launchers, this.servos);
    }

    private void initPedro() {
        follower = Constants.createFollower(opMode.hardwareMap);
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

        launchHelper.update();
        follower.update();

        movement(turnInput());
        manipulation();
        handleModeSwitch();

        showTelemetry(); // Ensure this is lightweight
        if(launchHelper.IsLaunching()) return;
        intakeHelper.update();
        // Moved HandleIntakeSpin into update() to reduce method call overhead
        // and it is now optimized internally in IntakeHelper
        intakeHelper.HandleIntakeSpin();
    }

    private void movement(double turnInput) {
        follower.setTeleOpDrive(
                -opMode.gamepad1.left_stick_y,
                -opMode.gamepad1.left_stick_x,
                turnInput,
                true
        );
    }

    private void handleModeSwitch() {
        if (opMode.gamepad1.rightBumperWasPressed()) {
            lockMode = !lockMode;
            headingPID.reset();
        }
    }

    private void manipulation() {

        if(opMode.gamepad1.aWasPressed()) {
            intaking = true;
            intakeHelper.spinIntake(true);
        }
        if(opMode.gamepad1.bWasPressed()) {
            intaking = false;
            intakeHelper.spinIntake(false);
        }

        if(opMode.gamepad1.dpadLeftWasPressed()) {
            launchHelper.setMotif(Motif.GPP);
        }
        if(opMode.gamepad1.dpadUpWasPressed()) {
            launchHelper.setMotif(Motif.PGP);
        }
        if(opMode.gamepad1.dpadRightWasPressed()) {
            launchHelper.setMotif(Motif.PPG);
        }

        if(opMode.gamepad1.right_trigger > 0.1) {
            launchHelper.setTargetRPM(4500);
        }

        if(opMode.gamepad1.left_trigger > 0.1) {
            launchHelper.setTargetRPM(4000);
        }

        if(opMode.gamepad1.xWasPressed()) {
            launchHelper.startLaunchSequence();
        }
    }

    private void handleAutoPark() {
        if (opMode.gamepad1.leftBumperWasPressed()) {
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
        return -opMode.gamepad1.right_stick_x * 1.1f;
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
        debug.addData("FPS", fps.getFps());
        debug.addData("Position", follower.getPose());
        launchHelper.showTelemetry();
        intakeHelper.showTelemetry();
        launchers.showTelemetry();
        debug.update();
    }
}