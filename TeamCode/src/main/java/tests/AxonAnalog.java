package tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;


@TeleOp
public class AxonAnalog extends LinearOpMode {

    AnalogInput axon1;
    AnalogInput axon2;
    Servo axonServo;
    boolean reverse = false;

    ElapsedTime timer = new ElapsedTime();
    double position = 0;

    @Override
    public void runOpMode() throws InterruptedException {
        axon1 = hardwareMap.get(AnalogInput.class, "axon1");
        axon2 = hardwareMap.get(AnalogInput.class, "axon2");
        axonServo = hardwareMap.get(Servo.class, "axon");
        waitForStart();

        while (opModeIsActive()) {
            if (gamepad1.a)
                position += 10 * timer.seconds();
            if (gamepad1.b)
                position -= 10 * timer.seconds();

            axonServo.setPosition(position);
            telemetry.addData("Axon 0 Angle", axon1.getVoltage() / 3.3 * 360);
            telemetry.addData("Axon 1 Angle", axon2.getVoltage() / 3.3 * 360);
            telemetry.update();
            timer.reset();
        }

    }
}
