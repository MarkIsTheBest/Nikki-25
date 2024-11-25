package org.firstinspires.ftc.teamcode.tests.teleop;

import static org.firstinspires.ftc.teamcode.subsystems.Func.lastTime;
import static org.firstinspires.ftc.teamcode.subsystems.Func.map;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.OpenCV;
import org.firstinspires.ftc.teamcode.subsystems.hardware.Servos;

@TeleOp(name = "CamClawTest", group = "TeleopTests")
public class CamClawTest extends LinearOpMode
{
    @Override
    public void runOpMode() throws InterruptedException
    {
        OpenCV.init(hardwareMap);
        Servos.init(hardwareMap);
        waitForStart();
        while (opModeIsActive())
        {
            RotateClaw(OpenCV.getBlueAngle());
        }
    }

    private void RotateClaw(double angle)
    {
        double newAngle = map(angle, 0, 180, 1, 0);
        Servos.intakeHorizontal.setPosition(newAngle);
        telemetry.addData("Angle", angle);
        telemetry.update();
    }
}
