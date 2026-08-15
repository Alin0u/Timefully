package dev.alinou.timefully.hud;

import dev.alinou.timefully.config.TimefullyConfig;
import dev.alinou.timefully.time.DayPhase;
import net.minecraft.client.gui.DrawContext;

/**
 * Paints the panel background: a flat colour, either following the day
 * phase automatically or set by the player, at the configured
 * transparency.
 */
final class ScenePainter {

    private ScenePainter() {
    }

    static void paint(DrawContext context, int x, int y, int width, int height,
                      DayPhase phase, PanelShape shape) {
        int rgb = resolveColor(phase);
        int color = TimefullyConfig.backgroundAlpha() << 24 | rgb;

        for (int row = 0; row < height; row++) {
            int inset = shape == PanelShape.CIRCLE
                    ? circleInset(row, width, height)
                    : roundedInset(row, height);
            if (inset < 0) {
                continue;
            }
            context.fill(x + inset, y + row, x + width - inset, y + row + 1, color);
        }
    }

    private static int resolveColor(DayPhase phase) {
        return switch (TimefullyConfig.backgroundMode()) {
            case STANDARD -> StandardColors.forPhase(phase);
            case SINGLE_COLOR -> TimefullyConfig.singleBackgroundColor();
            case CUSTOM_PER_PHASE -> TimefullyConfig.phaseBackgroundColor(phase);
        };
    }

    /** Pixels to skip at each end of a row so corners look rounded. */
    static int roundedInset(int row, int height) {
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
    static int circleInset(int row, int width, int height) {
        float rx = width / 2f;
        float ry = height / 2f;
        float dy = (row + 0.5f - ry) / ry;
        float inner = 1f - dy * dy;
        if (inner <= 0f) {
            return -1;
        }
        return Math.round(rx - rx * (float) Math.sqrt(inner));
    }
}
