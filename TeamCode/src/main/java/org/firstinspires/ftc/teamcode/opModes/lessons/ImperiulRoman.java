package org.firstinspires.ftc.teamcode.opModes.lessons; // <- Location of script/class file

/* \/ Here are the imports/library files references \/ */
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp // Makes the OpMode accessible in the TeleOp panel
public class ImperiulRoman extends LinearOpMode {

    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;
    private DcMotor intake;
    private DcMotor launcher;
    private Servo split;
    private Servo barrierRight;
    private Servo barrierLeft;
    private float toggle = -1;

    @Override
    public void runOpMode() throws InterruptedException {

        frontLeft = hardwareMap.dcMotor.get("frontLeft");
        frontRight = hardwareMap.dcMotor.get("frontRight");
        backLeft = hardwareMap.dcMotor.get("backLeft");
        backRight = hardwareMap.dcMotor.get("backRight");
        intake = hardwareMap.dcMotor.get("intake");
        launcher = hardwareMap.dcMotor.get("launcher");
        split = hardwareMap.servo.get("split");
        barrierRight = hardwareMap.servo.get("barrierRight");
        barrierLeft = hardwareMap.servo.get("barrierLeft");



        backRight.setDirection(DcMotorSimple.Direction.REVERSE);
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE );
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        waitForStart();

        while (opModeIsActive()) {
            double y = -gamepad1.left_stick_y; // Remember, Y stick value is reversed
            double x = gamepad1.left_stick_x; // Counteract imperfect strafing
            double rx = gamepad1.right_stick_x;


            // Denominator is the largest motor power (absolute value) or 1
            // This ensures all the powers maintain the same ratio,
            // but only if at least one is out of the range [-1, 1]
            //double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            double frontLeftPower = (y + x + rx); // denominator;
            double backLeftPower = (y - x + rx); // denominator;
            double frontRightPower = (y - x - rx); // denominator;
            double backRightPower = (y + x - rx); // denominator;

            frontLeft.setPower(frontLeftPower);
            backLeft.setPower(backLeftPower);
            frontRight.setPower(frontRightPower);
            backRight.setPower(backRightPower);

            if(gamepad1.aWasPressed()) toggle = -toggle;
            if (toggle == 1) intake.setPower(1);
            else intake.setPower(0);

            telemetry.addData("toggle", toggle);
            telemetry.addData("y", y);
            telemetry.addData("x", x);
            telemetry.addData("rx", rx);
            telemetry.update();
        }
    }


}


// cine vede asta, sa stie ca suge pula tare de tot <3