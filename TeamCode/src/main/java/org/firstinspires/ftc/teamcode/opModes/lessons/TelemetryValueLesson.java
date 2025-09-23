package org.firstinspires.ftc.teamcode.opModes.lessons;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class TelemetryValueLesson extends LinearOpMode {

    int value = 0;

    @Override
    public void runOpMode() throws InterruptedException {

        waitForStart();

        while (opModeIsActive()) {
            if(gamepad1.aWasPressed()) value += 1;
            else if (gamepad1.bWasPressed()) value -= 1;

            telemetry.addData("Value", value);
            telemetry.update();
        }
    }
}

