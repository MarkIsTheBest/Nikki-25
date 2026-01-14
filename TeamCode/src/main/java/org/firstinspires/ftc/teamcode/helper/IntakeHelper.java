package org.firstinspires.ftc.teamcode.helper;

import static org.firstinspires.ftc.teamcode.constants.Distances.Intake.BALL_DETECT_DISTANCE;
import static org.firstinspires.ftc.teamcode.constants.Positions.Servo.*;

import com.pedropathing.util.Timer;
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

    public static IntakeHelper INSTANCE;

    private double rightDistance;
    private double distance;
    private boolean jammed = false;
    private boolean spinIntake = false;

    // Optimization: Cache last set power to prevent duplicate hardware writes
    private double lastIntakePower = 0.0;

    private Launchers launcherHelper;

    public IntakeHelper() {
        launcherHelper = Launchers.INSTANCE;
        initPositions();
    }

    private final Timer intakeTimer = new Timer();
    private final Timer jamTimer = new Timer();

    // Optimization: Rate limit I2C distance reads
    private final Timer sensorTimer = new Timer();
    private static final double SENSOR_READ_DELAY_MS = 50; // Read at 20Hz

    private IntakeStep currentStep = IntakeStep.PREPARE_DOORS;
    private Launcher activeLauncher = null;
    private boolean hasBall = false;

    double intakeTime;

    private void changeStep(IntakeStep newStep) {
        currentStep = newStep;
        intakeTimer.resetTimer();
    }

    private void initPositions() {
        Servos.setPosition(Servos.Holder1(), H_PREPARE);
        Servos.setPosition(Servos.Holder2(), H_PREPARE);
        Servos.setPosition(Servos.Holder3(), H_PREPARE);
        Servos.setPosition(Servos.Door1(), L_D1_PREPARE);
        Servos.setPosition(Servos.Door2(), L_D2_PREPARE);
    }

    private Launcher getFirstEmptyLauncher() {
        boolean[] filled = launcherHelper.getFilledLaunchers();
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

    private void endIntake(Launcher launcher) {
        // This color sensor read is unavoidable, but happens rarely (once per ball)
        ArtifactColor color = ColorSensors.getColor(ColorSensors.AllColorSensors()[launcher.index]);

        if (color != ArtifactColor.EMPTY) {
            launcherHelper.setFilledLaunchers(launcher.index, true);
            launcherHelper.setLauncherColor(launcher.index, color);
            launcherHelper.HandleLEDS();
        }
        activeLauncher = null;
        hasBall = false;
        currentStep = IntakeStep.PREPARE_DOORS;
    }

    private void intake() {
        if (activeLauncher == null) return;

        Servo holder = (activeLauncher == Launcher.LEFT) ? Servos.Holder1() :
                (activeLauncher == Launcher.CENTER) ? Servos.Holder2() : Servos.Holder3();

        switch (currentStep) {
            case PREPARE_DOORS:
                applyDoorPositions(activeLauncher);
                Servos.setPosition(holder, H_PREPARE);
                if (hasBall) changeStep(IntakeStep.PARTIAL);
                break;

            case PARTIAL:
                if (intakeTime > Timers.Intake.PARTIAL_DELAY && !jammed) {
                    if (activeLauncher == Launcher.LEFT) Servos.setPosition(Servos.Door1(), L_D1_PARTIAL);
                    if (activeLauncher == Launcher.RIGHT) Servos.setPosition(Servos.Door2(), R_D2_PARTIAL);

                    // Servos.isBusy is now fast due to Bulk Reads
                    if (!Servos.isBusy(Servos.Door1()) && !Servos.isBusy(Servos.Door2())) {
                        Servos.setPosition(holder, H_CLOSE);
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
                Servos.setPosition(Servos.Door1(), L_D1_PREPARE);
                Servos.setPosition(Servos.Door2(), L_D2_PREPARE);
                break;
            case CENTER:
                Servos.setPosition(Servos.Door1(), C_D1_PREPARE);
                Servos.setPosition(Servos.Door2(), C_D2_PREPARE);
                break;
            case RIGHT:
                Servos.setPosition(Servos.Door1(), R_D1_PREPARE);
                Servos.setPosition(Servos.Door2(), R_D2_PREPARE);
                break;
        }
    }

    private void checkJammed() {
        if (jammed) {
            if (jamTimer.getElapsedTimeSeconds() > Timers.Intake.UNJAM_TIME) {
                jammed = false;
                launcherHelper.HandleLEDS();
                jamTimer.resetTimer();
            }
            LEDs.playRedFlashAnimation();
            return;
        }

        if (!Servos.isBusy(Servos.Door1()) && !Servos.isBusy(Servos.Door2())) {
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

    public void update() {
        checkJammed();
        selectLauncherIfNeeded();

        if (!jammed && getFirstEmptyLauncher() != null && !hasBall) {
            // Optimization: Only read I2C sensor every 50ms
            if (sensorTimer.getElapsedTime() > SENSOR_READ_DELAY_MS) {
                rightDistance = (DistanceSensors.Right().getDistance(DistanceUnit.INCH));
                distance = rightDistance;
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

    // Telemetry removed for speed, add back if debugging needed
}