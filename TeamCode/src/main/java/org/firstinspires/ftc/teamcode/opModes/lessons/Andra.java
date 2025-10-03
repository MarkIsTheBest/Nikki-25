package org.firstinspires.ftc.teamcode.opModes.lessons; // <- Location of script/class file

/* \/ Here are the imports/library files references \/ */
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp // Makes the OpMode accessible in the TeleOp panel
public class Andra extends LinearOpMode { // Class that inherits from LinearOpMode(base class for opModes)
DcMotor mark;
Servo mark1;

    @Override // Overrides function \/ from LinearOpMode to write custom code
    public void runOpMode() throws InterruptedException { // function that runs at init of OpMode
    mark = hardwareMap.dcMotor.get("motor");
    mark1 = hardwareMap.servo.get("servo");

        waitForStart(); // stops the thread(code) until the start button on the Driver Station (DS) is pressed

        while (opModeIsActive()) {  // loops each frame until the OpMode is stopped, by pressing the stop button
            if(gamepad1.aWasPressed()){
                mark.setPower(1);
            }
        }
    }
}
