package org.firstinspires.ftc.teamcode.opModes.lessons; // <- Location of script/class file

/* \/ Here are the imports/library files references \/ */
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp // Makes the OpMode accessible in the TeleOp panel
public class ClawArm extends LinearOpMode { // Class that inherits from LinearOpMode(base class for opModes)

    private DcMotorEx armMotor;
    private Servo servoClaw;
    private int position;


    @Override // Overrides function \/ from LinearOpMode to write custom code
    public void runOpMode() throws InterruptedException { // function that runs at init of OpMode

        armMotor = (DcMotorEx) hardwareMap.dcMotor.get("armMotor");
        servoClaw = hardwareMap.servo.get("servoClaw");

        armMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        waitForStart(); // stops the thread(code) until the start button on the Driver Station (DS) is pressed

        //rest: 0 up: -130 down: -240


        while (opModeIsActive()) {  // loops each frame until the OpMode is stopped, by pressing the stop button

            // Show the position of the motor on telemetry
            telemetry.addData("Encoder Position", position);
            telemetry.update();
            position = (int)LinearValueLesson.map(-gamepad1.left_stick_x,-1,1,-240,0 ) ;

            armMotor.setTargetPosition(position);
            armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            armMotor.setPower(0.25);



        }
    }
}