package dev.alinou.timefully.hud;

import dev.alinou.timefully.time.DayPhase;
import dev.alinou.timefully.time.GameTime;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/** Draws the clock widget each frame. */
public class TimefullyHud implements HudRenderCallback {

    private static final DateTimeFormatter REAL_TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private static final int MARGIN = 4;
    private static final int PADDING = 4;
    private static final int LINE_HEIGHT = 10;
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int BACKGROUND_COLOR = ColorHelper.Argb.getArgb(120, 0, 0, 0);

    @Override
    public void onHudRender(DrawContext context, net.minecraft.client.render.RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null || client.options.hudHidden) {
            return;
        }

        ClientWorld world = client.world;
        long worldTime = world.getTimeOfDay();

        Text realLine = Text.literal(LocalTime.now().format(REAL_TIME_FORMAT));
        Text gameLine = Text.literal(String.format("%02d:%02d", GameTime.hourOfDay(worldTime),
                GameTime.minuteOfHour(worldTime)));
        Text phaseLine = Text.translatable(DayPhase.of(worldTime).translationKey());

        int width = maxWidth(client, realLine, gameLine, phaseLine) + PADDING * 2;
        int height = LINE_HEIGHT * 3 + PADDING * 2;

        context.fill(MARGIN, MARGIN, MARGIN + width, MARGIN + height, BACKGROUND_COLOR);

        int textX = MARGIN + PADDING;
        int textY = MARGIN + PADDING;
        context.drawTextWithShadow(client.textRenderer, realLine, textX, textY, TEXT_COLOR);
        context.drawTextWithShadow(client.textRenderer, gameLine, textX, textY + LINE_HEIGHT, TEXT_COLOR);
        context.drawTextWithShadow(client.textRenderer, phaseLine, textX, textY + LINE_HEIGHT * 2, TEXT_COLOR);
    }

    private static int maxWidth(MinecraftClient client, Text... lines) {
        int max = 0;
        for (Text line : lines) {
            max = Math.max(max, client.textRenderer.getWidth(line));
        }
        return max;
    }
}
