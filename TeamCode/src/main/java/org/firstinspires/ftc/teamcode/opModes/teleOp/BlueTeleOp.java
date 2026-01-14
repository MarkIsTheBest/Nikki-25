package org.firstinspires.ftc.teamcode.opModes.teleOp;

import static org.firstinspires.ftc.onbotjava.OnBotJavaManager.initialize;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.constants.enums.AllianceColor;

@TeleOp(name="BlueTeleOp", group="TeleOp")
public class BlueTeleOp extends LinearOpMode {

    MainTeleOp teleOp = new MainTeleOp(AllianceColor.BLUE, this);

    public void runOpMode() throws InterruptedException {
        initialize();
        waitForStart();
        teleOp.play();
        if (isStopRequested()) return;
        while (opModeIsActive()) teleOp.update();
    }
}
