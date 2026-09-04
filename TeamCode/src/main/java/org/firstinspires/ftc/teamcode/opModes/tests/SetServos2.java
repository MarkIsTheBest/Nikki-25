package org.firstinspires.ftc.teamcode.opModes.tests;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.helper.general.Debug;
import org.firstinspires.ftc.teamcode.helper.hardware.actuators.MotorHelper;
import org.firstinspires.ftc.teamcode.helper.hardware.actuators.ServoHelper;
import org.firstinspires.ftc.teamcode.helper.hardware.actuators.ServoHelper2;

@TeleOp
@Configurable
public class SetServos2 extends LinearOpMode {

    public double currentPosition = 0;

    Debug debug = new Debug(telemetry);
    ServoHelper2 servos;

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
        servos = new ServoHelper2();
    }

    private void play() {

    }

    private void update() {
        if(gamepad1.dpadUpWasPressed()) currentPosition += 0.1;
        if(gamepad1.dpadDownWasPressed()) currentPosition -= 0.1;
        servos.Claw().setPosition(currentPosition);

    }

    private void telemetry() {
        debug.addData("currentPosition", currentPosition);
        debug.update();
    }

}