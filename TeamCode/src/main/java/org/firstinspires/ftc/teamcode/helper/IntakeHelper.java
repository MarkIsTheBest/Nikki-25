package org.firstinspires.ftc.teamcode.helper;

import static org.firstinspires.ftc.teamcode.constants.Distances.Intake.BALL_DETECT_DISTANCE;
import static org.firstinspires.ftc.teamcode.constants.Positions.Servo.*;

import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.constants.Timers;
import org.firstinspires.ftc.teamcode.constants.enums.ArtifactColor;
import org.firstinspires.ftc.teamcode.constants.enums.IntakeStep;
import org.firstinspires.ftc.teamcode.constants.enums.Launcher;
import org.firstinspires.ftc.teamcode.helper.general.Debug;
import org.firstinspires.ftc.teamcode.helper.hardware.Motors;
import org.firstinspires.ftc.teamcode.helper.hardware.Servos;
import org.firstinspires.ftc.teamcode.helper.hardware.sensors.ColorSensors;
import org.firstinspires.ftc.teamcode.helper.hardware.sensors.DistanceSensors;
import org.firstinspires.ftc.teamcode.helper.hardware.sensors.LEDs;

public class IntakeHelper {

    private final Servos servos;
    private final Debug debug;
    private final Launchers launchers;

    public IntakeHelper(Debug debug, Launchers launchers, Servos Servos) {
        this.launchers = launchers;
        this.debug = debug;
        this.servos = Servos;
        initPositions();
    }

    private double rightDistance;
    private double leftDistance;
    private double distance;
    private boolean jammed = false;
    private boolean spinIntake = false;

    // Optimization: Cache last set power to prevent duplicate hardware writes
    private double lastIntakePower = 0.0;

    private final Timer intakeTimer = new Timer();
    private final Timer jamTimer = new Timer();

    // Optimization: Rate limit I2C distance read
    private final Timer sensorTimer = new Timer();
    private static final double SENSOR_READ_DELAY_MS = 25; // Read at 20Hz

    private IntakeStep currentStep = IntakeStep.PREPARE_DOORS;
    private Launcher activeLauncher = null;
    private boolean hasBall = false;

    double intakeTime;

    private void changeStep(IntakeStep newStep) {
        currentStep = newStep;
        intakeTimer.resetTimer();
    }

    private void initPositions() {
        checkColors();
        servos.setPosition(servos.Holder1(), H_PREPARE);
        servos.setPosition(servos.Holder2(), H_PREPARE);
        servos.setPosition(servos.Holder3(), H_PREPARE);

        if(launchers.getFilledLaunchers()[0]) {
            servos.setPosition(servos.Holder1(), H_CLOSE);
        }
        if(launchers.getFilledLaunchers()[1]) {
            servos.setPosition(servos.Holder2(), H_CLOSE);
        }
        if(launchers.getFilledLaunchers()[2]) {
            servos.setPosition(servos.Holder3(), H_CLOSE);
        }

        if(getFirstEmptyLauncher() == Launcher.LEFT) {
            servos.setPosition(servos.Door1(), L_D1_PREPARE);
            servos.setPosition(servos.Door2(), L_D2_PREPARE);
        }

        if(getFirstEmptyLauncher() == Launcher.CENTER) {
            servos.setPosition(servos.Door1(), C_D1_PREPARE);
            servos.setPosition(servos.Door2(), C_D2_PREPARE);
        }

        if(getFirstEmptyLauncher() == Launcher.RIGHT) {
            servos.setPosition(servos.Door1(), R_D1_PREPARE);
            servos.setPosition(servos.Door2(), R_D2_PREPARE);
        }
    }

    private Launcher getFirstEmptyLauncher() {
        boolean[] filled = launchers.getFilledLaunchers();
        if (!filled[0]) return Launcher.LEFT;
        if (!filled[1]) return Launcher.CENTER;
        if (!filled[2]) return Launcher.RIGHT;
        return null;
    }

    private void selectLauncherIfNeeded() {
        if (activeLauncher != null) return;
        Launcher next = getFirstEmptyLauncher();
        if (next == null) return;

        activeLauncher = next;
        currentStep = IntakeStep.PREPARE_DOORS;
        intakeTimer.resetTimer();
    }

    private void checkColors() {
        for (int i = 0; i < 3; i++)
        {
            ArtifactColor color = ColorSensors.getColor(ColorSensors.AllColorSensors()[i]);

            if (color != ArtifactColor.EMPTY) {
                launchers.setFilledLaunchers(i, true);
                launchers.setLauncherColor(i, color);
                launchers.HandleLEDS();
            }
        }
    }

