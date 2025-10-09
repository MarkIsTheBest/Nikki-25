package org.firstinspires.ftc.teamcode.opModes.tests;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.helper.Debug;

@Configurable
public class CustomPID extends LinearOpMode {

    public static double Reference;

    DcMotorEx motor;

    double integralSum = 0;
    double p = 0;
    double i = 0;
    double d = 0;
    double f = 0;

    ElapsedTime timer = new ElapsedTime();
    double lastError = 0;

    @Override
    public void runOpMode() throws InterruptedException {
        initialize();
        waitForStart();
        play();
        if (isStopRequested()) return;
        while (opModeIsActive()) update();
    }

    private void initialize() {
        motor = hardwareMap.get(DcMotorEx.class, "motor");
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    private void play() {
        // start logic
    }

    private void update() {
        double state = motor.getVelocity();
        double power = PIDControl(Reference, state);
        motor.setPower(power);

        Debug.INSTANCE.addData("Reference", Reference);
        Debug.INSTANCE.addData("State", state);
        Debug.INSTANCE.update();
    }

    public double PIDControl(double reference, double state) {
        double error = reference - state;
        integralSum += error * timer.seconds();
        double derivative = (error - lastError) / timer.seconds();
        lastError = error;

        timer.reset();

        return (error * p) + (derivative * d) + (integralSum * i) + (reference * f);

    }
}