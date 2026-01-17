//package org.firstinspires.ftc.teamcode.opModes.tuners;
//
//import com.bylazar.configurables.annotations.Configurable;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.hardware.DcMotorEx;
//import com.qualcomm.robotcore.hardware.PIDFCoefficients;
//
//import org.firstinspires.ftc.teamcode.helper.general.Debug;
//import org.firstinspires.ftc.teamcode.helper.MotorHelper;
//
//@Configurable
//@TeleOp(name = "Arm Control Tuner", group = "Tuners")
//public class ArmControlTuner extends LinearOpMode {
//
//    private DcMotorEx motor;
//
//    public static double maxPower;
//    public static double motorTicksPerRev;
//
//    public static double p, i, d, f;
//    public static double targetAngle;
//
//    @Override
//    public void runOpMode() throws InterruptedException {
//        initialize();
//        waitForStart();
//        play();
//        if (isStopRequested()) return;
//        while (opModeIsActive()) update();
//    }
//
//    private void initialize() {
//        motor = hardwareMap.get(DcMotorEx.class, "motor");
//        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//    }
//
//    private void play() {
//        // start logic
//    }
//
//    private void update() {
//        MotorHelper.setArmAngle(motor, targetAngle, motorTicksPerRev, maxPower, new PIDFCoefficients(p, i, d, f));
//        Debug.INSTANCE.addData("Tick Position", motor.getCurrentPosition());
//        Debug.INSTANCE.update();
//    }
//}