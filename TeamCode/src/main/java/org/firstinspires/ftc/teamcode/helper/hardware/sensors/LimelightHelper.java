package org.firstinspires.ftc.teamcode.helper.hardware.sensors;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

import dev.nextftc.ftc.ActiveOpMode;

public class LimelightHelper {
    private Limelight3A limelight; public Limelight3A LimelightSensor() { return limelight; }

    private double tx = -999; public double Tx() { return tx; }
    private double ty = -999; public double Ty() { return ty; }
    private double ta = -999; public double Ta() { return ta; }
    private Pose3D botPose; public Pose3D BotPose() { return botPose; }
    private Pose3D botPoseMT2; public Pose3D BotPoseMT2() { return botPoseMT2; }

    public LimelightHelper() {
        init();
    }

    public void init() {
        getHardware(ActiveOpMode.hardwareMap());
        setPipeline(0);
    }

    private void getHardware(HardwareMap hardwareMap) {
        limelight = hardwareMap.tryGet(Limelight3A.class, "Limelight");
    }

    public void setPipeline(int pipeline) {
        limelight.pipelineSwitch(pipeline);
    }

    public void start() {
        limelight.start();
    }

    public void update() {
        LLResult result = limelight.getLatestResult();
        if (result != null) {
            if (result.isValid()) {
                botPose = result.getBotpose();
                botPoseMT2 = result.getBotpose_MT2();
                tx = result.getTx();
                ty = result.getTy();
                ta = result.getTa();
            }
        }
    }
}