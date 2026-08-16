package dev.alinou.timefully.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.alinou.timefully.Timefully;
import dev.alinou.timefully.config.TimefullyConfig;
import dev.alinou.timefully.time.DayPhase;
import dev.alinou.timefully.time.GameTime;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Draws one element (real time, in-game time, or weather) with its own
 * background box. Used for every element in both layout modes: in
 * GROUPED mode the boxes sit edge to edge in a stack, in SEPARATED mode
 * each is placed and sized independently.
 */
public final class ElementRenderer {

    private static final Identifier DIGITS = Identifier.of(Timefully.MODID, "textures/digits.png");
    private static final Identifier WEATHER = Identifier.of(Timefully.MODID, "textures/weather.png");

    private static final int DIGIT_W = 12;
    private static final int DIGIT_H = 20;
    private static final int DIGIT_COUNT = 11;
    private static final int SHEET_W = DIGIT_W * DIGIT_COUNT;

    private static final int WEATHER_CELL = 12;
    private static final int WEATHER_SHEET_W = WEATHER_CELL * 3;

    /** Drawn size of one seven-segment digit. */
    private static final int SEG_W = 11;
    private static final int SEG_H = 18;
    private static final int SEG_GAP = 1;
    private static final int SEG_COLON_W = 6;

    /** Vertical scale applied to the vanilla font for DEFAULT/BOLD game-time style. */
    private static final float VANILLA_SCALE = 1.8f;

    public static final int PADDING = 4;

    private static final DateTimeFormatter REAL_TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private ElementRenderer() {
    }

    /** Content width/height for an element, excluding padding. */
    public static int contentWidth(MinecraftClient client, ElementId element, long worldTime) {
        return switch (element) {
            case REAL_TIME -> client.textRenderer.getWidth(realTimeText());
            case GAME_TIME -> gameTimeWidth(client, worldTime);
            case WEATHER -> WEATHER_CELL;
        };
    }

    public static int contentHeight(MinecraftClient client, ElementId element) {
        return switch (element) {
            case REAL_TIME -> client.textRenderer.fontHeight;
            case GAME_TIME -> TimefullyConfig.fontStyle() == FontStyle.FANCY
                    ? SEG_H : Math.round(client.textRenderer.fontHeight * VANILLA_SCALE);
            case WEATHER -> WEATHER_CELL;
        };
    }

    public static int boxWidth(MinecraftClient client, ElementId element, long worldTime) {
        return contentWidth(client, element, worldTime) + PADDING * 2;
    }

    public static int boxHeight(MinecraftClient client, ElementId element) {
        return contentHeight(client, element) + PADDING * 2;
    }

    /** Draws one element's background box and content with its top-left corner at (x, y). */
    public static void draw(DrawContext context, MinecraftClient client, ElementId element,
                            int x, int y, long worldTime, DayPhase phase, WeatherState weather) {
        int width = boxWidth(client, element, worldTime);
        int height = boxHeight(client, element);
        FlatBackground.paint(context, x, y, width, height, phase);

        int contentX = x + PADDING;
        int contentY = y + PADDING;
        switch (element) {
            case REAL_TIME -> context.drawTextWithShadow(client.textRenderer, realTimeText(),
                    contentX, contentY, textColor());
            case GAME_TIME -> drawGameTime(context, client, contentX, contentY, worldTime);
            case WEATHER -> drawWeatherIcon(context, contentX, contentY, weather);
        }
    }

    private static String realTimeText() {
        return LocalTime.now().format(REAL_TIME_FORMAT);
    }

    private static int gameTimeWidth(MinecraftClient client, long worldTime) {
        if (TimefullyConfig.fontStyle() == FontStyle.FANCY) {
            return SEG_W * 4 + SEG_GAP * 4 + SEG_COLON_W;
        }
        String time = gameTimeText(worldTime);
        return Math.round(client.textRenderer.getWidth(time) * VANILLA_SCALE);
    }

    private static String gameTimeText(long worldTime) {
        return String.format("%02d:%02d", GameTime.hourOfDay(worldTime), GameTime.minuteOfHour(worldTime));
    }

    private static void drawGameTime(DrawContext context, MinecraftClient client, int x, int y, long worldTime) {
        switch (TimefullyConfig.fontStyle()) {
            case FANCY -> drawSevenSegmentTime(context, x, y, worldTime);
            case BOLD -> drawVanillaTime(context, client, x, y, worldTime, true);
            default -> drawVanillaTime(context, client, x, y, worldTime, false);
        }
    }

    private static void drawVanillaTime(DrawContext context, MinecraftClient client, int x, int y,
                                        long worldTime, boolean bold) {
        Text text = Text.literal(gameTimeText(worldTime)).setStyle(Style.EMPTY.withBold(bold));
        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0);
        context.getMatrices().scale(VANILLA_SCALE, VANILLA_SCALE, 1f);
        context.drawTextWithShadow(client.textRenderer, text, 0, 0, textColor());
        context.getMatrices().pop();
    }

    private static void drawSevenSegmentTime(DrawContext context, int x, int y, long worldTime) {
        int hour = GameTime.hourOfDay(worldTime);
        int minute = GameTime.minuteOfHour(worldTime);
        int cursor = x;

        cursor = drawDigit(context, cursor, y, hour / 10);
        cursor = drawDigit(context, cursor, y, hour % 10);
        drawTinted(context, DIGITS, cursor, y, SEG_COLON_W, SEG_H,
                10 * DIGIT_W, 0, DIGIT_W, DIGIT_H, SHEET_W, DIGIT_H, textColor(), TimefullyConfig.textAlpha());
        cursor += SEG_COLON_W + SEG_GAP;
        cursor = drawDigit(context, cursor, y, minute / 10);
        drawDigit(context, cursor, y, minute % 10);
    }

    private static int drawDigit(DrawContext context, int x, int y, int value) {
        drawTinted(context, DIGITS, x, y, SEG_W, SEG_H,
                value * DIGIT_W, 0, DIGIT_W, DIGIT_H, SHEET_W, DIGIT_H, textColor(), TimefullyConfig.textAlpha());
        return x + SEG_W + SEG_GAP;
    }

    private static void drawWeatherIcon(DrawContext context, int x, int y, WeatherState weather) {
        drawTinted(context, WEATHER, x, y, WEATHER_CELL, WEATHER_CELL,
                weather.iconIndex() * WEATHER_CELL, 0, WEATHER_CELL, WEATHER_CELL,
                WEATHER_SHEET_W, WEATHER_CELL, 0xFFFFFF, TimefullyConfig.weatherIconAlpha());
    }

    /**
     * Draws a region of a white-source sprite tinted to a colour and
     * transparency. DrawContext's drawTexture overloads have no colour
     * parameter in this version, so the tint goes through the shader
     * colour, same approach Chestifier uses for its icon sprites.
     */
    private static void drawTinted(DrawContext context, Identifier texture, int x, int y, int width, int height,
                                   int u, int v, int regionWidth, int regionHeight,
                                   int textureWidth, int textureHeight, int rgb, int alpha) {
        float r = (rgb >> 16 & 0xFF) / 255f;
        float g = (rgb >> 8 & 0xFF) / 255f;
        float b = (rgb & 0xFF) / 255f;
        float a = (alpha & 0xFF) / 255f;
        RenderSystem.setShaderColor(r, g, b, a);
        context.drawTexture(texture, x, y, width, height, u, v, regionWidth, regionHeight,
                textureWidth, textureHeight);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    private static int textColor() {
        return TimefullyConfig.textAlpha() << 24 | TimefullyConfig.textColor();
    }
}
