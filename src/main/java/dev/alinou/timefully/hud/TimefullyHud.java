package dev.alinou.timefully.hud;

import dev.alinou.timefully.Timefully;
import dev.alinou.timefully.time.DayPhase;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

/** Draws the clock widget each frame. */
public class TimefullyHud implements HudRenderCallback {

    public static final Identifier ICONS = Identifier.of(Timefully.MODID, "textures/icons.png");

    /** Source sheet is four 16x16 cells in a row. */
    private static final int SHEET_WIDTH = 64;
    private static final int SHEET_HEIGHT = 16;
    private static final int CELL = 16;

    static final int TEXT_COLOR = 0xFFFFFFFF;
    static final int BACKGROUND_COLOR = 0x78000000;

    @Override
    public void onHudRender(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null || client.options.hudHidden) {
            return;
        }

        WidgetLayout layout = WidgetLayout.build(client.textRenderer, client.world.getTimeOfDay());
        if (layout.lines().isEmpty()) {
            return;
        }

        int x = WidgetLayout.originX(client, layout.width());
        int y = WidgetLayout.originY(client, layout.height());
        draw(context, client, layout, x, y, BACKGROUND_COLOR);
    }

    /** Shared by the HUD and the reposition screen. */
    static void draw(DrawContext context, MinecraftClient client, WidgetLayout layout,
                     int x, int y, int backgroundColor) {
        context.fill(x, y, x + layout.width(), y + layout.height(), backgroundColor);

        int lineY = y + WidgetLayout.PADDING;
        for (WidgetLayout.Line line : layout.lines()) {
            int textX = x + WidgetLayout.PADDING;
            if (line.icon() != null) {
                drawPhaseIcon(context, line.icon(), textX, lineY - 1);
                textX += WidgetLayout.ICON_SIZE + WidgetLayout.ICON_GAP;
            }
            int textY = lineY + (WidgetLayout.LINE_HEIGHT - client.textRenderer.fontHeight) / 2;
            context.drawTextWithShadow(client.textRenderer, line.text(), textX, textY, TEXT_COLOR);
            lineY += WidgetLayout.LINE_HEIGHT;
        }
    }

    private static void drawPhaseIcon(DrawContext context, DayPhase phase, int x, int y) {
        context.drawTexture(ICONS, x, y, WidgetLayout.ICON_SIZE, WidgetLayout.ICON_SIZE,
                phase.iconIndex() * CELL, 0, CELL, CELL, SHEET_WIDTH, SHEET_HEIGHT);
    }
}
