package dev.alinou.timefully.config;

import dev.alinou.timefully.Timefully;
import dev.alinou.timefully.hud.BackgroundMode;
import dev.alinou.timefully.hud.ElementId;
import dev.alinou.timefully.hud.FontStyle;
import dev.alinou.timefully.hud.LayoutMode;
import dev.alinou.timefully.time.DayPhase;
import net.fabricmc.loader.api.FabricLoader;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;

/**
 * Persisted widget settings: which components are shown, where they sit,
 * and how they are styled. Position is stored as a fraction of the screen
 * so the widget keeps its place when the window is resized.
 */
public final class TimefullyConfig {

    private static final String FILE_NAME = "timefully.properties";

    private static boolean showRealTime = true;
    private static boolean showGameTime = true;
    private static boolean showWeather = true;
    private static FontStyle fontStyle = FontStyle.DEFAULT;
    private static LayoutMode layoutMode = LayoutMode.GROUPED;

    private static BackgroundMode backgroundMode = BackgroundMode.STANDARD;
    private static int singleBackgroundColor = 0x5A7A86;
    private static final Map<DayPhase, Integer> phaseBackgroundColors = new EnumMap<>(DayPhase.class);

    /** 0-255. */
    private static int backgroundAlpha = 220;
    private static int textAlpha = 255;
    private static int weatherIconAlpha = 255;
    private static int textColor = 0xFFFFFF;

    /** Used in GROUPED layout mode. */
    private static float anchorX = 0.0f;
    private static float anchorY = 0.0f;

    /** Used in SEPARATED layout mode, one position per element. */
    private static final Map<ElementId, float[]> elementAnchors = new EnumMap<>(ElementId.class);

    private static Path configFile;

    private TimefullyConfig() {
    }

