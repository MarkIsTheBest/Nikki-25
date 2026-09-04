package org.firstinspires.ftc.teamcode.opModes.teleOp.runnable;

import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.constants.enums.AllianceColor;
import org.firstinspires.ftc.teamcode.opModes.teleOp.MainTeleOp;
import org.firstinspires.ftc.teamcode.opModes.teleOp.MainTeleOp2;

@TeleOp(group="Teleop2")
public class RedNearTeleOp extends LinearOpMode {

    MainTeleOp2 teleOp = new MainTeleOp2(
            this,
            new Pose(96.8, 78.5, Math.toRadians(0))
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
