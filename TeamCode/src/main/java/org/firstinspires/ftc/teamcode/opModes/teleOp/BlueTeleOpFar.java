package org.firstinspires.ftc.teamcode.opModes.teleOp;

import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.constants.enums.AllianceColor;

@TeleOp(name="BlueTeleOpFar", group="TeleOp")
public class BlueTeleOpFar extends LinearOpMode {

    MainTeleOp teleOp = new MainTeleOp(AllianceColor.BLUE, this,new Pose(107.57839721254358, 9.630662020905914, Math.toRadians(90)).mirror());

    public void runOpMode() throws InterruptedException {
        teleOp.initialize();
        waitForStart();
        teleOp.play();
        if (isStopRequested()) return;
        while (opModeIsActive()) teleOp.update();
    }
}
