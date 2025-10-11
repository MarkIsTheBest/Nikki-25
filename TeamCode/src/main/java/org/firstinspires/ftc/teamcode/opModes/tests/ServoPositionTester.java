package org.firstinspires.ftc.teamcode.opModes.tests;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.helper.Debug;
import org.firstinspires.ftc.teamcode.helper.hardware.Motors;
import org.firstinspires.ftc.teamcode.helper.hardware.Servos;

@TeleOp(name = "Servo Tester", group = "Tests")
@Configurable
public class ServoPositionTester extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        initialize();
        waitForStart();
        if (isStopRequested()) return;

        while (opModeIsActive()) {
            update();
        }
    }

    private final ServoPos[] servos = new ServoPos[Servos.INSTANCE.AllServos().length];

    private void initialize() {
        initArrays();
        updateServoStates();
    }

    private void initArrays() {
        for (int i = 0; i < servos.length; i++) {
            servos[i] = new ServoPos();
        }
    }

    private void update() {
        updateServoStates();
        updatePositions();
        onDebug();
    }

    private void onDebug() {
        if (PT_Servos.activeTelemetry) {
            for (ServoPos servo : servos) {
                Debug.INSTANCE.addData(String.valueOf(servo.servo.getPortNumber()), servo.servo.getPosition());
            }
        }

        Debug.INSTANCE.update();
    }

    private void updateServoStates() {
        servos[0].position = PT_Servos._0_servo;

        servos[0].active = PT_Servos._0_active;

        servos[0].servo = Servos.INSTANCE.Servo();
    }

    private void updatePositions() {
        for (int i = 0; i < Servos.INSTANCE.AllServos().length; i++) {
            if (servos[i].active && servos[i].servo.getPosition() != servos[i].position) servos[i].servo.setPosition(servos[i].position);
        }
    }

    @Configurable
    public static class PT_Servos {
        public static boolean activeTelemetry;

        public static double _0_servo;
        public static boolean _0_active;
    }

    private static class ServoPos {
        public Servo servo;
        public double position;
        public boolean active;
    }
}
