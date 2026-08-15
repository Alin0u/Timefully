package dev.alinou.timefully.hud;

import dev.alinou.timefully.config.TimefullyConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

/** Drag the widget to place it, then close to save. */
public class RepositionScreen extends Screen {

    private static final int HIGHLIGHT_COLOR = 0xA0202020;
    private static final int OUTLINE_COLOR = 0xFFFFFFFF;

    private final Screen parent;

    private boolean dragging;
    private int grabOffsetX;
    private int grabOffsetY;

    public RepositionScreen(Screen parent) {
        super(Text.translatable("timefully.reposition.title"));
        this.parent = parent;
    }

    /** Widget size for whichever mode is active. Null when nothing is shown. */
    private int[] widgetBounds(MinecraftClient client) {
        long worldTime = client.world == null ? 0L : client.world.getTimeOfDay();
        int w;
        int h;
        if (TimefullyConfig.fancyMode()) {
            w = FancyPanel.width();
            h = FancyPanel.height();
        } else {
            WidgetLayout layout = WidgetLayout.build(textRenderer, worldTime);
            if (layout.lines().isEmpty()) {
                return null;
            }
            w = layout.width();
            h = layout.height();
        }
        return new int[] {WidgetLayout.originX(client, w), WidgetLayout.originY(client, h), w, h};
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        MinecraftClient client = MinecraftClient.getInstance();
        int[] bounds = widgetBounds(client);
        if (bounds == null) {
            context.drawCenteredTextWithShadow(textRenderer,
                    Text.translatable("timefully.reposition.nothing_shown"),
                    width / 2, height / 2, 0xFFFFFFFF);
            return;
        }

        int x = bounds[0];
        int y = bounds[1];
        long worldTime = client.world == null ? 0L : client.world.getTimeOfDay();

        if (TimefullyConfig.fancyMode()) {
            FancyPanel.render(context, client, x, y, worldTime);
        } else {
            WidgetLayout layout = WidgetLayout.build(textRenderer, worldTime);
            TimefullyHud.draw(context, client, layout, x, y, HIGHLIGHT_COLOR);
        }
        context.drawBorder(x, y, bounds[2], bounds[3], OUTLINE_COLOR);

        context.drawCenteredTextWithShadow(textRenderer,
                Text.translatable("timefully.reposition.hint"),
                width / 2, height - 20, 0xFFFFFFFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        MinecraftClient client = MinecraftClient.getInstance();
        int[] bounds = widgetBounds(client);
        if (bounds == null) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        int x = bounds[0];
        int y = bounds[1];
        if (mouseX >= x && mouseX < x + bounds[2] && mouseY >= y && mouseY < y + bounds[3]) {
            dragging = true;
            grabOffsetX = (int) mouseX - x;
            grabOffsetY = (int) mouseY - y;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!dragging) {
            return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }

        MinecraftClient client = MinecraftClient.getInstance();
        int[] bounds = widgetBounds(client);
        if (bounds == null) {
            return true;
        }

        int freeX = Math.max(1, width - bounds[2]);
        int freeY = Math.max(1, height - bounds[3]);
        TimefullyConfig.setAnchor((float) (mouseX - grabOffsetX) / freeX,
                (float) (mouseY - grabOffsetY) / freeY);
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (dragging) {
            dragging = false;
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
