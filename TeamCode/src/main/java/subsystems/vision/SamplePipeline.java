package subsystems.vision;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;
import org.openftc.easyopencv.OpenCvWebcam;

import java.util.ArrayList;
import java.util.List;

public class SamplePipeline extends OpenCvPipeline {
    public Scalar upperBlue;
    public Scalar lowerBlue;

    public Scalar upperRed;
    public Scalar lowerRed;

    public Scalar upperYellow;
    public Scalar lowerYellow;
    public double redAngle;
    public double blueAngle;
    public double yellowAngle;
    boolean viewportPaused;
    OpenCvWebcam webcam;
    private double angle;

    public SamplePipeline(OpenCvWebcam camera) {
        webcam = camera;

        // Set default HSV ranges for blue, red, and yellow colors
        lowerBlue = new Scalar(100, 150, 50);
        upperBlue = new Scalar(140, 255, 255);

        lowerRed = new Scalar(0, 120, 70);
        upperRed = new Scalar(10, 255, 255);

        lowerYellow = new Scalar(20, 100, 100);
        upperYellow = new Scalar(30, 255, 255);
    }

    @Override
    public Mat processFrame(Mat input) {
        Mat hsvMat = new Mat();
        Imgproc.cvtColor(input, hsvMat, Imgproc.COLOR_RGB2HSV);

        Mat binaryMask = Mat.zeros(input.size(), input.type());

        // Process each color independently
        processColor(hsvMat, binaryMask, lowerBlue, upperBlue, new Scalar(0, 0, 255), "Blue");
        processColor(hsvMat, binaryMask, lowerRed, upperRed, new Scalar(255, 0, 0), "Red");
        processColor(hsvMat, binaryMask, lowerYellow, upperYellow, new Scalar(240, 236, 7), "Yellow");

        hsvMat.release();

        // Return the binary mask showing detected samples for each color
        return binaryMask;
    }

    private void processColor(Mat hsvMat, Mat binaryMask, Scalar lower, Scalar upper, Scalar color, String colorName) {
        Mat mask = new Mat();
        Core.inRange(hsvMat, lower, upper, mask);

        // Perform morphological operations to clean up the mask
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        Imgproc.erode(mask, mask, kernel);
        Imgproc.dilate(mask, mask, kernel);

        // Find contours in the mask
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        RotatedRect largestRotatedRect = null;
        double maxArea = 0;

        // Identify the largest contour that exceeds a threshold area
        for (MatOfPoint contour : contours) {
            double area = Imgproc.contourArea(contour);
            if (area > 500 && area > maxArea) {
                RotatedRect rotatedRect = Imgproc.minAreaRect(new MatOfPoint2f(contour.toArray()));
                maxArea = area;
                largestRotatedRect = rotatedRect;
            }
        }

        if (largestRotatedRect != null) {
            Point[] boxPoints = new Point[4];
            largestRotatedRect.points(boxPoints);
            List<MatOfPoint> largestContour = new ArrayList<>();
            MatOfPoint rectContour = new MatOfPoint(boxPoints);
            largestContour.add(rectContour);

            // Fill the rectangle on the binary mask
            Imgproc.fillPoly(binaryMask, largestContour, color);

            // Calculate the angle of the rectangle
            angle = largestRotatedRect.angle;
            if (largestRotatedRect.size.width < largestRotatedRect.size.height) {
                angle += 90;
            }
            if (angle < 0) {
                angle += 360;
            } else if (angle >= 360) {
                angle -= 360;
            }

            // Get the center of the screen
            double screenWidth = binaryMask.cols();
            double screenHeight = binaryMask.rows();
            Point screenCenter = new Point(screenWidth / 2, screenHeight / 2);

            // Calculate the distance from the rectangle's center to the screen center
            Point rectCenter = largestRotatedRect.center;
            double distanceToCenter = Math.sqrt(
                    Math.pow(rectCenter.x - screenCenter.x, 2) +
                            Math.pow(rectCenter.y - screenCenter.y, 2)
            );

            // Store the angle and distance for the specific color
            if (color.equals(new Scalar(0, 0, 255))) {
                blueAngle = angle;
            } else if (color.equals(new Scalar(255, 0, 0))) {
                redAngle = angle;
            } else if (color.equals(new Scalar(240, 236, 7))) {
                yellowAngle = angle;
            }

            // Display the information on the binary mask
            Imgproc.putText(binaryMask, colorName + String.format(" Angle: %.2f", angle),
                    new Point(rectCenter.x, rectCenter.y),
                    Imgproc.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(255, 255, 255), 2);
            Imgproc.putText(binaryMask, String.format("Dist: %.2f", distanceToCenter),
                    new Point(rectCenter.x, rectCenter.y + 20),
                    Imgproc.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(255, 255, 255), 2);
        }

        // Release resources
        mask.release();
    }

    @Override
    public void onViewportTapped() {
        viewportPaused = !viewportPaused;

        if (viewportPaused) {
            webcam.pauseViewport();
        } else {
            webcam.resumeViewport();
        }
    }
}
