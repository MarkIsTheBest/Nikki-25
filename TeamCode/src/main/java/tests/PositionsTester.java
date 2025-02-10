package tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import subsystems.Func;
import subsystems.hardware.Motors;
import subsystems.hardware.Servos;

/*
TODO calorie=calorieupdated.
                te rog fute ma
        te rog lasa ma
        te iubesc
        lasa ma sa mor
        nu. te getIubesc
                gras prost
                        nigger
    lui iusti nu i plac nergi
        il iubesc
*/

@TeleOp(name = "Positions Tester", group = "Tests")
public class PositionsTester extends LinearOpMode
{
    @Config
    public static class PT_Servos
    {
        public static boolean activeTelemetry;

        public static double _0_rotateAxis;
        public static boolean _0_active;

        public static double _1_rotateBody;
        public static boolean _1_active;

        public static double _2_rotateHead;
        public static boolean _2_active;

        public static double _3_rotateClaw;
        public static boolean _3_active;

        public static double _4_claw;
        public static boolean _4_active;

        public static double _5_rotateBackBody;
        public static boolean _5_active;

        public static double _6_rotateBackClaw;
        public static boolean _6_active;

        public static double _7_backClaw;
        public static boolean _7_active;

        public static double _8_linkageRight;
        public static boolean _8_active;

        public static double _9_linkageLeft;
        public static boolean _9_active;
    }

    @Config
    public static class PT_Motors
    {
        public static boolean activeTelemetry;

        public static int _0_leftFront;
        public static boolean _0_active;

        public static int _1_leftRear;
        public static boolean _1_active;

        public static int _2_rightFront;
        public static boolean _2_active;

        public static int _3_rightRear;
        public static boolean _3_active;

        public static int _4_verticalLeft;
        public static boolean _4_active;

        public static int _5_verticalRight;
        public static boolean _5_active;
    }

    private static class ServoPos
    {
        public Servo servo;
        public double position;
        public boolean active;
    }

    private static class MotorsPos
    {
        public DcMotor motor;
        public int position;
        public boolean active;
    }

    private final ServoPos[] servos = new ServoPos[10];
    private final MotorsPos[] motors = new MotorsPos[6];
    private final Telemetry debug = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

    @Override
    public void runOpMode() throws InterruptedException
    {
        onInit();
        waitForStart();
        while(opModeIsActive())
        {
            onUpdate();
        }
    }

    private void onInit()
    {
        initArrays();
        initHardware();
        updateMotorStates();
        updateServoStates();
    }

    private void initArrays()
    {
        for (int i = 0; i < servos.length; i++) {
            servos[i] = new ServoPos();
        }

        for (int i = 0; i < motors.length; i++) {
            motors[i] = new MotorsPos();
        }
    }

    private void onUpdate()
    {
        updateServoStates();
        updateMotorStates();
        updatePositions();
        onDebug();
    }

    private void onDebug()
    {
        // -- Servo Debug -- //
        if (PT_Servos.activeTelemetry)
        {
            for (ServoPos servo : servos)
            {
                debug.addData(servo.servo.getDeviceName(), servo.servo.getPosition());
            }
        }

        // -- Motor Debug -- //
        if (PT_Servos.activeTelemetry)
        {
            for (MotorsPos motor : motors)
            {
                debug.addData(motor.motor.getDeviceName(), motor.motor.getCurrentPosition());
            }
        }

        debug.update();
    }

    private void updateServoStates()
    {
        servos[0].position = PT_Servos._0_rotateAxis;
        servos[1].position = PT_Servos._1_rotateBody;
        servos[2].position = PT_Servos._2_rotateHead;
        servos[3].position = PT_Servos._3_rotateClaw;
        servos[4].position = PT_Servos._4_claw;
        servos[5].position = PT_Servos._5_rotateBackBody;
        servos[6].position = PT_Servos._6_rotateBackClaw;
        servos[7].position = PT_Servos._7_backClaw;
        servos[8].position = PT_Servos._8_linkageRight;
        servos[9].position = PT_Servos._9_linkageLeft;

        servos[0].active = PT_Servos._0_active;
        servos[1].active = PT_Servos._1_active;
        servos[2].active = PT_Servos._2_active;
        servos[3].active = PT_Servos._3_active;
        servos[4].active = PT_Servos._4_active;
        servos[5].active = PT_Servos._5_active;
        servos[6].active = PT_Servos._6_active;
        servos[7].active = PT_Servos._7_active;
        servos[8].active = PT_Servos._8_active;
        servos[9].active = PT_Servos._9_active;

        servos[0].servo = Servos.rotateAxis;
        servos[1].servo = Servos.rotateBody;
        servos[2].servo = Servos.rotateHead;
        servos[3].servo = Servos.rotateClaw;
        servos[4].servo = Servos.claw;
        servos[5].servo = Servos.rotateBackBody;
        servos[6].servo = Servos.rotateBackClaw;
        servos[7].servo = Servos.backClaw;
        servos[8].servo = Servos.linkageRight;
        servos[9].servo = Servos.linkageLeft;
    }

    private void updateMotorStates()
    {
        motors[0].position = PT_Motors._0_leftFront;
        motors[1].position = PT_Motors._1_leftRear;
        motors[2].position = PT_Motors._2_rightFront;
        motors[3].position = PT_Motors._3_rightRear;
        motors[4].position = PT_Motors._4_verticalLeft;
        motors[5].position = PT_Motors._5_verticalRight;

        motors[0].active = PT_Motors._0_active;
        motors[1].active = PT_Motors._1_active;
        motors[2].active = PT_Motors._2_active;
        motors[3].active = PT_Motors._3_active;
        motors[4].active = PT_Motors._4_active;
        motors[5].active = PT_Motors._5_active;

        motors[0].motor = Motors.leftFront;
        motors[1].motor = Motors.leftRear;
        motors[2].motor = Motors.rightFront;
        motors[3].motor = Motors.rightRear;
        motors[4].motor = Motors.verticalLeft;
        motors[5].motor = Motors.verticalRight;
    }

    private void initHardware()
    {
        Servos.init(hardwareMap);
        Motors.init(hardwareMap);
    }

    private void updatePositions()
    {
        // -- Servos -- //
        for (int i = 0; i < 10; i++)
        {
            if(servos[i].active) servos[i].servo.setPosition(servos[i].position);
            else servos[i].servo.getController().pwmDisable();
        }

        // -- Motors -- //
        for (int i = 0; i < 6; i++)
        {
            if(motors[i].active) Func.SetMotorPosition(motors[i].motor, motors[i].position);
            else motors[i].motor.setPower(0);
        }
    }
}