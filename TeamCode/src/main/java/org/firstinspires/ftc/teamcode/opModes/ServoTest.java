package org.firstinspires.ftc.teamcode.opModes;

import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class ServoTest extends LinearOpMode {

    Servo servo;
    double position = 0;

    @Override
    public void runOpMode() throws InterruptedException {
        initialize();
        waitForStart();
        while (opModeIsActive())
        {
            update();
        }
    }

    private void initialize()
    {
        servo = hardwareMap.get(Servo.class, "servo");
    }

    private void update()
    {
        if(gamepad1.dpadRightWasPressed())
        {
            position += 0.05;

        }
        if(gamepad1.dpadLeftWasPressed())
        {
            position -= 0.05;

        }
        if(gamepad1.aWasPressed())
        {
            updateServoPosition();
        }
        TelemetryManager telm = PanelsTelemetry.INSTANCE.getTelemetry();
        telm.addData("Position", position);
        telm.update();
    }

    private void updateServoPosition()
    {
        servo.setPosition(position);


    }
}
