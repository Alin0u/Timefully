package dev.alinou.timefully.hud;

import dev.alinou.timefully.config.TimefullyConfig;
import dev.alinou.timefully.time.DayPhase;
import dev.alinou.timefully.time.GameTime;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Works out the widget's lines and box size for the current settings.
 * Shared by the HUD renderer and the reposition screen so both agree on
 * where the widget is and how big it is.
 */
public final class WidgetLayout {

    public static final int PADDING = 4;
    public static final int LINE_HEIGHT = 12;
    public static final int ICON_SIZE = 12;
    public static final int ICON_GAP = 3;

    private static final DateTimeFormatter REAL_TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    /** One rendered row: text, and optionally a phase icon in front of it. */
    public record Line(Text text, DayPhase icon) {
    }

    private final List<Line> lines;
    private final int width;
    private final int height;

    private WidgetLayout(List<Line> lines, int width, int height) {
        this.lines = lines;
        this.width = width;
        this.height = height;
    }

    public List<Line> lines() {
        return lines;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public static WidgetLayout build(TextRenderer textRenderer, long worldTime) {
        DayPhase phase = DayPhase.of(worldTime);
        List<Line> lines = new ArrayList<>(3);

        if (TimefullyConfig.showRealTime()) {
            lines.add(new Line(Text.literal(LocalTime.now().format(REAL_TIME_FORMAT)), null));
        }
        if (TimefullyConfig.showGameTime()) {
            Text time = Text.literal(String.format("%02d:%02d",
                    GameTime.hourOfDay(worldTime), GameTime.minuteOfHour(worldTime)));
            lines.add(new Line(time, TimefullyConfig.showPhaseIcon() ? phase : null));
        }
        if (TimefullyConfig.showPhaseLabel()) {
            lines.add(new Line(Text.translatable(phase.translationKey()), null));
        }

        int contentWidth = 0;
        for (Line line : lines) {
            int lineWidth = textRenderer.getWidth(line.text());
            if (line.icon() != null) {
                lineWidth += ICON_SIZE + ICON_GAP;
            }
            contentWidth = Math.max(contentWidth, lineWidth);
        }

        int width = contentWidth + PADDING * 2;
        int height = lines.size() * LINE_HEIGHT + PADDING * 2;
        return new WidgetLayout(lines, width, height);
    }

    /** Top-left corner for the stored anchor, kept fully on screen. */
    public static int originX(MinecraftClient client, int width) {
        int screenWidth = client.getWindow().getScaledWidth();
        return Math.round(TimefullyConfig.anchorX() * Math.max(0, screenWidth - width));
    }

    public static int originY(MinecraftClient client, int height) {
        int screenHeight = client.getWindow().getScaledHeight();
        return Math.round(TimefullyConfig.anchorY() * Math.max(0, screenHeight - height));
    }
}
