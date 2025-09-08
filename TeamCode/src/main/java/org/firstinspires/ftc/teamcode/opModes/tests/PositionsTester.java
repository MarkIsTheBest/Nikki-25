package org.firstinspires.ftc.teamcode.opModes.tests;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.helper.Debug;
import org.firstinspires.ftc.teamcode.helper.hardware.Motors;
import org.firstinspires.ftc.teamcode.helper.hardware.Servos;

@TeleOp
@Configurable
public class PositionsTester extends LinearOpMode {

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
    private final MotorsPos[] motors = new MotorsPos[Motors.INSTANCE.AllMotors().length];

    private void initialize() {
        initArrays();
        updateMotorStates();
        updateServoStates();
    }

    private void initArrays() {
        for (int i = 0; i < servos.length; i++) {
            servos[i] = new ServoPos();
        }

        for (int i = 0; i < motors.length; i++) {
            motors[i] = new MotorsPos();
        }
    }

    private void update() {
        updateServoStates();
        updateMotorStates();
        updatePositions();
        onDebug();
    }

    private void onDebug() {
        if (PT_Servos.activeTelemetry) {
            for (ServoPos servo : servos) {
                Debug.INSTANCE.addData(String.valueOf(servo.servo.getPortNumber()), servo.servo.getPosition());
            }
        }

        if (PT_Motors.activeTelemetry) {
            for (MotorsPos motor : motors) {
                Debug.INSTANCE.addData(motor.motor.getDeviceName(), motor.motor.getCurrentPosition());
            }
        }

        Debug.INSTANCE.update();
    }

    private void updateServoStates() {
        servos[0].position = PT_Servos._0_servo;

        servos[0].active = PT_Servos._0_active;

        servos[0].servo = Servos.INSTANCE.Servo();
    }

    private void updateMotorStates() {
        motors[0].position = PT_Motors._0_leftFront;
        motors[1].position = PT_Motors._1_leftRear;
        motors[2].position = PT_Motors._2_rightFront;
        motors[3].position = PT_Motors._3_rightRear;

        motors[0].active = PT_Motors._0_active;
        motors[1].active = PT_Motors._1_active;
        motors[2].active = PT_Motors._2_active;
        motors[3].active = PT_Motors._3_active;

        motors[0].motor = Motors.INSTANCE.LeftFront();
        motors[1].motor = Motors.INSTANCE.LeftRear();
        motors[2].motor = Motors.INSTANCE.RightFront();
        motors[3].motor = Motors.INSTANCE.RightRear();
    }

    private void updatePositions() {
        for (int i = 0; i < Servos.INSTANCE.AllServos().length; i++) {
            if (servos[i].active && servos[i].servo.getPosition() != servos[i].position) servos[i].servo.setPosition(servos[i].position);
        }

        for (int i = 0; i < Motors.INSTANCE.AllMotors().length; i++) {
            if (motors[i].active) Motors.setMotorPosition(motors[i].motor, motors[i].position);
            else motors[i].motor.setPower(0);
        }
    }

    @Configurable
    public static class PT_Servos {
        public static boolean activeTelemetry;

        public static double _0_servo;
        public static boolean _0_active;
    }

    @Configurable
    public static class PT_Motors {
        public static boolean activeTelemetry;

        public static int _0_leftFront;
        public static boolean _0_active;

        public static int _1_leftRear;
        public static boolean _1_active;

        public static int _2_rightFront;
        public static boolean _2_active;

        public static int _3_rightRear;
        public static boolean _3_active;
    }

    private static class ServoPos {
        public Servo servo;
        public double position;
        public boolean active;
    }

    private static class MotorsPos {
        public DcMotorEx motor;
        public int position;
        public boolean active;
    }
}
