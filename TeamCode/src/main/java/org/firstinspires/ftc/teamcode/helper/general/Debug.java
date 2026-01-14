package org.firstinspires.ftc.teamcode.helper.general;

import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import dev.nextftc.ftc.ActiveOpMode;

public class Debug {

    private final Telemetry telemetry;

    public Debug(Telemetry telemetry) {
        this.telemetry = telemetry;
    }

    private final TelemetryManager telemetryDash = PanelsTelemetry.INSTANCE.getTelemetry();

    public void addData(String key, Object value) {
        telemetryDash.addData(key,value);
    }

    public void addLine(String line) {telemetryDash.addLine(line);}

    public void addBreak() {telemetryDash.addLine("");}

    public void update() {
        telemetryDash.update(telemetry);
    }
}
