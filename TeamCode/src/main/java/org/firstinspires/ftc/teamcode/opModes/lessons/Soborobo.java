package org.firstinspires.ftc.teamcode.opModes.lessons; // <- Location of script/class file

/* \/ Here are the imports/library files references \/ */
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp // Makes the OpMode accessible in the TeleOp panel
public class Soborobo extends LinearOpMode { // Class that inherits from LinearOpMode(base class for opModes)

    private double leftTrigger;
    private double rightTrigger;
    private double power;
    private double maxPower;
    private double steer;
    private double steerLim;
    private boolean toggle = false;
    private boolean yLast = false;

    private DcMotor motor;
    private Servo servo;

    @Override // Overrides function \/ from LinearOpMode to write custom code
    public void runOpMode() throws InterruptedException { // function that runs at init of OpMode
        motor = hardwareMap.get(DcMotor.class, "motor");
        servo = hardwareMap.get(Servo.class, "servo");
        maxPower = 1;
        servo.setPosition(0.5);

        waitForStart(); // stops the thread(code) until the start button on the Driver Station (DS) is pressed

        while (opModeIsActive()) {  // loops each frame until the OpMode is stopped, by pressing the stop button
            steerLim = -0.26 * Math.abs(leftTrigger + rightTrigger) + 0.36;
            steerLim = clamp(steerLim, 0.1, 0.175);
            leftTrigger = map(gamepad1.left_trigger, 0, 1, 0, -maxPower);
            rightTrigger  = map(gamepad1.right_trigger, 0, 1, 0, maxPower);
            steer = map(-gamepad1.left_stick_x, -1, 1, 0.49 - steerLim, 0.5 + steerLim);

            if (gamepad1.dpadUpWasPressed()) {
                maxPower += 0.05;
            }
            else if (gamepad1.dpadDownWasPressed()) {
                maxPower -= 0.05;
            }

            if (gamepad1.y && !yLast) {
                toggle = !toggle;
                maxPower = toggle ? 1 : 0.5;
            }

            yLast = gamepad1.y;

            maxPower = clamp(maxPower, 0.25,1);
            power = leftTrigger + rightTrigger;

            motor.setPower(power);
            servo.setPosition(steer);


            telemetry.addData("leftTrigger", leftTrigger);
            telemetry.addData("rightTrigger", rightTrigger);
            telemetry.addData("steer", steer);
            telemetry.addData("power", power);
            telemetry.addData("maxPower", maxPower);
            telemetry.update();
        }
    }

    private double map(double val, double inMin, double inMax, double outMin, double outMax) {
        return (val - inMin) * (outMax - outMin)/(inMax - inMin) + outMin;
    }

    private double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }
}
