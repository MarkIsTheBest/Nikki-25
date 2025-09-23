package org.firstinspires.ftc.teamcode.opModes.lessons; // <- Location of script/class file

/* \/ Here are the imports/library files references \/ */
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/*
SUMMARY : When the op mode is initialized, 'Hello World gets sent to the console'
When the op mode is started, one instance of 'Start World' and a repeating 'Update World' get sent to the console
*/

@TeleOp // Makes the OpMode accessible in the TeleOp panel
public class TelemetryLesson extends LinearOpMode { // Class that inherits from LinearOpMode(base class for opModes)

    @Override // Overrides function \/ from LinearOpMode to write custom code
    public void runOpMode() throws InterruptedException { // function that runs at init of OpMode

        telemetry.addLine("Hello World"); // add to telemetry queue the line 'Hello World'
        telemetry.update(); // read the queue and output to console (OUTPUT -> 'Hello World')
        waitForStart(); // stops the thread(code) until the start button on the Driver Station (DS) is pressed
        telemetry.addLine("Start World"); // add to telemetry queue the line 'Start World'

        while (opModeIsActive()) { // loops each frame until the OpMode is stopped, by pressing the stop button
            telemetry.addLine("Update World"); // add to telemetry queue the line 'Update World' every frame
            telemetry.update();
            /* ^ reads the queue and output to console every frame.
            (First OUTPUT -> 'Start World\nUpdate World)
            (Subsequent OUTPUT -> 'Update World') */
        }
    }
}
