package org.firstinspires.ftc.teamcode.opModes.tests;

import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.helper.Launchers;
import org.firstinspires.ftc.teamcode.helper.general.Debug;
import org.firstinspires.ftc.teamcode.helper.general.FpsCounter;
import org.firstinspires.ftc.teamcode.helper.IntakeHelper;
import org.firstinspires.ftc.teamcode.helper.hardware.Hardware;
import org.firstinspires.ftc.teamcode.helper.hardware.Motors;
import org.firstinspires.ftc.teamcode.helper.hardware.sensors.LEDs;

import java.util.Arrays;

@TeleOp
public class IntakeTest extends LinearOpMode {

    FpsCounter fps = new FpsCounter();
    Timer animTimer = new Timer();

    @Override
    public void runOpMode() throws InterruptedException {
        initialize();
        while(!isStarted()) {
            LEDs.playRedFlashAnimation();
            telemetry.addData("animTimer sec", animTimer.getElapsedTimeSeconds());
            telemetry.addData("Current Pos", LEDs.AllLEDs()[0].getPosition());
            telemetry.addData("Launchers INFO", Arrays.toString(Launchers.INSTANCE.getFilledLaunchers()));
            telemetry.addData("Launchers Color", Arrays.toString(Launchers.INSTANCE.getLauncherColorArray()));
            telemetry.update();
        }
        waitForStart();
        play();
        if (isStopRequested()){

            return;
        }
        while (opModeIsActive()) update();
    }

    private IntakeHelper intake;

    private void initialize() {
        Hardware.init();

        Launchers.INSTANCE = new Launchers();
        IntakeHelper.INSTANCE = new IntakeHelper();
        intake = IntakeHelper.INSTANCE;
    }

    private void play() {
        LEDs.setEmpty(LEDs.LauncherLeft());
        LEDs.setEmpty(LEDs.LauncherCenter());
        LEDs.setEmpty(LEDs.LauncherRight());
    }

    private void update() {
        if(gamepad1.aWasPressed()) intake.spinIntake(true);
        if(gamepad1.bWasPressed()) intake.spinIntake(false);

        fps.update();
        Debug.INSTANCE.addData("GENERAL", "");
        Debug.INSTANCE.addBreak();
        Debug.INSTANCE.addData("FPS", fps.getFps());
        Debug.INSTANCE.addData("Motor Power", Motors.Intake().getPower());
        Debug.INSTANCE.addBreak();
        Debug.INSTANCE.addData("INTAKE HELPER", "");
        Debug.INSTANCE.addBreak();
        intake.showTelemetry();

        Debug.INSTANCE.update();
        intake.update();
    }
}