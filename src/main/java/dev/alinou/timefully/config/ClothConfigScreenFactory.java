package dev.alinou.timefully.config;

import dev.alinou.timefully.hud.PanelShape;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

/**
 * Builds the Cloth Config screen.
 *
 * Every reference to Cloth lives in this class and nowhere else, so the
 * JVM only loads it once Cloth is known to be installed. Touching these
 * types from a class that runs without Cloth would throw
 * NoClassDefFoundError.
 */
final class ClothConfigScreenFactory {

    private ClothConfigScreenFactory() {
    }

    static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("timefully.config.title"))
                .setSavingRunnable(TimefullyConfig::save);

        ConfigEntryBuilder entries = builder.entryBuilder();
        ConfigCategory display = builder.getOrCreateCategory(
                Text.translatable("timefully.config.category.display"));

        display.addEntry(entries
                .startBooleanToggle(Text.translatable("timefully.config.show_real_time"),
                        TimefullyConfig.showRealTime())
                .setDefaultValue(true)
                .setSaveConsumer(TimefullyConfig::setShowRealTime)
                .build());

        display.addEntry(entries
                .startBooleanToggle(Text.translatable("timefully.config.show_game_time"),
                        TimefullyConfig.showGameTime())
                .setDefaultValue(true)
                .setSaveConsumer(TimefullyConfig::setShowGameTime)
                .build());

        display.addEntry(entries
                .startBooleanToggle(Text.translatable("timefully.config.show_phase_icon"),
                        TimefullyConfig.showPhaseIcon())
                .setDefaultValue(true)
                .setTooltip(Text.translatable("timefully.config.show_phase_icon.tooltip"))
                .setSaveConsumer(TimefullyConfig::setShowPhaseIcon)
                .build());

        display.addEntry(entries
                .startBooleanToggle(Text.translatable("timefully.config.show_phase_label"),
                        TimefullyConfig.showPhaseLabel())
                .setDefaultValue(false)
                .setSaveConsumer(TimefullyConfig::setShowPhaseLabel)
                .build());

        display.addEntry(entries
                .startBooleanToggle(Text.translatable("timefully.config.show_weather"),
                        TimefullyConfig.showWeather())
                .setDefaultValue(true)
                .setSaveConsumer(TimefullyConfig::setShowWeather)
                .build());

        display.addEntry(entries
                .startTextDescription(Text.translatable("timefully.config.position.description"))
                .build());

        ConfigCategory style = builder.getOrCreateCategory(
                Text.translatable("timefully.config.category.style"));

        style.addEntry(entries
                .startBooleanToggle(Text.translatable("timefully.config.fancy_mode"),
                        TimefullyConfig.fancyMode())
                .setDefaultValue(true)
                .setTooltip(Text.translatable("timefully.config.fancy_mode.tooltip"))
                .setSaveConsumer(TimefullyConfig::setFancyMode)
                .build());

        style.addEntry(entries
                .startEnumSelector(Text.translatable("timefully.config.panel_shape"),
                        PanelShape.class, TimefullyConfig.panelShape())
                .setDefaultValue(PanelShape.ROUNDED)
                .setEnumNameProvider(value -> Text.translatable(((PanelShape) value).translationKey()))
                .setSaveConsumer(TimefullyConfig::setPanelShape)
                .build());

        builder.setDoesConfirmSave(false);
        return builder.build();
    }
}
