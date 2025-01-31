package subsystems.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Motors
{
    public static DcMotor leftFront;
    public static DcMotor leftRear;
    public static DcMotor rightFront;
    public static DcMotor rightRear;

    public static DcMotor verticalLeft;
    public static DcMotor verticalRight;

    public static DcMotor[] allMotors = new DcMotor[6];

    public static void init(HardwareMap hardwareMap) {
        try
        {
            getMotors(hardwareMap);
            setZeroPowerBehaviour();
            setDirection();
            setAllMotors();
        }
        catch (Exception ignored) {}
    }

    private static void getMotors(HardwareMap hardwareMap)
    {
        leftFront = hardwareMap.tryGet(DcMotor.class, "leftFront");
        leftRear = hardwareMap.tryGet(DcMotor.class, "leftRear");
        rightFront = hardwareMap.tryGet(DcMotor.class, "rightFront");
        rightRear = hardwareMap.tryGet(DcMotor.class, "rightRear");

        verticalLeft= hardwareMap.tryGet(DcMotor.class, "verticalLeft");
        verticalRight = hardwareMap.tryGet(DcMotor.class, "verticalRight");
    }

    private static void setZeroPowerBehaviour()
    {
        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    private static void setDirection()
    {
        verticalLeft.setDirection(DcMotorSimple.Direction.REVERSE);
    }


    private static void setAllMotors() {
        DcMotor[] motors = {leftFront, rightFront, leftRear, rightRear, verticalRight, verticalLeft};

        for (int i = 0; i < motors.length; i++) {
            if (motors[i] != null) {
                allMotors[i] = motors[i];
            } else {
                allMotors[i] = null;
            }
        }
    }

}
