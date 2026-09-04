package org.firstinspires.ftc.teamcode.opModes.tests;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.helper.general.Debug;
import org.firstinspires.ftc.teamcode.helper.hardware.Hardware;
import org.firstinspires.ftc.teamcode.subsystems.Turret;


@TeleOp
@Configurable
public class TurretTest extends LinearOpMode {

    Debug debug = new Debug(telemetry);

    private Turret turret;
    private Hardware hardware = new Hardware();
    public static double targetAngle;

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
        hardware.Motors().init();
        turret = new Turret(hardware, debug, this);
    }

    private void play() {

    }

    private void update() {
        turret.setAngle(targetAngle);

        turret.update(false);
    }

    private void telemetry() {

    }

}