    private void endIntake(Launcher launcher) {
        // This color sensor read is unavoidable, but happens rarely (once per ball)
        ArtifactColor color = ColorSensors.getColor(ColorSensors.AllColorSensors()[launcher.index]);

        if (color != ArtifactColor.EMPTY) {
            launchers.setFilledLaunchers(launcher.index, true);
            launchers.setLauncherColor(launcher.index, color);
            launchers.HandleLEDS();
        }
        activeLauncher = null;
        hasBall = false;
        currentStep = IntakeStep.PREPARE_DOORS;
    }

    private void intake() {
        if (activeLauncher == null) return;

        Servo holder = (activeLauncher == Launcher.LEFT) ? servos.Holder1() :
                (activeLauncher == Launcher.CENTER) ? servos.Holder2() : servos.Holder3();

        switch (currentStep) {
            case PREPARE_DOORS:
                applyDoorPositions(activeLauncher);
                servos.setPosition(holder, H_PREPARE);
                if (hasBall) changeStep(IntakeStep.PARTIAL);
                break;

            case PARTIAL:
                if (intakeTime > Timers.Intake.PARTIAL_DELAY && !jammed) {
                    if (activeLauncher == Launcher.LEFT) servos.setPosition(servos.Door1(), L_D1_PARTIAL);
                    if (activeLauncher == Launcher.RIGHT) servos.setPosition(servos.Door2(), R_D2_PARTIAL);
                    if (activeLauncher == Launcher.CENTER) servos.setPosition(servos.Door2(), 0.4);

                    // servos.isBusy is now fast due to Bulk Reads
                    if (!servos.isBusy(servos.Door1()) && !servos.isBusy(servos.Door2())) {
                        servos.setPosition(holder, H_CLOSE);
                        changeStep(IntakeStep.CLOSE);
                    }
                }
                break;

            case CLOSE:
                if (intakeTime > Timers.Intake.PARTIAL_DELAY && !jammed) {
                    endIntake(activeLauncher);
                }
                break;
        }
    }

    private void applyDoorPositions(Launcher launcher) {
        switch (launcher) {
            case LEFT:
                servos.setPosition(servos.Door1(), L_D1_PREPARE);
                servos.setPosition(servos.Door2(), L_D2_PREPARE);
                break;
            case CENTER:
                servos.setPosition(servos.Door1(), C_D1_PREPARE);
                servos.setPosition(servos.Door2(), C_D2_PREPARE);
                break;
            case RIGHT:
                servos.setPosition(servos.Door1(), R_D1_PREPARE);
                servos.setPosition(servos.Door2(), R_D2_PREPARE);
                break;
        }
    }

    private void checkJammed() {
        if (jammed) {
            LEDs.playRedFlashAnimation();
            if (jamTimer.getElapsedTimeSeconds() > Timers.Intake.UNJAM_TIME) {
                jammed = false;
                launchers.HandleLEDS();
                jamTimer.resetTimer();
            }
            return;
        }

        if (!servos.isBusy(servos.Door1()) && !servos.isBusy(servos.Door2())) {
            jamTimer.resetTimer();
        } else if (jamTimer.getElapsedTimeSeconds() > Timers.Intake.JAMMED_TIME) {
            jammed = true;
            hasBall = false;
            currentStep = IntakeStep.PREPARE_DOORS;
            jamTimer.resetTimer();
        }
    }

    public void HandleIntakeSpin() {
        double targetPower = 0;
        if(spinIntake && !jammed) targetPower = 1;
        else if (jammed) targetPower = -1;

        // Optimization: Only write to hardware if power changed
        if (Math.abs(targetPower - lastIntakePower) > 0.01) {
            Motors.Intake().setPower(targetPower);
            lastIntakePower = targetPower;
        }
    }

    private double computeMinDistance(double left, double right) {
        double avg = (left + right) / 2.0;
        if (avg <= BALL_DETECT_DISTANCE * 2) return Math.min(left, right);
        return 999;
    }

    public void update() {
        checkJammed();
        selectLauncherIfNeeded();

        if (!jammed && getFirstEmptyLauncher() != null && !hasBall) {
            // Optimization: Only read I2C sensor every 50ms
            if (sensorTimer.getElapsedTime() > SENSOR_READ_DELAY_MS) {
                rightDistance = (DistanceSensors.Right().getDistance(DistanceUnit.INCH));
                leftDistance = (DistanceSensors.Left().getDistance(DistanceUnit.INCH));
                distance = computeMinDistance(leftDistance, rightDistance);
                sensorTimer.resetTimer();

                if (distance < BALL_DETECT_DISTANCE) {
                    hasBall = true;
                    intakeTimer.resetTimer();
                }
            }
        }

        intakeTime = intakeTimer.getElapsedTimeSeconds();
        intake();
    }

    public void spinIntake(boolean value) {
        spinIntake = value;
    }

    public void showTelemetry() {
        debug.addData("Distance Detected", distance);
    }
}