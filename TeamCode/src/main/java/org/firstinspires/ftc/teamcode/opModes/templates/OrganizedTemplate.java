package org.firstinspires.ftc.teamcode.opModes.templates;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.helper.MotorHelper;
import org.firstinspires.ftc.teamcode.helper.general.Debug;
import org.firstinspires.ftc.teamcode.helper.hardware.Motors;

@TeleOp
@Configurable
public class OrganizedTemplate extends LinearOpMode {

    private Debug debug = new Debug(telemetry);
    public static double TargetRPM = 0;
    private double lastTargetRPM = 0;

    @Override
    public void runOpMode() throws InterruptedException {
        initialize();
        waitForStart();
        play();
        if (isStopRequested()) return;
        while (opModeIsActive()) update();
    }

    private void initialize() {
        Motors.init();
    }

    private void play() {
        // start logic
//        for (DcMotorEx motor : Motors.Launchers()) {
//            motor.setPower(1);
//        }
    }

    private void update() {
        if(lastTargetRPM != TargetRPM) {

            for (DcMotorEx Launcher: Motors.Launchers()) {
                Launcher.setPower(TargetRPM);
            }
            lastTargetRPM = TargetRPM;
        }

        debug.addData("RPM MOTOR Left", MotorHelper.getCurrentRPM(Motors.Launchers()[1]));
        debug.addData("RPM MOTOR Left", MotorHelper.getCurrentRPM(Motors.Launchers()[0]));
        debug.addData("Target Power MOTOR", TargetRPM);
        debug.update();
    }
}