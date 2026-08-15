package dev.alinou.timefully.hud;

import dev.alinou.timefully.time.DayPhase;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;

/**
 * Paints the panel background: sky, sun or moon, mountains and weather.
 *
 * Everything is drawn from filled rectangles rather than a texture so the
 * scene fits whatever size the panel ends up being.
 */
final class ScenePainter {

    /** Height of the mountain band, as a share of the panel height. */
    private static final float RIDGE_TOP = 0.62f;

    private ScenePainter() {
    }

    static void paint(DrawContext context, int x, int y, int width, int height,
                      DayPhase phase, WeatherState weather, PanelShape shape) {
        Palette palette = Palette.of(phase, weather);

        for (int row = 0; row < height; row++) {
            int inset = shape.equals(PanelShape.CIRCLE)
                    ? circleInset(row, width, height)
                    : roundedInset(row, height);
            if (inset < 0) {
                continue;
            }
            int rowColor = skyColor(palette, row, height);
            context.fill(x + inset, y + row, x + width - inset, y + row + 1, rowColor);
        }

        drawCelestialBody(context, x, y, width, height, phase, palette, shape);
        drawRidges(context, x, y, width, height, palette, shape);
        drawTopScrim(context, x, y, width, height, shape);

        if (weather == WeatherState.RAIN || weather == WeatherState.THUNDER) {
            drawRain(context, x, y, width, height, palette, shape);
        }
    }

    /** Vertical gradient between the palette's top and horizon colours. */
    private static int skyColor(Palette palette, int row, int height) {
        float t = height <= 1 ? 0f : row / (float) (height - 1);
        return lerpColor(palette.skyTop, palette.skyBottom, Math.min(1f, t / RIDGE_TOP));
    }

    private static void drawCelestialBody(DrawContext context, int x, int y, int width, int height,
                                          DayPhase phase, Palette palette, PanelShape shape) {
        // Sits low so it tucks behind the ridges and stays clear of the clock.
        int radius = Math.max(3, Math.round(height * 0.15f));
        int cx = x + Math.round(width * 0.17f);
        int cy = y + Math.round(height * 0.68f);

        for (int dy = -radius; dy <= radius; dy++) {
            int span = (int) Math.sqrt(Math.max(0, radius * radius - dy * dy));
            int row = cy + dy - y;
            if (row < 0 || row >= height) {
                continue;
            }
            int inset = shape.equals(PanelShape.CIRCLE)
                    ? circleInset(row, width, height)
                    : roundedInset(row, height);
            if (inset < 0) {
                continue;
            }
            int left = Math.max(x + inset, cx - span);
            int right = Math.min(x + width - inset, cx + span + 1);
            if (right > left) {
                context.fill(left, cy + dy, right, cy + dy + 1, palette.body);
            }
        }
    }

    /** Two overlapping mountain bands, back one lighter than the front. */
    private static void drawRidges(DrawContext context, int x, int y, int width, int height,
                                   Palette palette, PanelShape shape) {
        int ridgeTop = Math.round(height * RIDGE_TOP);
        drawRidge(context, x, y, width, height, ridgeTop, 0.62f, 3, palette.ridgeBack, shape);
        drawRidge(context, x, y, width, height, ridgeTop + Math.max(2, height / 12), 0.85f, 2,
                palette.ridgeFront, shape);
    }

    private static void drawRidge(DrawContext context, int x, int y, int width, int height,
                                  int baseRow, float frequency, int peaks, int color, PanelShape shape) {
        for (int col = 0; col < width; col++) {
            float t = col / (float) Math.max(1, width - 1);
            float wave = 0f;
            for (int p = 1; p <= peaks; p++) {
                wave += MathHelper.sin((t * peaks + p * 0.37f) * frequency * MathHelper.PI * 2f) / p;
            }
            int top = baseRow - Math.round(wave * height * 0.06f);
            top = MathHelper.clamp(top, 0, height);

            for (int row = top; row < height; row++) {
                int inset = shape.equals(PanelShape.CIRCLE)
                        ? circleInset(row, width, height)
                        : roundedInset(row, height);
                if (inset < 0 || col < inset || col >= width - inset) {
                    continue;
                }
                context.fill(x + col, y + row, x + col + 1, y + row + 1, color);
            }
        }
    }

