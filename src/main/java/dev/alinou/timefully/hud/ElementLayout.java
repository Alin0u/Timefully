package dev.alinou.timefully.hud;

import dev.alinou.timefully.config.TimefullyConfig;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Works out where each visible element sits.
 *
 * In GROUPED mode all shown elements stack under one shared anchor. In
 * SEPARATED mode each element carries its own anchor and is placed
 * independently. Shared by the HUD renderer and the reposition screen so
 * both agree on where things are.
 */
public final class ElementLayout {

    public record Placement(ElementId element, int x, int y, int width, int height) {
    }

    private ElementLayout() {
    }

    public static List<Placement> compute(MinecraftClient client, long worldTime) {
        List<ElementId> visible = new ArrayList<>(3);
        if (TimefullyConfig.showRealTime()) {
            visible.add(ElementId.REAL_TIME);
        }
        if (TimefullyConfig.showGameTime()) {
            visible.add(ElementId.GAME_TIME);
        }
        if (TimefullyConfig.showWeather()) {
            visible.add(ElementId.WEATHER);
        }

        return TimefullyConfig.layoutMode() == LayoutMode.SEPARATED
                ? separated(client, worldTime, visible)
                : grouped(client, worldTime, visible);
    }

    private static List<Placement> grouped(MinecraftClient client, long worldTime, List<ElementId> visible) {
        List<Placement> placements = new ArrayList<>(visible.size());
        if (visible.isEmpty()) {
            return placements;
        }

        int width = 0;
        int totalHeight = 0;
        for (ElementId element : visible) {
            width = Math.max(width, ElementRenderer.boxWidth(client, element, worldTime));
            totalHeight += ElementRenderer.boxHeight(client, element);
        }

        int originX = originX(client, width);
        int originY = originY(client, totalHeight);

        int y = originY;
        for (ElementId element : visible) {
            int height = ElementRenderer.boxHeight(client, element);
            placements.add(new Placement(element, originX, y, width, height));
            y += height;
        }
        return placements;
    }

    private static List<Placement> separated(MinecraftClient client, long worldTime, List<ElementId> visible) {
        List<Placement> placements = new ArrayList<>(visible.size());
        for (ElementId element : visible) {
            int width = ElementRenderer.boxWidth(client, element, worldTime);
            int height = ElementRenderer.boxHeight(client, element);
            int x = originXFor(client, width, TimefullyConfig.elementAnchorX(element));
            int y = originYFor(client, height, TimefullyConfig.elementAnchorY(element));
            placements.add(new Placement(element, x, y, width, height));
        }
        return placements;
    }

    /** Top-left corner for the GROUPED anchor, kept fully on screen. */
    public static int originX(MinecraftClient client, int width) {
        return originXFor(client, width, TimefullyConfig.anchorX());
    }

    public static int originY(MinecraftClient client, int height) {
        return originYFor(client, height, TimefullyConfig.anchorY());
    }

    private static int originXFor(MinecraftClient client, int width, float anchor) {
        int screenWidth = client.getWindow().getScaledWidth();
        return Math.round(anchor * Math.max(0, screenWidth - width));
    }

    private static int originYFor(MinecraftClient client, int height, float anchor) {
        int screenHeight = client.getWindow().getScaledHeight();
        return Math.round(anchor * Math.max(0, screenHeight - height));
    }
}
