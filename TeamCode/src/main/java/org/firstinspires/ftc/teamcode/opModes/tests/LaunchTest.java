package org.firstinspires.ftc.teamcode.opModes.tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.constants.enums.Motif;
import org.firstinspires.ftc.teamcode.helper.LaunchHelper;
import org.firstinspires.ftc.teamcode.helper.general.Debug;
import org.firstinspires.ftc.teamcode.helper.general.FpsCounter;
import org.firstinspires.ftc.teamcode.helper.MotorHelper;
import org.firstinspires.ftc.teamcode.helper.hardware.Hardware;
import org.firstinspires.ftc.teamcode.helper.hardware.Motors;

@TeleOp
public class LaunchTest extends OpMode {

    FpsCounter fps = new FpsCounter();
    LaunchHelper launchHelper;
    int RPMIncrease = 100;

    @Override
    public void init() {

        LaunchHelper.INSTANCE =  new LaunchHelper();
        launchHelper = LaunchHelper.INSTANCE;
    }

    @Override
    public void loop() {

        if(gamepad1.aWasPressed()) {
            launchHelper.startLaunchSequence();
        }
        if(gamepad1.dpadUpWasPressed()) {
            launchHelper.SetTargetRPM(launchHelper.GetTargetRPM() + RPMIncrease);
        }
        if(gamepad1.dpadDownWasPressed()) {
            launchHelper.SetTargetRPM(launchHelper.GetTargetRPM() - RPMIncrease);
        }

        if(gamepad1.bWasPressed()) {
            launchHelper.setMotif(Motif.PPG);
        }
        if(gamepad1.xWasPressed()) {
            launchHelper.setMotif(Motif.GPP);
        }
        if(gamepad1.yWasPressed()) {
            launchHelper.setMotif(Motif.PGP);
        }

        launchHelper.update();
        fps.update();
        telemetry();
    }

    private void telemetry() {
        try {
            Debug.INSTANCE.addData("GENERAL", "");
            Debug.INSTANCE.addBreak();
            Debug.INSTANCE.addData("FPS", fps.getFps());

            Debug.INSTANCE.addBreak();
            Debug.INSTANCE.addData("LAUNCHER", "");
            Debug.INSTANCE.addBreak();
            launchHelper.showTelemetry();

            Debug.INSTANCE.update();
        } catch (Exception ex) {}
    }
}
