package org.firstinspires.ftc.teamcode.opModes.teleOp.runnable;

import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.constants.enums.AllianceColor;
import org.firstinspires.ftc.teamcode.opModes.teleOp.MainTeleOp;

@TeleOp(group="Teleop NEAR")
public class BlueNearTeleOp extends LinearOpMode {

    MainTeleOp teleOp = new MainTeleOp(
            AllianceColor.BLUE,
            this,
            new Pose(96.8, 78.5, Math.toRadians(0)).mirror()
    );

    public void runOpMode() throws InterruptedException {
        teleOp.initialize();
        waitForStart();
        teleOp.play();
        if (isStopRequested()) return;
        while (opModeIsActive()) {
            teleOp.update();
            teleOp.telemetry();
        }
    }
}