    static {
        phaseBackgroundColors.put(DayPhase.DAWN, 0x7A5A62);
        phaseBackgroundColors.put(DayPhase.DAY, 0x5A7A86);
        phaseBackgroundColors.put(DayPhase.DUSK, 0x6A4A5E);
        phaseBackgroundColors.put(DayPhase.NIGHT, 0x262C46);

        elementAnchors.put(ElementId.REAL_TIME, new float[] {0.0f, 0.0f});
        elementAnchors.put(ElementId.GAME_TIME, new float[] {0.0f, 0.12f});
        elementAnchors.put(ElementId.WEATHER, new float[] {0.0f, 0.24f});
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

    public static boolean showWeather() {
        return showWeather;
    }

    public static FontStyle fontStyle() {
        return fontStyle;
    }

    public static LayoutMode layoutMode() {
        return layoutMode;
    }

    public static BackgroundMode backgroundMode() {
        return backgroundMode;
    }

    public static int singleBackgroundColor() {
        return singleBackgroundColor;
    }

    public static int phaseBackgroundColor(DayPhase phase) {
        return phaseBackgroundColors.getOrDefault(phase, singleBackgroundColor);
    }

    public static int backgroundAlpha() {
        return backgroundAlpha;
    }

    public static int textAlpha() {
        return textAlpha;
    }

    public static int weatherIconAlpha() {
        return weatherIconAlpha;
    }

    public static int textColor() {
        return textColor;
    }

    public static float anchorX() {
        return anchorX;
    }

    public static float anchorY() {
        return anchorY;
    }

    public static float elementAnchorX(ElementId element) {
        return elementAnchors.get(element)[0];
    }

    public static float elementAnchorY(ElementId element) {
        return elementAnchors.get(element)[1];
    }

    public static void setShowRealTime(boolean value) {
        showRealTime = value;
    }

    public static void setShowGameTime(boolean value) {
        showGameTime = value;
    }

    public static void setShowWeather(boolean value) {
        showWeather = value;
    }

    public static void setFontStyle(FontStyle value) {
        fontStyle = value;
    }

    public static void setLayoutMode(LayoutMode value) {
        layoutMode = value;
    }

    public static void setBackgroundMode(BackgroundMode value) {
        backgroundMode = value;
    }

    public static void setSingleBackgroundColor(int rgb) {
        singleBackgroundColor = rgb & 0xFFFFFF;
    }

    public static void setPhaseBackgroundColor(DayPhase phase, int rgb) {
        phaseBackgroundColors.put(phase, rgb & 0xFFFFFF);
    }

    public static void setBackgroundAlpha(int alpha) {
        backgroundAlpha = clampByte(alpha);
    }

    public static void setTextAlpha(int alpha) {
        textAlpha = clampByte(alpha);
    }

    public static void setWeatherIconAlpha(int alpha) {
        weatherIconAlpha = clampByte(alpha);
    }

    public static void setTextColor(int rgb) {
        textColor = rgb & 0xFFFFFF;
    }

    /** Stores the widget anchor for GROUPED mode, clamped to the visible screen area. */
    public static void setAnchor(float x, float y) {
        anchorX = Math.clamp(x, 0.0f, 1.0f);
        anchorY = Math.clamp(y, 0.0f, 1.0f);
    }

    /** Stores one element's anchor for SEPARATED mode, clamped to the visible screen area. */
    public static void setElementAnchor(ElementId element, float x, float y) {
        elementAnchors.put(element, new float[] {Math.clamp(x, 0.0f, 1.0f), Math.clamp(y, 0.0f, 1.0f)});
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
            showWeather = readBool(props, "showWeather", showWeather);
            fontStyle = readEnum(props, "fontStyle", FontStyle.class, fontStyle);
            layoutMode = readEnum(props, "layoutMode", LayoutMode.class, layoutMode);
            backgroundMode = readEnum(props, "backgroundMode", BackgroundMode.class, backgroundMode);
            singleBackgroundColor = readInt(props, "singleBackgroundColor", singleBackgroundColor);
            for (DayPhase phase : DayPhase.values()) {
                int fallback = phaseBackgroundColors.get(phase);
                phaseBackgroundColors.put(phase, readInt(props, "backgroundColor." + phase.name(), fallback));
            }
            backgroundAlpha = clampByte(readInt(props, "backgroundAlpha", backgroundAlpha));
            textAlpha = clampByte(readInt(props, "textAlpha", textAlpha));
            weatherIconAlpha = clampByte(readInt(props, "weatherIconAlpha", weatherIconAlpha));
            textColor = readInt(props, "textColor", textColor);
            setAnchor(readFloat(props, "anchorX", anchorX), readFloat(props, "anchorY", anchorY));
            for (ElementId element : ElementId.values()) {
                float[] fallback = elementAnchors.get(element);
                float x = readFloat(props, "anchor." + element.name() + ".x", fallback[0]);
                float y = readFloat(props, "anchor." + element.name() + ".y", fallback[1]);
                setElementAnchor(element, x, y);
            }
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
        props.setProperty("showWeather", Boolean.toString(showWeather));
        props.setProperty("fontStyle", fontStyle.name());
        props.setProperty("layoutMode", layoutMode.name());
        props.setProperty("backgroundMode", backgroundMode.name());
        props.setProperty("singleBackgroundColor", Integer.toString(singleBackgroundColor));
        for (DayPhase phase : DayPhase.values()) {
            props.setProperty("backgroundColor." + phase.name(),
                    Integer.toString(phaseBackgroundColors.get(phase)));
        }
        props.setProperty("backgroundAlpha", Integer.toString(backgroundAlpha));
        props.setProperty("textAlpha", Integer.toString(textAlpha));
        props.setProperty("weatherIconAlpha", Integer.toString(weatherIconAlpha));
        props.setProperty("textColor", Integer.toString(textColor));
        props.setProperty("anchorX", Float.toString(anchorX));
        props.setProperty("anchorY", Float.toString(anchorY));
        for (ElementId element : ElementId.values()) {
            float[] anchor = elementAnchors.get(element);
            props.setProperty("anchor." + element.name() + ".x", Float.toString(anchor[0]));
            props.setProperty("anchor." + element.name() + ".y", Float.toString(anchor[1]));
        }
        try (FileWriter writer = new FileWriter(configFile.toFile())) {
            props.store(writer, "Timefully settings");
        } catch (IOException e) {
            Timefully.LOGGER.warn("Could not write {}: {}", FILE_NAME, e.getMessage());
        }
    }

    private static int clampByte(int value) {
        return Math.max(0, Math.min(255, value));
    }

    private static boolean readBool(Properties props, String key, boolean fallback) {
        String value = props.getProperty(key);
        return value == null ? fallback : Boolean.parseBoolean(value);
    }

    private static <E extends Enum<E>> E readEnum(Properties props, String key, Class<E> type, E fallback) {
        String value = props.getProperty(key);
        if (value == null) {
            return fallback;
        }
        try {
            return Enum.valueOf(type, value);
        } catch (IllegalArgumentException e) {
            return fallback;
        }
    }

    private static int readInt(Properties props, String key, int fallback) {
        String value = props.getProperty(key);
        if (value == null) {
            return fallback;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
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
