package dev.alinou.timefully.config;

import dev.alinou.timefully.hud.FontStyle;
import dev.alinou.timefully.hud.PanelShape;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.text.Text;

/**
 * Plain settings screen used when Cloth Config is not installed.
 * Deliberately free of any Cloth reference.
 */
public class FallbackConfigScreen extends Screen {

    private static final int WIDGET_WIDTH = 220;
    private static final int WIDGET_HEIGHT = 20;
    private static final int SPACING = 4;

    private final Screen parent;

    public FallbackConfigScreen(Screen parent) {
        super(Text.translatable("timefully.config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int x = (width - WIDGET_WIDTH) / 2;
        int y = height / 4;

        addToggle(x, y, "timefully.config.show_real_time",
                TimefullyConfig.showRealTime(), TimefullyConfig::setShowRealTime);
        y += WIDGET_HEIGHT + SPACING;

        addToggle(x, y, "timefully.config.show_game_time",
                TimefullyConfig.showGameTime(), TimefullyConfig::setShowGameTime);
        y += WIDGET_HEIGHT + SPACING;

        addToggle(x, y, "timefully.config.show_phase_icon",
                TimefullyConfig.showPhaseIcon(), TimefullyConfig::setShowPhaseIcon);
        y += WIDGET_HEIGHT + SPACING;

        addToggle(x, y, "timefully.config.show_weather",
                TimefullyConfig.showWeather(), TimefullyConfig::setShowWeather);
        y += WIDGET_HEIGHT + SPACING;

        addToggle(x, y, "timefully.config.fancy_mode",
                TimefullyConfig.fancyMode(), TimefullyConfig::setFancyMode);
        y += WIDGET_HEIGHT + SPACING;

        addDrawableChild(CyclingButtonWidget
                .<PanelShape>builder(value -> Text.translatable(value.translationKey()))
                .values(PanelShape.values())
                .initially(TimefullyConfig.panelShape())
                .build(x, y, WIDGET_WIDTH, WIDGET_HEIGHT,
                        Text.translatable("timefully.config.panel_shape"),
                        (button, value) -> TimefullyConfig.setPanelShape(value)));
        y += WIDGET_HEIGHT + SPACING;

        addDrawableChild(CyclingButtonWidget
                .<FontStyle>builder(value -> Text.translatable(value.translationKey()))
                .values(FontStyle.values())
                .initially(TimefullyConfig.fontStyle())
                .build(x, y, WIDGET_WIDTH, WIDGET_HEIGHT,
                        Text.translatable("timefully.config.font_style"),
                        (button, value) -> TimefullyConfig.setFontStyle(value)));
        y += (WIDGET_HEIGHT + SPACING) * 2;

        addDrawableChild(ButtonWidget.builder(Text.translatable("gui.done"), button -> close())
                .dimensions(x, y, WIDGET_WIDTH, WIDGET_HEIGHT)
                .build());
    }

    private void addToggle(int x, int y, String translationKey, boolean initial,
                           java.util.function.Consumer<Boolean> setter) {
        addDrawableChild(CyclingButtonWidget.onOffBuilder(initial)
                .build(x, y, WIDGET_WIDTH, WIDGET_HEIGHT, Text.translatable(translationKey),
                        (button, value) -> setter.accept(value)));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 20, 0xFFFFFFFF);
        context.drawCenteredTextWithShadow(textRenderer,
                Text.translatable("timefully.config.no_cloth"),
                width / 2, 36, 0xFFA0A0A0);
        context.drawCenteredTextWithShadow(textRenderer,
                Text.translatable("timefully.config.no_cloth_colors"),
                width / 2, 48, 0xFFA0A0A0);
    }

    @Override
    public void close() {
        TimefullyConfig.save();
        client.setScreen(parent);
    }
}
