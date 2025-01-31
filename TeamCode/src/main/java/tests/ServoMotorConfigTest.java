package tests;

import static subsystems.Func.SetMotorPosition;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import subsystems.hardware.Motors;
import subsystems.hardware.Servos;

@TeleOp(name = "ServoMotorConfigTest", group = "Tests")
@Config
public final class ServoMotorConfigTest extends LinearOpMode {

    private static final int SERVO_PIN_COUNT = 6;

    // Motor configuration
    @Config
    public static class M_Drive {
        public static int _0_LeftFrontPosition = 0;
        public static int _1_RightFrontPosition = 0;
        public static int _2_LeftBackPosition = 0;
        public static int _3_RightBackPosition = 0;
    }

    @Config
    public static class M_Sliders {
        public static int _4_ArmLeftPosition = 0;
        public static int _5_ArmRightPosition = 0;
    }

    // Servo configuration
    private Servo servosCH[] = new Servo[SERVO_PIN_COUNT]; // Servos on Control Hub
    private Servo servosEH[] = new Servo[SERVO_PIN_COUNT]; // Servos on Expansion Hub

    public static double servoPosition = 0;
    public static int motorID;
    public static int servoIndex = 0;
    public static boolean isCH = true;
    public static boolean apply = false;
    public static boolean allVipers = false;

    @Override
    public void runOpMode() throws InterruptedException {
        initHardware();
        waitForStart();
        while (opModeIsActive()) {
            update();
        }
    }

    private void initHardware() {
        // Initialize hardware
        Servos.init(hardwareMap);
        Motors.init(hardwareMap);

        // Initialize all motors
        for (int i = 0; i < Motors.allMotors.length; i++) {
            if (Motors.allMotors[i] != null) {
                Motors.allMotors[i].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                Motors.allMotors[i].setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            }
        }

        servosEH[0] = Servos.linkageLeft;
        servosEH[1] = Servos.linkageRight;
        servosEH[2] = Servos.rotateBody;
        servosEH[3] = Servos.rotateHead;
        servosEH[4] = Servos.rotateClaw;
        servosEH[5] = Servos.claw;

        servosCH[0] = Servos.rotateAxis;
        servosCH[1] = Servos.rotateBackClaw;
        servosCH[2] = Servos.rotateBackClaw;
        servosCH[3] = Servos.backClaw;
    }

    private void update() {
        // Motor control
        if (apply)
        {
            if(isCH)
                servosCH[servoIndex].setPosition(servoPosition);
            else servosEH[servoIndex].setPosition(servoPosition);

            switch (motorID) {
                // Drive motors
                case 0: SetMotorPosition(Motors.leftFront, M_Drive._0_LeftFrontPosition); break;
                case 1: SetMotorPosition(Motors.rightFront, M_Drive._1_RightFrontPosition); break;
                case 2: SetMotorPosition(Motors.leftRear, M_Drive._2_LeftBackPosition); break;
                case 3: SetMotorPosition(Motors.rightRear, M_Drive._3_RightBackPosition); break;
                // Slider motors
                case 4: SetMotorPosition(Motors.verticalLeft, M_Sliders._4_ArmLeftPosition); break;
                case 5: SetMotorPosition(Motors.verticalRight, M_Sliders._5_ArmRightPosition); break;
            }
            if(allVipers)
            {
                SetMotorPosition(Motors.verticalLeft, M_Sliders._4_ArmLeftPosition);
                SetMotorPosition(Motors.verticalRight, M_Sliders._5_ArmRightPosition);
            }


            apply = false;
        }

        // Debugging
        }

}
