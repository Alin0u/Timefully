package dev.alinou.timefully.hud;

import dev.alinou.timefully.config.TimefullyConfig;
import dev.alinou.timefully.time.DayPhase;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

/**
 * Drag the widget's elements to place them, then close to save.
 *
 * In GROUPED mode the whole stack moves as one block. In SEPARATED mode
 * every visible element gets its own drag handle, all shown at once.
 */
public class RepositionScreen extends Screen {

    private static final int OUTLINE_COLOR = 0xFFFFFFFF;

    private final Screen parent;

    private ElementId dragging;
    private int grabOffsetX;
    private int grabOffsetY;

    public RepositionScreen(Screen parent) {
        super(Text.translatable("timefully.reposition.title"));
        this.parent = parent;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        MinecraftClient client = MinecraftClient.getInstance();
        long worldTime = client.world == null ? 0L : client.world.getTimeOfDay();

        if (dragging != null) {
            updateAnchorFromMouse(client, worldTime, dragging, mouseX, mouseY);
        }

        var placements = ElementLayout.compute(client, worldTime);
        if (placements.isEmpty()) {
            context.drawCenteredTextWithShadow(textRenderer,
                    Text.translatable("timefully.reposition.nothing_shown"),
                    width / 2, height / 2, 0xFFFFFFFF);
            return;
        }

        DayPhase phase = DayPhase.of(worldTime);
        WeatherState weather = WeatherState.of(client.world);
        for (ElementLayout.Placement placement : placements) {
            ElementRenderer.draw(context, client, placement.element(), placement.x(), placement.y(),
                    worldTime, phase, weather);
            context.drawBorder(placement.x(), placement.y(), placement.width(), placement.height(),
                    OUTLINE_COLOR);
        }

        context.drawCenteredTextWithShadow(textRenderer,
                Text.translatable("timefully.reposition.hint"),
                width / 2, height - 20, 0xFFFFFFFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        MinecraftClient client = MinecraftClient.getInstance();
        long worldTime = client.world == null ? 0L : client.world.getTimeOfDay();
        var placements = ElementLayout.compute(client, worldTime);

        for (ElementLayout.Placement placement : placements) {
            if (mouseX >= placement.x() && mouseX < placement.x() + placement.width()
                    && mouseY >= placement.y() && mouseY < placement.y() + placement.height()) {
                dragging = placement.element();
                if (TimefullyConfig.layoutMode() == LayoutMode.SEPARATED) {
                    grabOffsetX = (int) mouseX - placement.x();
                    grabOffsetY = (int) mouseY - placement.y();
                } else {
                    // Grab relative to the whole stack's origin, not this
                    // element's own offset within it, so dragging any row
                    // moves the group from the mouse's position within it.
                    int groupX = placements.get(0).x();
                    int groupY = placements.get(0).y();
                    grabOffsetX = (int) mouseX - groupX;
                    grabOffsetY = (int) mouseY - groupY;
                }
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragging == null) {
            return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
        MinecraftClient client = MinecraftClient.getInstance();
        long worldTime = client.world == null ? 0L : client.world.getTimeOfDay();
        updateAnchorFromMouse(client, worldTime, dragging, mouseX, mouseY);
        return true;
    }

    /**
     * Recomputes the dragged element's anchor from the mouse's current
     * absolute position. Also called every render frame while dragging,
     * not just from mouseDragged: on some platforms mouseDragged's
     * deltaX/deltaY pair does not report Y movement reliably while the
     * button is held, which left widgets stuck against the top edge.
     */
    private void updateAnchorFromMouse(MinecraftClient client, long worldTime, ElementId element,
                                       double mouseX, double mouseY) {
        if (TimefullyConfig.layoutMode() == LayoutMode.SEPARATED) {
            int elementWidth = ElementRenderer.boxWidth(client, element, worldTime);
            int elementHeight = ElementRenderer.boxHeight(client, element);
            int freeX = Math.max(1, width - elementWidth);
            int freeY = Math.max(1, height - elementHeight);
            TimefullyConfig.setElementAnchor(element, (float) (mouseX - grabOffsetX) / freeX,
                    (float) (mouseY - grabOffsetY) / freeY);
            return;
        }

        int groupWidth = 0;
        int groupHeight = 0;
        for (ElementLayout.Placement placement : ElementLayout.compute(client, worldTime)) {
            groupWidth = Math.max(groupWidth, placement.width());
            groupHeight += placement.height();
        }
        int freeX = Math.max(1, width - groupWidth);
        int freeY = Math.max(1, height - groupHeight);
        TimefullyConfig.setAnchor((float) (mouseX - grabOffsetX) / freeX,
                (float) (mouseY - grabOffsetY) / freeY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (dragging != null) {
            dragging = null;
            TimefullyConfig.save();
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void close() {
        TimefullyConfig.save();
        MinecraftClient.getInstance().setScreen(parent);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
