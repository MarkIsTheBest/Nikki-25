package org.firstinspires.ftc.teamcode.opModes.teleOp;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.util.Timer;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.helper.Drawing;
import org.firstinspires.ftc.teamcode.helper.general.Debug;
import org.firstinspires.ftc.teamcode.helper.general.FpsCounter;
import org.firstinspires.ftc.teamcode.helper.hardware.Hardware2;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystems.Claw;
import org.firstinspires.ftc.teamcode.subsystems.Drive;
import org.firstinspires.ftc.teamcode.subsystems.Slider;

import java.util.List;

public class MainTeleOp2 {

    private final LinearOpMode opMode;
    private final Pose startingPosition;

    private Follower follower;
    private Hardware2 hardware;
    private Debug debug;
    private Drive drive;
    private Claw claw;
    private Slider slider;
    private FpsCounter fps = new FpsCounter();

    private List<LynxModule> allHubs;
    private boolean slowMode = false;
    private boolean inverted = false;

    private boolean intakeOn = false;
    private boolean feederOn = false;

    private final Timer telemetryTimer = new Timer();
    private final Timer drawingTimer = new Timer();

    public MainTeleOp2(LinearOpMode opMode, Pose startPose) {
        this.opMode = opMode;
        this.startingPosition = startPose;
    }

    public void initialize() {
        initPedro();
        initHelpers();
        Drawing.init();
        telemetryTimer.resetTimer();
    }

    private void initPedro() {
        follower = Constants.createFollower(opMode.hardwareMap);
        follower.setStartingPose(startingPosition);
        follower.update();
    }

    private void initHelpers() {
        allHubs = opMode.hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }

        hardware = new Hardware2();
        debug = new Debug(opMode.telemetry);
        drive = new Drive(opMode, follower);
        claw = new Claw(hardware);
        slider = new Slider(hardware);
    }

    public void play() {
        drive.start();
        telemetryTimer.resetTimer();
        drawingTimer.resetTimer();
    }

    public void update() {
        for (LynxModule hub : allHubs) {
            hub.clearBulkCache();
        }

        follower.update();

        if (opMode.gamepad1.leftBumperWasPressed()) {
            slowMode = !slowMode;
        }
        if (opMode.gamepad1.yWasPressed()) {
            inverted = !inverted;
        }


        if (opMode.gamepad1.rightBumperWasPressed()) {
            claw.toggle();
            slider.setClaw(!claw.isOpen());
        }

        if (opMode.gamepad1.aWasPressed()) {
            intakeOn = !intakeOn;
            hardware.Motors().Intake().setPower(intakeOn ? 1.0 : 0.0);
        }

        if (opMode.gamepad1.xWasPressed()) {
            feederOn = !feederOn;
            hardware.Motors().Feeder().setPower(feederOn ? 0.5 : 0.0);
        }

        if (opMode.gamepad1.dpadUpWasPressed()) {
            slider.stepUp();
        } else if (opMode.gamepad1.dpadDownWasPressed()) {
            slider.stepDown();
        } else if (opMode.gamepad1.right_trigger > 0.05) {
            slider.setPower(opMode.gamepad1.right_trigger);
        } else if (opMode.gamepad1.left_trigger > 0.05) {
            slider.setPower(-opMode.gamepad1.left_trigger);
        } else if (opMode.gamepad1.bWasPressed()) {
            slider.reset();
        } else {
            slider.idle();
        }

        drive.update(slowMode, inverted);

        claw.update();

        if (drawingTimer.getElapsedTime() > 50) {
            Drawing.drawDebug(follower);
            drawingTimer.resetTimer();
        }
    }

    public void telemetry() {
        if (telemetryTimer.getElapsedTime() < 250) {
            return;
        }
        telemetryTimer.resetTimer();
        debug.addData("ticks",hardware.Motors().SliderLeft().getCurrentPosition());
        debug.update();
    }
}