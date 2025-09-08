package org.firstinspires.ftc.teamcode.helper;

import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;

import dev.nextftc.ftc.ActiveOpMode;

public class Debug {
    public static final Debug INSTANCE = new Debug();
    public Debug() {}

    private final TelemetryManager telemetry = PanelsTelemetry.INSTANCE.getTelemetry();

    public void addData(String key, Object value) {
        telemetry.addData(key,value);
    }

    public void update() {
        telemetry.update(ActiveOpMode.telemetry());
    }
}