    private static void drawRain(DrawContext context, int x, int y, int width, int height,
                                 Palette palette, PanelShape shape) {
        // Kept to the lower sky so the clock above stays readable.
        int step = Math.max(6, width / 8);
        int top = Math.round(height * 0.34f);
        for (int col = step / 2; col < width; col += step) {
            for (int row = top; row < Math.round(height * RIDGE_TOP); row += 5) {
                int inset = shape.equals(PanelShape.CIRCLE)
                        ? circleInset(row, width, height)
                        : roundedInset(row, height);
                if (inset < 0 || col < inset || col >= width - inset) {
                    continue;
                }
                context.fill(x + col, y + row, x + col + 1, y + row + 3, palette.rain);
            }
        }
    }

    /** Darkens the upper area so the clock stays readable over any sky. */
    private static void drawTopScrim(DrawContext context, int x, int y, int width, int height,
                                     PanelShape shape) {
        int scrimHeight = Math.round(height * 0.46f);
        for (int row = 0; row < scrimHeight; row++) {
            int inset = shape.equals(PanelShape.CIRCLE)
                    ? circleInset(row, width, height)
                    : roundedInset(row, height);
            if (inset < 0) {
                continue;
            }
            float fade = 1f - row / (float) scrimHeight;
            int alpha = Math.round(70 * fade);
            context.fill(x + inset, y + row, x + width - inset, y + row + 1, alpha << 24);
        }
    }

    /** Pixels to skip at each end of a row so corners look rounded. */
    private static int roundedInset(int row, int height) {
        int corner = 3;
        if (row < corner) {
            return corner - row - 1;
        }
        if (row >= height - corner) {
            return corner - (height - row);
        }
        return 0;
    }

    /** Pixels to skip at each end of a row so the panel reads as a circle. */
    private static int circleInset(int row, int width, int height) {
        float rx = width / 2f;
        float ry = height / 2f;
        float dy = (row + 0.5f - ry) / ry;
        float inner = 1f - dy * dy;
        if (inner <= 0f) {
            return -1;
        }
        return Math.round(rx - rx * (float) Math.sqrt(inner));
    }

    private static int lerpColor(int from, int to, float t) {
        t = MathHelper.clamp(t, 0f, 1f);
        int a = lerpChannel(from >> 24 & 0xFF, to >> 24 & 0xFF, t);
        int r = lerpChannel(from >> 16 & 0xFF, to >> 16 & 0xFF, t);
        int g = lerpChannel(from >> 8 & 0xFF, to >> 8 & 0xFF, t);
        int b = lerpChannel(from & 0xFF, to & 0xFF, t);
        return a << 24 | r << 16 | g << 8 | b;
    }

    private static int lerpChannel(int from, int to, float t) {
        return Math.round(from + (to - from) * t);
    }

    /** Colours for one combination of day phase and weather. */
    private record Palette(int skyTop, int skyBottom, int body, int ridgeBack, int ridgeFront, int rain) {

        static Palette of(DayPhase phase, WeatherState weather) {
            if (weather == WeatherState.THUNDER) {
                return new Palette(0xF01A1C26, 0xF03A3D48, 0xFF6E7280,
                        0xFF2A2D38, 0xFF1A1C24, 0x90C8D4F0);
            }
            if (weather == WeatherState.RAIN) {
                return new Palette(0xF03A4050, 0xF06A7284, 0xFF9AA2B4,
                        0xFF4A5060, 0xFF32384A, 0x90BAC8E8);
            }
            return switch (phase) {
                case DAWN -> new Palette(0xF04A4468, 0xF0E88A54, 0xFFFFC46E,
                        0xFF7A5A62, 0xFF4A3A48, 0);
                case DAY -> new Palette(0xF04A86C8, 0xF0A8D2EE, 0xFFFFE08A,
                        0xFF5A7A86, 0xFF3A5460, 0);
                case DUSK -> new Palette(0xF03A3660, 0xF0E0705E, 0xFFFF9E70,
                        0xFF6A4A5E, 0xFF3E2E44, 0);
                case NIGHT -> new Palette(0xF0141830, 0xF02E3658, 0xFFE8EEFF,
                        0xFF262C46, 0xFF161A2C, 0);
            };
        }
    }
}
