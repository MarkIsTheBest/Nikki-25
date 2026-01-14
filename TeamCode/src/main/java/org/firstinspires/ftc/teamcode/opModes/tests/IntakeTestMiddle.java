package org.firstinspires.ftc.teamcode.opModes.tests;

import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.constants.Positions;
import org.firstinspires.ftc.teamcode.helper.general.Debug;
import org.firstinspires.ftc.teamcode.helper.general.FpsCounter;
import org.firstinspires.ftc.teamcode.helper.IntakeHelper;
import org.firstinspires.ftc.teamcode.helper.hardware.Hardware;
import org.firstinspires.ftc.teamcode.helper.hardware.Motors;
import org.firstinspires.ftc.teamcode.helper.hardware.Servos;
import org.firstinspires.ftc.teamcode.helper.hardware.sensors.LEDs;

import java.util.Arrays;

@TeleOp
public class IntakeTestMiddle extends LinearOpMode {

    FpsCounter fps = new FpsCounter();

    @Override
    public void runOpMode() throws InterruptedException {
        initialize();
        while(!isStarted()) {
            LEDs.playRedFlashAnimation();
            //LEDs.playGradientAnimation(5);
            //LEDs.playRedWhiteAnimation(animTimer, 0.5);
            telemetry.addData("Current Pos", LEDs.AllLEDs()[0].getPosition());
            telemetry.update();
        }
        waitForStart();
        play();
        if (isStopRequested()){

            return;
        }
        while (opModeIsActive()) update();
    }

    private void initialize() {
        Hardware.init();
    }

    private void play() {
        Servos.setPosition(Servos.Holder2(), Positions.Servo.H_PREPARE_MIDDLE);
        Servos.setPosition(Servos.Door1(), 1);
        Servos.setPosition(Servos.Door2(), 0);
    }

    private void update() {
        if(gamepad1.aWasPressed()) Motors.Intake().setPower(1);
        if(gamepad1.bWasPressed()) Motors.Intake().setPower(0);
        if(gamepad1.xWasPressed()) Servos.setPosition(Servos.Holder2(), Positions.Servo.H_LAUNCH);
        if(gamepad1.yWasPressed()) Servos.setPosition(Servos.Holder2(), Positions.Servo.H_PREPARE_MIDDLE);

        if(gamepad1.rightBumperWasPressed()) for (DcMotorEx launcher : Motors.Launchers()) {
            launcher.setPower(1);
        }

        if(gamepad1.leftBumperWasPressed()) for (DcMotorEx launcher : Motors.Launchers()) {
            launcher.setPower(0);
        }

        fps.update();
        Debug.INSTANCE.addData("GENERAL", "");
        Debug.INSTANCE.addBreak();
        Debug.INSTANCE.addData("FPS", fps.getFps());
        Debug.INSTANCE.addData("Motor Power", Motors.Intake().getPower());
        Debug.INSTANCE.addBreak();
        Debug.INSTANCE.addData("INTAKE HELPER", "");
        Debug.INSTANCE.addBreak();
        Debug.INSTANCE.update();

    }
}