package org.firstinspires.ftc.teamcode.opModes.teleOp.runnable;

import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.constants.enums.AllianceColor;
import org.firstinspires.ftc.teamcode.opModes.teleOp.MainTeleOp;

@TeleOp(group="Teleop FAR")
public class BlueFarTeleOp extends LinearOpMode {

    MainTeleOp teleOp = new MainTeleOp(
            AllianceColor.BLUE,
            this,
            new Pose(108, 15, Math.toRadians(90)).mirror()
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
