package org.firstinspires.ftc.teamcode.opModes.tests;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.constants.Positions;
import org.firstinspires.ftc.teamcode.helper.control.CustomFlywheelPID;
import org.firstinspires.ftc.teamcode.helper.general.Debug;
import org.firstinspires.ftc.teamcode.helper.general.MathHelper;
import org.firstinspires.ftc.teamcode.helper.hardware.Hardware;
import org.firstinspires.ftc.teamcode.helper.hardware.actuators.ServoHelper;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Turret;


@TeleOp
@Configurable
public class ShooterTest extends LinearOpMode {

    public static double rpm = 0;
    public static double turretAngle = 0;
    private double angle = 45;
    private Hardware hardware;
    private CustomFlywheelPID pid;
    private Intake intake;
    private Turret turret;
    private ServoHelper servos;

    private Timer testTimer = new Timer();

    Debug debug = new Debug(telemetry);
    Follower follower;

    private DcMotorEx flywheelLeft;
    private DcMotorEx flywheelRight;

    private boolean hasChanged = false;

    @Override
    public void runOpMode() throws InterruptedException {
        initialize();
        waitForStart();
        play();
        if (isStopRequested()) return;
        while (opModeIsActive()) {
            update();
            telemetry();
            debug.update();
        }
    }

    private void initPedro() {
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(84, 9, Math.toRadians(90)));
        follower.update();
    }

    private void initialize() {
        hardware = new Hardware();
        pid = new CustomFlywheelPID();
        flywheelLeft = hardware.Motors().LeftLauncher();
        flywheelRight = hardware.Motors().RightLauncher();
        turret = new Turret(hardware, debug, this);
        servos = new ServoHelper();
        //closeBarrier();
        //setHoodAngle(45);
        initPedro();

        intake = new Intake(this, hardware);
    }

    private void play() {
        hardware.Limelight().start();
    }

    private void update() {
        hardware.Limelight().update();
        turret.update(false);

        double power = pid.update(rpm, pid.getCurrentRPM(flywheelLeft));
        flywheelRight.setPower(power);
        flywheelLeft.setPower(power);

        intake.update();
        intake.input();
        follower.update();

        if(gamepad1.xWasPressed()) {
            hardware.Motors().RightLauncher().setPower(0);
            hardware.Motors().LeftLauncher().setPower(0);
        }

        if(gamepad1.rightBumperWasPressed()) openBarrier();
        if(gamepad1.leftBumperWasPressed()) closeBarrier();

        if(gamepad1.dpadUpWasPressed()) angle += 2.5;
        if(gamepad1.dpadDownWasPressed()) angle -= 2.5;

        if(gamepad2.aWasPressed()) {
            flywheelLeft.setPower(1);
        }

        if(gamepad2.bWasPressed()) {
            flywheelLeft.setPower(0);
        }

        if(gamepad2.xWasPressed()) {
            flywheelRight.setPower(1);
        }

        if(gamepad2.rightBumperWasPressed()) {
            hardware.Servos().FeedLeft().setPower(1);
            hardware.Servos().FeedRight().setPower(1);
        }

        if(gamepad2.yWasPressed()) {
            flywheelRight.setPower(0);
        }

        setHoodAngle(angle);

//        if(testTimer.getElapsedTime() <= 100) return;
//
//        if(!hasChanged) {
//            turretAngle += 1;
//            hasChanged = true;
//            testTimer.resetTimer();
//        } else {
//            turretAngle -= 1;
//            hasChanged = false;
//            testTimer.resetTimer();
//        }
//        if(turretAngle <= 65 && !hasChanged) {
//            turretAngle += 1;
//        }
//        else {
//            hasChanged = true;
//        }
//
//        if(turretAngle >= -80 && hasChanged) {
//            turretAngle -= 1;
//        }
//        else {
//            hasChanged = false;
//        }

        //turretAngle = -21;

    }

    private void telemetry() {
        debug.addData("Current RPM", -pid.getCurrentRPM(hardware.Motors().LeftLauncher()));
        debug.addData("Target RPM", rpm);
        debug.addData("Angle", angle);
        debug.addData("Barrier Position", hardware.Servos().BarrierLeft().getPosition());
        debug.addData("Limelight Distance", Math.sqrt(Math.pow(hardware.Limelight().Distance() * 39.37, 2) - Math.pow(22.801, 2)));
        debug.addData("Pinpoint Distance", getDistanceToGoal());
        debug.addData("Current Turret Angle", turret.getAngle());
        debug.addData("Target Turret Angle", turret.getTargetAngle());
    }

    public void closeBarrier() {
        moveBarrier(Positions.Servo.BARRIER_OPENED);
    }

    public void openBarrier() {
        moveBarrier(Positions.Servo.BARRIER_CLOSED);
    }

    public void setHoodAngle(double angle) {
        double pos = -0.03055555555 * angle + 1.525;
        moveHood(pos);
    }
    private void moveHood(double pos) {
        hardware.Servos().HoodLeft().setPosition(pos);
        hardware.Servos().HoodRight().setPosition(pos + 0.05);
    }

    private void moveBarrier(double pos) {
        hardware.Servos().BarrierLeft().setPosition(pos);
        hardware.Servos().BarrierRight().setPosition(pos + 0.1);
    }

    private double getDistanceToGoal() {
        return MathHelper.dist(follower.getPose(), Positions.Field.RED_GOAL) - 10;
    }

}