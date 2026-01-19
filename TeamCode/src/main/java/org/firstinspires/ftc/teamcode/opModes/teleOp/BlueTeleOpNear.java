package org.firstinspires.ftc.teamcode.opModes.teleOp;

import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.constants.enums.AllianceColor;

@TeleOp(name="BlueTeleOpNear", group="TeleOp")
public class BlueTeleOpNear extends LinearOpMode {

    MainTeleOp teleOp = new MainTeleOp(AllianceColor.BLUE, this,new Pose(125.30662020905925, 103.45644599303135, Math.toRadians(-90)).mirror());

    public void runOpMode() throws InterruptedException {
        teleOp.initialize();
        waitForStart();
        teleOp.play();
        if (isStopRequested()) return;
        while (opModeIsActive()) teleOp.update();
    }
}
