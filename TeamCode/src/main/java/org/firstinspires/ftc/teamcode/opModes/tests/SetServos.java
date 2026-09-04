package org.firstinspires.ftc.teamcode.opModes.tests;

import com.bylazar.configurables.annotations.Configurable;
//import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.constants.Positions;
import org.firstinspires.ftc.teamcode.helper.general.Debug;
import org.firstinspires.ftc.teamcode.helper.hardware.actuators.MotorHelper;
import org.firstinspires.ftc.teamcode.helper.hardware.actuators.ServoHelper;

@TeleOp
@Configurable
public class SetServos extends LinearOpMode {

    public double currentPosition = 0;

    Debug debug = new Debug(telemetry);
    ServoHelper servos;
    MotorHelper motors;

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

    private void initialize() {
        servos = new ServoHelper();
        motors = new MotorHelper();
    }

    private void play() {

    }

    private void update() {
        if(gamepad1.aWasPressed()) {
            servos.BarrierLeft().setPosition(currentPosition);
            servos.BarrierRight().setPosition(currentPosition + 0.1);
        }
        if(gamepad1.xWasPressed()) {
            servos.HoodLeft().setPosition(currentPosition);
            servos.HoodRight().setPosition(currentPosition /*+ 0.05*/);
        }

        if(gamepad1.dpadUpWasPressed()) currentPosition += 0.025;
        if(gamepad1.dpadDownWasPressed()) currentPosition -= 0.025;

        if(gamepad1.yWasPressed()) {
            servos.FeedLeft().setPower(1);
            servos.FeedRight().setPower(1);
        }
        if(gamepad1.bWasPressed()) {
            servos.FeedLeft().setPower(0);
            servos.FeedRight().setPower(0);
        }


    }

    private void telemetry() {
        debug.addData("currentPosition", currentPosition);
        debug.addData("turretPosition", motors.Turret().getCurrentPosition());
        debug.update();
    }

}