package org.firstinspires.ftc.teamcode.opModes.lessons;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.helper.Debug;

@Configurable
@TeleOp
public class PIDTest extends LinearOpMode {

    // ------------------ Configurable Variables ------------------
    public static double Reference = 0;      // Target position in encoder ticks
    public static double p = 0.0;            // Proportional gain
    public static double i = 0.0;            // Integral gain
    public static double d = 0.0;            // Derivative gain
    public static double maxPower = 1.0;     // Max motor output power (0 to 1)
    public static double tolerance = 5.0;    // Acceptable error in ticks
    public static double kCos = 0.0;         // Gravity compensation coefficient

    public static double ticksPerRevolution = 537.7; // Depends on motor/gearbox
    public static double gearRatio = 1.0;            // Adjust if gears used

    // ------------------ Hardware and State ------------------
    private DcMotorEx motor;
    private ElapsedTime timer = new ElapsedTime();

    private double integralSum = 0;
    private double lastError = 0;

    @Override
    public void runOpMode() throws InterruptedException {
        initialize();
        waitForStart();

        if (isStopRequested()) return;

        timer.reset();

        while (opModeIsActive()) {
            update();
        }
    }

    private void initialize() {
        motor = hardwareMap.get(DcMotorEx.class, "armMotor");
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    private void update() {
        double currentPosition = motor.getCurrentPosition();
        double power = PIDControl(Reference, currentPosition, maxPower);

        motor.setPower(power);

        // Debug output to FTC Dashboard or Driver Station
        Debug.INSTANCE.addData("Reference", Reference);
        Debug.INSTANCE.addData("Current Position", currentPosition);
        Debug.INSTANCE.addData("Error", Reference - currentPosition);
        Debug.INSTANCE.addData("Motor Power", power);
        Debug.INSTANCE.addData("Angle (deg)", radiansToDegrees(ticksToRadians(currentPosition)));
        Debug.INSTANCE.update();
    }

    // PID Control with gravity compensation
    private double PIDControl(double reference, double state, double maxSpeed) {
        double error = reference - state;
        double dt = timer.seconds();
        timer.reset();

        // Stop integrating if within tolerance to prevent windup
        if (Math.abs(error) < tolerance) {
            integralSum = 0;
            return 0;
        }

        integralSum += error * dt;
        double derivative = (error - lastError) / dt;
        lastError = error;

        // Feedforward term for gravity compensation
        double angleRad = ticksToRadians(state);
        double feedforward = kCos * Math.cos(angleRad);

        // PID + Feedforward output
        double output = (p * error) + (i * integralSum) + (d * derivative) + feedforward;

        // Clamp to maxSpeed
        output = Math.max(-maxSpeed, Math.min(output, maxSpeed));

        return output;
    }

    // Converts encoder ticks to arm angle in radians
    private double ticksToRadians(double ticks) {
        return (ticks / (ticksPerRevolution * gearRatio)) * 2 * Math.PI;
    }

    // Optional: convert to degrees for telemetry
    private double radiansToDegrees(double radians) {
        return radians * (180.0 / Math.PI);
    }
}
