package org.firstinspires.ftc.teamcode.opModes.helper;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.ArrayList;
import java.util.List;

public class AprilTagHelper {

    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;

    // Public values you can read ANYWHERE
    public int detectedId = -1;
    public double detectedDistance = -1;

    // List of allowed IDs
    private List<Integer> allowedIDs = new ArrayList<>();

    public void init(HardwareMap hardwareMap, String webcamName) {
        aprilTag = AprilTagProcessor.easyCreateWithDefaults();

        visionPortal = VisionPortal.easyCreateWithDefaults(
                hardwareMap.get(WebcamName.class, webcamName),
                aprilTag
        );
    }

    // Call this inside your OpMode loop
    public void update() {
        List<AprilTagDetection> detections = aprilTag.getDetections();

        detectedId = -1;
        detectedDistance = -1;

        if (detections == null || detections.isEmpty()) return;

        for (AprilTagDetection d : detections) {
            if (allowedIDs.isEmpty() || allowedIDs.contains(d.id)) {
                detectedId = d.id;
                detectedDistance = d.ftcPose.range;
                return;
            }
        }
    }

    public void close() {
        if (visionPortal != null) {
            visionPortal.close();
        }
    }

    public void allowedIDs(ArrayList<Integer> integers) {
        allowedIDs = integers;
    }
}
