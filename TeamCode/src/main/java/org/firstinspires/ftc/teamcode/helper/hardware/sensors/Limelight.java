package org.firstinspires.ftc.teamcode.helper.hardware.sensors;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.teamcode.helper.Debug;

import dev.nextftc.ftc.ActiveOpMode;

public class Limelight {
    private static Limelight3A limelight;

    public static Limelight3A LimelightSensor() {
        return limelight;
    }

    private static double tx = -999; public static double Tx() { return tx; }
    private static double ty = -999; public static double Ty() { return ty; }
    private static double ta = -999; public static double Ta() { return ta; }
    private static Pose3D botPose; public static Pose3D BotPose() { return botPose; }

    public static void init() {
        try {
            getHardware(ActiveOpMode.hardwareMap());
            setPipeline(0);
        } catch (Exception ex) {
            Debug.INSTANCE.addData("ERROR", ex.getMessage());
            Debug.INSTANCE.update();
        }
    }

    private static void getHardware(HardwareMap hardwareMap) {
        limelight = hardwareMap.tryGet(Limelight3A.class, "limelight");
    }

    public static void setPipeline(int pipeline) {
        limelight.pipelineSwitch(pipeline);
    }

    public static void start() {
        limelight.start();
    }

    public static void update() {
        LLResult result = limelight.getLatestResult();
        if (result != null) {
            if (result.isValid()) {
                botPose = result.getBotpose();
                tx = result.getTx();
                ty = result.getTy();
                ta = result.getTa();
            }
        }
    }
}