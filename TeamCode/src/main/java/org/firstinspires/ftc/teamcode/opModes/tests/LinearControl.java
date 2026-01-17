//package org.firstinspires.ftc.teamcode.opModes.tests;
//
//import com.bylazar.configurables.annotations.Configurable;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//import com.qualcomm.robotcore.hardware.DcMotorEx;
//import com.qualcomm.robotcore.util.ElapsedTime;
//
//import org.firstinspires.ftc.teamcode.helper.general.Debug;
//
//@Configurable
//@TeleOp(name = "Linear Control", group = "TestsPID")
//public class LinearControl extends LinearOpMode {
//
//    private DcMotorEx leftSlider, rightSlider;
//
//    public static double p = 0.01;
//    public static double i = 0.0;
//    public static double d = 0.0005;
//
//    public static int targetPosition = 0; // encoder ticks
//    public static double gravityComp = 0.05; // power to hold position
//
//    private double integralSum = 0;
//    private double lastError = 0;
//    private final ElapsedTime timer = new ElapsedTime();
//
//    @Override
//    public void runOpMode() throws InterruptedException {
//        initialize();
//
//        waitForStart();
//        timer.reset();
//
//        while (opModeIsActive()) {
//            update();
//        }
//    }
//
//    private void initialize() {
//        leftSlider = hardwareMap.get(DcMotorEx.class, "leftSlider");
//        rightSlider = hardwareMap.get(DcMotorEx.class, "rightSlider");
//
//        leftSlider.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
//        rightSlider.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
//
//        leftSlider.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
//        rightSlider.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
//
//        leftSlider.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
//        rightSlider.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
//    }
//
//    private void update()
//    {
//        updatePID();
//        debug();
//    }
//
//    private void updatePID() {
//        double currentPos = (leftSlider.getCurrentPosition() + rightSlider.getCurrentPosition()) / 2.0;
//        double error = targetPosition - currentPos;
//        double dt = timer.seconds();
//
//        integralSum += error * dt;
//        double derivative = (error - lastError) / dt;
//        lastError = error;
//        timer.reset();
//
//        double output = (p * error) + (i * integralSum) + (d * derivative) + gravityComp;
//
//        output = Math.max(-1, Math.min(1, output));
//
//        leftSlider.setPower(output);
//        rightSlider.setPower(output);
//    }
//
//    private void debug() {
//        double currentPos = (leftSlider.getCurrentPosition() + rightSlider.getCurrentPosition()) / 2.0;
//        Debug.INSTANCE.addData("Target", targetPosition);
//        Debug.INSTANCE.addData("Current", currentPos);
//        Debug.INSTANCE.addData("Error", targetPosition - currentPos);
//        Debug.INSTANCE.addData("Power", leftSlider.getPower());
//        Debug.INSTANCE.update();
//    }
//}
