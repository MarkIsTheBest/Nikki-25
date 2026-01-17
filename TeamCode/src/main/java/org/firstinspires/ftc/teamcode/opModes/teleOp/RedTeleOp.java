package org.firstinspires.ftc.teamcode.opModes.teleOp;

import static org.firstinspires.ftc.onbotjava.OnBotJavaManager.initialize;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.constants.enums.AllianceColor;

@TeleOp(name="RedTeleOp", group="TeleOp")
public class RedTeleOp extends LinearOpMode {

    MainTeleOp teleOp = new MainTeleOp(AllianceColor.RED, this);

    public void runOpMode() throws InterruptedException {
        teleOp.initialize();
        waitForStart();
        teleOp.play();
        if (isStopRequested()) return;
        while (opModeIsActive()) teleOp.update();
    }
}
