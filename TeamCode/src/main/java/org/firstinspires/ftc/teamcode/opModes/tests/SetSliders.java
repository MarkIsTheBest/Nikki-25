package org.firstinspires.ftc.teamcode.opModes.tests;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.helper.general.Debug;
import org.firstinspires.ftc.teamcode.helper.hardware.actuators.MotorHelper;
import org.firstinspires.ftc.teamcode.helper.hardware.actuators.MotorHelper2;
import org.firstinspires.ftc.teamcode.helper.hardware.actuators.ServoHelper;

@TeleOp
@Configurable
public class SetSliders extends LinearOpMode {

    public int currentPosition = 0;

    Debug debug = new Debug(telemetry);
    MotorHelper2 motors;

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
        motors = new MotorHelper2();
    }

    private void play() {

    }

    private void update() {

        if(gamepad1.dpadUpWasPressed()) currentPosition += 50;
        if(gamepad1.dpadDownWasPressed()) currentPosition -= 50;

        motors.SliderRight().setTargetPosition(currentPosition);
        motors.SliderLeft().setTargetPosition(currentPosition);

        motors.SliderRight().setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motors.SliderLeft().setMode(DcMotor.RunMode.RUN_TO_POSITION);

        motors.SliderRight().setPower(1);
        motors.SliderLeft().setPower(1);
    }

    private void telemetry() {
        debug.addData("currentPosition", currentPosition);

        debug.update();
    }

}