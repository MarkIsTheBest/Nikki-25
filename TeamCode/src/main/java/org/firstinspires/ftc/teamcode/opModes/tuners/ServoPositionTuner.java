package org.firstinspires.ftc.teamcode.opModes.tuners;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.helper.general.Debug;
import org.firstinspires.ftc.teamcode.helper.hardware.Servos;

@TeleOp(name = "Servo Position Tuner", group = "Tuners")
@Configurable
public class ServoPositionTuner extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        initialize();
        waitForStart();
        if (isStopRequested()) return;

        while (opModeIsActive()) {
            update();
        }
    }

    private ServoPos[] servos;

    private void initialize() {
        Servos.init();
        servos = new ServoPos[Servos.AllServos().length];
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
        servos[0].position = PT_Servos.door1_position;
        servos[0].active = PT_Servos.door1_active;
        servos[0].servo = Servos.Door1();

        servos[1].position = PT_Servos.door2_position;
        servos[1].active = PT_Servos.door2_active;
        servos[1].servo = Servos.Door2();

        servos[2].position = PT_Servos.holder1_position;
        servos[2].active = PT_Servos.holder1_active;
        servos[2].servo = Servos.Holder1();

        servos[3].position = PT_Servos.holder2_position;
        servos[3].active = PT_Servos.holder2_active;
        servos[3].servo = Servos.Holder2();

        servos[4].position = PT_Servos.holder3_position;
        servos[4].active = PT_Servos.holder3_active;
        servos[4].servo = Servos.Holder3();
    }

    private void updatePositions() {
        for (int i = 0; i < Servos.AllServos().length; i++) {
            if (servos[i].active && servos[i].servo.getPosition() != servos[i].position) servos[i].servo.setPosition(servos[i].position);
        }
    }

    @Configurable
    public static class PT_Servos {
        public static boolean activeTelemetry;

        public static double door1_position;
        public static boolean door1_active;

        public static double door2_position;
        public static boolean door2_active;

        public static double holder1_position;
        public static boolean holder1_active;

        public static double holder2_position;
        public static boolean holder2_active;

        public static double holder3_position;
        public static boolean holder3_active;
    }

    private static class ServoPos {
        public Servo servo;
        public double position;
        public boolean active;
    }
}
