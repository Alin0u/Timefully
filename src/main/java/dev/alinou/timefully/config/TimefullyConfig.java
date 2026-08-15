package dev.alinou.timefully.config;

import dev.alinou.timefully.Timefully;
import dev.alinou.timefully.hud.PanelShape;
import net.fabricmc.loader.api.FabricLoader;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Persisted widget settings: which components are shown, and where the
 * widget sits. Position is stored as a fraction of the screen so the
 * widget keeps its place when the window is resized.
 */
public final class TimefullyConfig {

    private static final String FILE_NAME = "timefully.properties";

    private static boolean showRealTime = true;
    private static boolean showGameTime = true;
    private static boolean showPhaseIcon = true;
    private static boolean showPhaseLabel = false;
    private static boolean showWeather = true;
    private static boolean fancyMode = true;
    private static PanelShape panelShape = PanelShape.ROUNDED;

    private static float anchorX = 0.0f;
    private static float anchorY = 0.0f;

    private static Path configFile;

    private TimefullyConfig() {
    }

    public static void init() {
        configFile = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
        load();
    }

    public static boolean showRealTime() {
        return showRealTime;
    }

    public static boolean showGameTime() {
        return showGameTime;
    }

    public static boolean showPhaseIcon() {
        return showPhaseIcon;
    }

    public static boolean showPhaseLabel() {
        return showPhaseLabel;
    }

    public static boolean showWeather() {
        return showWeather;
    }

    public static boolean fancyMode() {
        return fancyMode;
    }

    public static PanelShape panelShape() {
        return panelShape;
    }

    public static float anchorX() {
        return anchorX;
    }

    public static float anchorY() {
        return anchorY;
    }

    public static void setShowRealTime(boolean value) {
        showRealTime = value;
    }

    public static void setShowGameTime(boolean value) {
        showGameTime = value;
    }

    public static void setShowPhaseIcon(boolean value) {
        showPhaseIcon = value;
    }

    public static void setShowPhaseLabel(boolean value) {
        showPhaseLabel = value;
    }

    public static void setShowWeather(boolean value) {
        showWeather = value;
    }

    public static void setFancyMode(boolean value) {
        fancyMode = value;
    }

    public static void setPanelShape(PanelShape value) {
        panelShape = value;
    }

    /** Stores the widget anchor, clamped to the visible screen area. */
    public static void setAnchor(float x, float y) {
        anchorX = Math.clamp(x, 0.0f, 1.0f);
        anchorY = Math.clamp(y, 0.0f, 1.0f);
    }

    private static void load() {
        if (configFile == null || !configFile.toFile().exists()) {
            return;
        }
        Properties props = new Properties();
        try (FileReader reader = new FileReader(configFile.toFile())) {
            props.load(reader);
            showRealTime = readBool(props, "showRealTime", showRealTime);
            showGameTime = readBool(props, "showGameTime", showGameTime);
            showPhaseIcon = readBool(props, "showPhaseIcon", showPhaseIcon);
            showPhaseLabel = readBool(props, "showPhaseLabel", showPhaseLabel);
            showWeather = readBool(props, "showWeather", showWeather);
            fancyMode = readBool(props, "fancyMode", fancyMode);
            panelShape = readShape(props, "panelShape", panelShape);
            setAnchor(readFloat(props, "anchorX", anchorX), readFloat(props, "anchorY", anchorY));
        } catch (IOException e) {
            Timefully.LOGGER.warn("Could not read {}: {}", FILE_NAME, e.getMessage());
        }
    }

    public static void save() {
        if (configFile == null) {
            return;
        }
        Properties props = new Properties();
        props.setProperty("showRealTime", Boolean.toString(showRealTime));
        props.setProperty("showGameTime", Boolean.toString(showGameTime));
        props.setProperty("showPhaseIcon", Boolean.toString(showPhaseIcon));
        props.setProperty("showPhaseLabel", Boolean.toString(showPhaseLabel));
        props.setProperty("showWeather", Boolean.toString(showWeather));
        props.setProperty("fancyMode", Boolean.toString(fancyMode));
        props.setProperty("panelShape", panelShape.name());
        props.setProperty("anchorX", Float.toString(anchorX));
        props.setProperty("anchorY", Float.toString(anchorY));
        try (FileWriter writer = new FileWriter(configFile.toFile())) {
            props.store(writer, "Timefully settings");
        } catch (IOException e) {
            Timefully.LOGGER.warn("Could not write {}: {}", FILE_NAME, e.getMessage());
        }
    }

    private static boolean readBool(Properties props, String key, boolean fallback) {
        String value = props.getProperty(key);
        return value == null ? fallback : Boolean.parseBoolean(value);
    }

    private static PanelShape readShape(Properties props, String key, PanelShape fallback) {
        String value = props.getProperty(key);
        if (value == null) {
            return fallback;
        }
        try {
            return PanelShape.valueOf(value);
        } catch (IllegalArgumentException e) {
            return fallback;
        }
    }

    private static float readFloat(Properties props, String key, float fallback) {
        String value = props.getProperty(key);
        if (value == null) {
            return fallback;
        }
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
