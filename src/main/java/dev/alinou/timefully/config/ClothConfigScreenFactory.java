package dev.alinou.timefully.config;

import dev.alinou.timefully.hud.BackgroundMode;
import dev.alinou.timefully.hud.FontStyle;
import dev.alinou.timefully.hud.LayoutMode;
import dev.alinou.timefully.time.DayPhase;
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
                .startBooleanToggle(Text.translatable("timefully.config.show_weather"),
                        TimefullyConfig.showWeather())
                .setDefaultValue(true)
                .setSaveConsumer(TimefullyConfig::setShowWeather)
                .build());

        display.addEntry(entries
                .startEnumSelector(Text.translatable("timefully.config.layout_mode"),
                        LayoutMode.class, TimefullyConfig.layoutMode())
                .setDefaultValue(LayoutMode.GROUPED)
                .setEnumNameProvider(value -> Text.translatable(((LayoutMode) value).translationKey()))
                .setTooltip(Text.translatable("timefully.config.layout_mode.tooltip"))
                .setSaveConsumer(TimefullyConfig::setLayoutMode)
                .build());

        display.addEntry(entries
                .startTextDescription(Text.translatable("timefully.config.position.description"))
                .build());

        ConfigCategory style = builder.getOrCreateCategory(
                Text.translatable("timefully.config.category.style"));

        style.addEntry(entries
                .startEnumSelector(Text.translatable("timefully.config.font_style"),
                        FontStyle.class, TimefullyConfig.fontStyle())
                .setDefaultValue(FontStyle.DEFAULT)
                .setEnumNameProvider(value -> Text.translatable(((FontStyle) value).translationKey()))
                .setSaveConsumer(TimefullyConfig::setFontStyle)
                .build());

        style.addEntry(entries
                .startColorField(Text.translatable("timefully.config.text_color"), TimefullyConfig.textColor())
                .setDefaultValue(0xFFFFFF)
                .setSaveConsumer(TimefullyConfig::setTextColor)
                .build());

        style.addEntry(alphaSlider(entries, "timefully.config.text_alpha",
                TimefullyConfig.textAlpha(), TimefullyConfig::setTextAlpha));
        style.addEntry(alphaSlider(entries, "timefully.config.weather_icon_alpha",
                TimefullyConfig.weatherIconAlpha(), TimefullyConfig::setWeatherIconAlpha));

        ConfigCategory background = builder.getOrCreateCategory(
                Text.translatable("timefully.config.category.background"));

        background.addEntry(entries
                .startEnumSelector(Text.translatable("timefully.config.background_mode"),
                        BackgroundMode.class, TimefullyConfig.backgroundMode())
                .setDefaultValue(BackgroundMode.STANDARD)
                .setEnumNameProvider(value -> Text.translatable(((BackgroundMode) value).translationKey()))
                .setTooltip(Text.translatable("timefully.config.background_mode.tooltip"))
                .setSaveConsumer(TimefullyConfig::setBackgroundMode)
                .build());

        background.addEntry(entries
                .startColorField(Text.translatable("timefully.config.background_color"),
                        TimefullyConfig.singleBackgroundColor())
                .setDefaultValue(0x5A7A86)
                .setTooltip(Text.translatable("timefully.config.background_color.tooltip"))
                .setSaveConsumer(TimefullyConfig::setSingleBackgroundColor)
                .build());

        for (DayPhase phase : DayPhase.values()) {
            background.addEntry(entries
                    .startColorField(Text.translatable("timefully.phase." + phase.name().toLowerCase()),
                            TimefullyConfig.phaseBackgroundColor(phase))
                    .setDefaultValue(TimefullyConfig.phaseBackgroundColor(phase))
                    .setTooltip(Text.translatable("timefully.config.background_color_phase.tooltip"))
                    .setSaveConsumer(rgb -> TimefullyConfig.setPhaseBackgroundColor(phase, rgb))
                    .build());
        }

        background.addEntry(alphaSlider(entries, "timefully.config.background_alpha",
                TimefullyConfig.backgroundAlpha(), TimefullyConfig::setBackgroundAlpha));

        builder.setDoesConfirmSave(false);
        return builder.build();
    }

    private static me.shedaniel.clothconfig2.api.AbstractConfigListEntry<Integer> alphaSlider(
            ConfigEntryBuilder entries, String translationKey, int value, java.util.function.IntConsumer setter) {
        return entries.startIntSlider(Text.translatable(translationKey), value, 0, 255)
                .setDefaultValue(255)
                .setTextGetter(v -> Text.literal(v + " / 255"))
                .setSaveConsumer(setter::accept)
                .build();
    }
}
