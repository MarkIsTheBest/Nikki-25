package org.firstinspires.ftc.teamcode.opModes;

import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp
public class MotorTest extends LinearOpMode {

    DcMotorEx motor;

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
        motor = hardwareMap.get(DcMotorEx.class, "motor");
        motor.setPower(1);
    }

    private void update()
    {
        if(gamepad1.a) motor.setPower(1);
        else if (gamepad1.b) motor.setPower(-1);
    }
}
