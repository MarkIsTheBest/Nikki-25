package org.firstinspires.ftc.teamcode.opModes.tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.constants.Positions;
import org.firstinspires.ftc.teamcode.helper.hardware.Servos;

@TeleOp
public class AnalogServoTest extends LinearOpMode {

    int index = 0;
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
        // Init logic
    }

    private void play() {
        // start logic
    }

    private void update() {

        if(gamepad1.aWasPressed()) index++;
        if(gamepad1.bWasPressed()) index--;

        switch (index) {
            case -1:
                index = 4;
                break;
            case 0:
                Servos.setPosition(Servos.Door1(), Positions.Servo.L_D1_PREPARE);
                Servos.setPosition(Servos.Door2(), Positions.Servo.L_D2_PREPARE);
                break;
            case 1:
                Servos.setPosition(Servos.Door1(), Positions.Servo.C_D1_PREPARE);
                Servos.setPosition(Servos.Door2(), Positions.Servo.C_D2_PREPARE);
                break;
            case 2:
                Servos.setPosition(Servos.Door1(), Positions.Servo.R_D1_PREPARE);
                Servos.setPosition(Servos.Door2(), Positions.Servo.R_D2_PREPARE);
                break;
            case 3:
                Servos.setPosition(Servos.Door1(), Positions.Servo.L_D1_PARTIAL);
                Servos.setPosition(Servos.Door2(), Positions.Servo.R_D2_PARTIAL);
                break;
            case 4:
                Servos.setPosition(Servos.Holder2(), Positions.Servo.H_LAUNCH);
                break;
            case 5:
                index = 0;
                break;
        }


        telemetry.addData("Door1 isBusy", Servos.isBusy(Servos.Door1()));
        telemetry.addData("Door2 isBusy", Servos.isBusy(Servos.Door2()));
        telemetry.addData("Door1 Pos", Servos.analogToServoPos(Servos.Door1()));
        telemetry.addData("Door2 Pos", Servos.analogToServoPos(Servos.Door2()));
        telemetry.addData("index", index);
        telemetry.update();
    }
}