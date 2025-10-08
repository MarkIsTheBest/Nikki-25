package org.firstinspires.ftc.teamcode.opModes.lessons; // <- Location of script/class file

/* \/ Here are the imports/library files references \/ */
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp // Makes the OpMode accessible in the TeleOp panel
public class EncoderTester extends LinearOpMode { // Class that inherits from LinearOpMode(base class for opModes)

    private DcMotor armMotor;

    @Override // Overrides function \/ from LinearOpMode to write custom code
    public void runOpMode() throws InterruptedException { // function that runs at init of OpMode

        armMotor = hardwareMap.dcMotor.get("armMotor");

        waitForStart(); // stops the thread(code) until the start button on the Driver Station (DS) is pressed

        while (opModeIsActive()) {  // loops each frame until the OpMode is stopped, by pressing the stop button

            int position = armMotor.getCurrentPosition();

            // Show the position of the motor on telemetry
            telemetry.addData("Encoder Position", position);
            telemetry.update();

            if(gamepad1.aWasPressed()){
                armMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            }

        }
    }
}