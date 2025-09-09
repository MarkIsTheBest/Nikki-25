package org.firstinspires.ftc.teamcode.opModes;

import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.subsystems.hardware.Motors;

@TeleOp
public class MotorTest extends LinearOpMode {

    DcMotorEx motor;
    DcMotorEx motor1;
    int position = 0;
    boolean isMotor1 = false;

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
        motor = hardwareMap.get(DcMotorEx.class, "rotateSlider");
        motor1 = hardwareMap.get(DcMotorEx.class, "extendSlider");
        motor1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        updateMotorPosition();
        //motor.setPower(0.1);
    }

    private void update()
    {

        if(gamepad1.startWasPressed())
        {
            isMotor1 = !isMotor1;
        }
        if(gamepad1.dpadRightWasPressed())
        {
            position += 50;
            //updateMotorPosition();
        }
        if(gamepad1.dpadLeftWasPressed())
        {
            position -= 50;
            //updateMotorPosition();
        }

        if(gamepad1.aWasPressed())
        {
            updateMotorPosition();
        }

        TelemetryManager telm = PanelsTelemetry.INSTANCE.getTelemetry();
        telm.addData("Position", position);
        telm.update();
    }

    private void updateMotorPosition()
    {
        if(isMotor1)
        {
            Motors.setPosition(motor1, position,1 );
        }
        else
        {
            Motors.setPosition(motor, position,1 );
        }

    }
}
