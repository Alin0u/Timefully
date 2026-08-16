package dev.alinou.timefully.hud;

import dev.alinou.timefully.time.DayPhase;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

/** Draws each visible element every frame. */
public class TimefullyHud implements HudRenderCallback {

    @Override
    public void onHudRender(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null || client.options.hudHidden) {
            return;
        }

        long worldTime = client.world.getTimeOfDay();
        DayPhase phase = DayPhase.of(worldTime);
        WeatherState weather = WeatherState.of(client.world);

        for (ElementLayout.Placement placement : ElementLayout.compute(client, worldTime)) {
            ElementRenderer.draw(context, client, placement.element(), placement.x(), placement.y(),
                    worldTime, phase, weather);
        }
    }
}
