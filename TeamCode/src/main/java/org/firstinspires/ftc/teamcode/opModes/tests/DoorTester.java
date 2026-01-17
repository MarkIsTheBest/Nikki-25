package org.firstinspires.ftc.teamcode.opModes.tests;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.constants.Positions;
import org.firstinspires.ftc.teamcode.helper.hardware.Servos;


@TeleOp
public class DoorTester extends LinearOpMode {

    double position = Positions.Servo.R_D2_PARTIAL;
    Servos Servos = new Servos();

    @Override
    public void runOpMode() throws InterruptedException {
        initialize();
        waitForStart();
        play();
        if (isStopRequested()) return;
        while (opModeIsActive()) update();
    }

    private void initialize() {
        Servos.init();
    }

    private void play() {
        // start logic
    }

    private void update() {

        if(gamepad1.aWasPressed()) position += 0.01;
        if(gamepad1.bWasPressed()) position -= 0.01;

        Servos.setPosition(Servos.Door2(), position);

        telemetry.addData("Door2 Servo position", position);
        telemetry.addData("Door2 Analog position", Servos.Door2Pos().getVoltage());
        telemetry.addData("Door2 isBusy", Servos.isBusy(Servos.Door2()));
        telemetry.update();
    }
}