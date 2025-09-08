package org.firstinspires.ftc.teamcode.helper.hardware.sensors;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.teamcode.helper.Debug;

import dev.nextftc.ftc.ActiveOpMode;

public class Limelight {
    public static final Limelight INSTANCE = new Limelight();

    public Limelight() {
        init();
    }

    private Limelight3A limelight;

    public Limelight3A LimelightSensor() {
        return limelight;
    }

    private double tx = -999; public double Tx() { return tx; }
    private double ty = -999; public double Ty() { return ty; }
    private double ta = -999; public double Ta() { return ta; }
    private Pose3D botPose; public Pose3D BotPose() { return botPose; }

    private void init() {
        try {
            getHardware(ActiveOpMode.hardwareMap());
        } catch (Exception ex) {
            Debug.INSTANCE.addData("ERROR", ex.getMessage());
            Debug.INSTANCE.update();
        }
    }

    private void getHardware(HardwareMap hardwareMap) {
        limelight = hardwareMap.tryGet(Limelight3A.class, "limelight");
    }

    public void setLimelightPipeline(int pipeline) {
        limelight.pipelineSwitch(pipeline);
    }

    public void startLimelight() {
        limelight.start();
    }

    public void updateLimelight() {
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