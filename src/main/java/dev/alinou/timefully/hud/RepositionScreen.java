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

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        MinecraftClient client = MinecraftClient.getInstance();
        long worldTime = client.world == null ? 0L : client.world.getTimeOfDay();
        WidgetLayout layout = WidgetLayout.build(textRenderer, worldTime);
        if (layout.lines().isEmpty()) {
            context.drawCenteredTextWithShadow(textRenderer,
                    Text.translatable("timefully.reposition.nothing_shown"),
                    width / 2, height / 2, 0xFFFFFFFF);
            return;
        }

        int x = WidgetLayout.originX(client, layout.width());
        int y = WidgetLayout.originY(client, layout.height());

        TimefullyHud.draw(context, client, layout, x, y, HIGHLIGHT_COLOR);
        context.drawBorder(x, y, layout.width(), layout.height(), OUTLINE_COLOR);

        context.drawCenteredTextWithShadow(textRenderer,
                Text.translatable("timefully.reposition.hint"),
                width / 2, height - 20, 0xFFFFFFFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        MinecraftClient client = MinecraftClient.getInstance();
        long worldTime = client.world == null ? 0L : client.world.getTimeOfDay();
        WidgetLayout layout = WidgetLayout.build(textRenderer, worldTime);

        int x = WidgetLayout.originX(client, layout.width());
        int y = WidgetLayout.originY(client, layout.height());

        if (mouseX >= x && mouseX < x + layout.width() && mouseY >= y && mouseY < y + layout.height()) {
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
        long worldTime = client.world == null ? 0L : client.world.getTimeOfDay();
        WidgetLayout layout = WidgetLayout.build(textRenderer, worldTime);

        int freeX = Math.max(1, width - layout.width());
        int freeY = Math.max(1, height - layout.height());
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
