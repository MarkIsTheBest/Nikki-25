package org.firstinspires.ftc.teamcode.opModes.teleOp;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.util.Timer;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.constants.Positions;
import org.firstinspires.ftc.teamcode.helper.IntakeHelper;
import org.firstinspires.ftc.teamcode.helper.LaunchHelper;
import org.firstinspires.ftc.teamcode.helper.Launchers;
import org.firstinspires.ftc.teamcode.helper.general.Debug;
import org.firstinspires.ftc.teamcode.helper.general.FpsCounter;
import org.firstinspires.ftc.teamcode.helper.pid.HeadingPID;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

import java.util.List;

@TeleOp
public class TeleOpPedro extends OpMode {

    FpsCounter fps = new FpsCounter();
    HeadingPID headingPID = new HeadingPID();
    IntakeHelper intakeHelper;
    LaunchHelper launchHelper;

    private Follower follower;
    public static Pose startingPose;
    private boolean lockMode = false;
    Timer opModeTimer = new Timer();

    @Override
    public void init() {
        // OPTIMIZATION: Enable Bulk Caching for 60+ FPS
        List<LynxModule> allHubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule module : allHubs) {
            module.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }

        initPedro();
        initHelpers();
    }

    private void initHelpers() {
        Launchers.INSTANCE = new Launchers();
        IntakeHelper.INSTANCE = new IntakeHelper();
        LaunchHelper.INSTANCE = new LaunchHelper();

        intakeHelper = IntakeHelper.INSTANCE;
        launchHelper = LaunchHelper.INSTANCE;
    }

    private void initPedro() {
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(startingPose == null ? new Pose() : startingPose);
        follower.update();
    }

    @Override
    public void start() {
        follower.startTeleopDrive(true);
        opModeTimer.resetTimer();
    }

    @Override
    public void loop() {
        if(opModeTimer.getElapsedTimeSeconds() > 120) return;

        fps.update();
        follower.update();

        // CRITICAL: Update state machines
        intakeHelper.update();
        launchHelper.update();

        // Prevent TeleOp from fighting Auto-Park
        if (!follower.isBusy()) {
            movement(turnInput());
        }

        manipulation();
        handleModeSwitch();
        handleAutoPark();
        showTelemetry();
    }

    private void movement(double turnInput) {
        follower.setTeleOpDrive(-gamepad1.left_stick_y, -gamepad1.left_stick_x, turnInput, true);
    }

    private void handleModeSwitch() {
        if (gamepad1.rightBumperWasPressed()) {
            lockMode = !lockMode;
            headingPID.reset();
        }
    }

    private void manipulation() {
        if(gamepad1.aWasPressed()) {
            intakeHelper.spinIntake(!intakeHelper.isIntakeSpinning());
        }
        if(gamepad1.xWasPressed()) {
            launchHelper.startLaunchSequence();
        }
    }

    private void handleAutoPark() {
        if (gamepad1.leftBumperWasPressed()) {
            follower.followPath(follower.pathBuilder()
                    .addPath(new com.pedropathing.geometry.BezierLine(follower.getPose(), Positions.Field.RED_BASE))
                    .setLinearHeadingInterpolation(follower.getPose().getHeading(), Positions.Field.RED_BASE.getHeading())
                    .build(), true);
        }
    }

    private double turnInput() {
        if (lockMode) {
            return headingPID.update(getAngleToGoal(), follower.getPose().getHeading());
        }
        return -gamepad1.right_stick_x * 1.1f;
    }

    private double getAngleToGoal() {
        double dx = Positions.Field.RED_GOAL.getX() - follower.getPose().getX();
        double dy = Positions.Field.RED_GOAL.getY() - follower.getPose().getY();
        return Math.atan2(dy, dx);
    }

    private void showTelemetry() {
        Debug.INSTANCE.addData("FPS", fps.getFps());
        Debug.INSTANCE.addData("LockMode", lockMode);
        intakeHelper.showTelemetry();
        launchHelper.showTelemetry();
        Debug.INSTANCE.update();
    }
}