package dev.alinou.timefully.hud;

import dev.alinou.timefully.Timefully;
import dev.alinou.timefully.config.TimefullyConfig;
import dev.alinou.timefully.time.DayPhase;
import dev.alinou.timefully.time.GameTime;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * The fancy widget: a big seven-segment in-game clock over a scene that
 * follows the day cycle and weather.
 *
 * Layout follows the rows that are switched on. The second row holds the
 * real time and the weather icon; if only one of them is shown it sits in
 * the middle, and if neither is shown the big clock moves down to take up
 * the free space.
 */
public final class FancyPanel {

    static final Identifier DIGITS = Identifier.of(Timefully.MODID, "textures/digits.png");
    static final Identifier WEATHER = Identifier.of(Timefully.MODID, "textures/weather.png");

    private static final int DIGIT_W = 12;
    private static final int DIGIT_H = 20;
    private static final int DIGIT_COUNT = 11;
    private static final int SHEET_W = DIGIT_W * DIGIT_COUNT;

    private static final int WEATHER_CELL = 12;
    private static final int WEATHER_SHEET_W = WEATHER_CELL * 3;

    /** Drawn size of one big digit. */
    private static final int BIG_W = 11;
    private static final int BIG_H = 18;
    private static final int BIG_GAP = 1;
    private static final int COLON_W = 6;

    private static final int PANEL_W = 104;
    private static final int PANEL_H = 74;
    private static final int CIRCLE_SIZE = 96;

    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int RIM_COLOR = 0x66FFFFFF;

    private static final DateTimeFormatter REAL_TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private FancyPanel() {
    }

    public static int width() {
        return TimefullyConfig.panelShape() == PanelShape.CIRCLE ? CIRCLE_SIZE : PANEL_W;
    }

    public static int height() {
        return TimefullyConfig.panelShape() == PanelShape.CIRCLE ? CIRCLE_SIZE : PANEL_H;
    }

    public static void render(DrawContext context, MinecraftClient client, int x, int y, long worldTime) {
        DayPhase phase = DayPhase.of(worldTime);
        WeatherState weather = WeatherState.of(client.world);
        PanelShape shape = TimefullyConfig.panelShape();

        int width = width();
        int height = height();

        ScenePainter.paint(context, x, y, width, height, phase, weather, shape);
        drawRim(context, x, y, width, height, shape);

        boolean showGame = TimefullyConfig.showGameTime();
        boolean showReal = TimefullyConfig.showRealTime();
        boolean showWeather = TimefullyConfig.showWeather();
        boolean secondRow = showReal || showWeather;

        int topPadding = shape == PanelShape.CIRCLE ? Math.round(height * 0.20f) : 8;
        int clockY = secondRow ? topPadding : topPadding + 6;

        if (showGame) {
            drawBigTime(context, x, width, clockY,
                    GameTime.hourOfDay(worldTime), GameTime.minuteOfHour(worldTime));
        }

        if (secondRow) {
            int rowY = clockY + (showGame ? BIG_H + 5 : 0);
            drawSecondRow(context, client, x, width, rowY, showReal, showWeather, weather);
        }
    }

    /** Real time and weather icon, centred as a pair or on their own. */
    private static void drawSecondRow(DrawContext context, MinecraftClient client, int x, int width,
                                      int rowY, boolean showReal, boolean showWeather,
                                      WeatherState weather) {
        String realTime = LocalTime.now().format(REAL_TIME_FORMAT);
        int textWidth = showReal ? client.textRenderer.getWidth(realTime) : 0;
        int iconWidth = showWeather ? WEATHER_CELL : 0;
        int gap = showReal && showWeather ? 4 : 0;

        int totalWidth = textWidth + gap + iconWidth;
        int startX = x + (width - totalWidth) / 2;

        if (showReal) {
            int textY = rowY + (WEATHER_CELL - client.textRenderer.fontHeight) / 2;
            context.drawTextWithShadow(client.textRenderer, realTime, startX, textY, TEXT_COLOR);
        }
        if (showWeather) {
            int iconX = startX + textWidth + gap;
            context.drawTexture(WEATHER, iconX, rowY, WEATHER_CELL, WEATHER_CELL,
                    weather.iconIndex() * WEATHER_CELL, 0, WEATHER_CELL, WEATHER_CELL,
                    WEATHER_SHEET_W, WEATHER_CELL);
        }
    }

    /** HH:MM in seven-segment digits, centred in the panel. */
    private static void drawBigTime(DrawContext context, int x, int width, int y, int hour, int minute) {
        int totalWidth = BIG_W * 4 + BIG_GAP * 4 + COLON_W;
        int cursor = x + (width - totalWidth) / 2;

        cursor = drawDigit(context, cursor, y, hour / 10);
        cursor = drawDigit(context, cursor, y, hour % 10);
        context.drawTexture(DIGITS, cursor, y, COLON_W, BIG_H,
                10 * DIGIT_W, 0, DIGIT_W, DIGIT_H, SHEET_W, DIGIT_H);
        cursor += COLON_W + BIG_GAP;
        cursor = drawDigit(context, cursor, y, minute / 10);
        drawDigit(context, cursor, y, minute % 10);
    }

    private static int drawDigit(DrawContext context, int x, int y, int value) {
        context.drawTexture(DIGITS, x, y, BIG_W, BIG_H,
                value * DIGIT_W, 0, DIGIT_W, DIGIT_H, SHEET_W, DIGIT_H);
        return x + BIG_W + BIG_GAP;
    }

    /** Thin light outline, in the spirit of VoxelMap's map frame. */
    private static void drawRim(DrawContext context, int x, int y, int width, int height, PanelShape shape) {
        if (shape == PanelShape.CIRCLE) {
            drawCircleRim(context, x, y, width, height);
            return;
        }
        context.fill(x + 3, y, x + width - 3, y + 1, RIM_COLOR);
        context.fill(x + 3, y + height - 1, x + width - 3, y + height, RIM_COLOR);
        context.fill(x, y + 3, x + 1, y + height - 3, RIM_COLOR);
        context.fill(x + width - 1, y + 3, x + width, y + height - 3, RIM_COLOR);
        context.fill(x + 1, y + 1, x + 3, y + 3, RIM_COLOR);
        context.fill(x + width - 3, y + 1, x + width - 1, y + 3, RIM_COLOR);
        context.fill(x + 1, y + height - 3, x + 3, y + height - 1, RIM_COLOR);
        context.fill(x + width - 3, y + height - 3, x + width - 1, y + height - 1, RIM_COLOR);
    }

    private static void drawCircleRim(DrawContext context, int x, int y, int width, int height) {
        float rx = width / 2f;
        float ry = height / 2f;
        for (int row = 0; row < height; row++) {
            float dy = (row + 0.5f - ry) / ry;
            float inner = 1f - dy * dy;
            if (inner <= 0f) {
                continue;
            }
            int inset = Math.round(rx - rx * (float) Math.sqrt(inner));
            context.fill(x + inset, y + row, x + inset + 1, y + row + 1, RIM_COLOR);
            context.fill(x + width - inset - 1, y + row, x + width - inset, y + row + 1, RIM_COLOR);
        }
    }
}
