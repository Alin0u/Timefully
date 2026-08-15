package dev.alinou.timefully.hud;

import net.minecraft.client.world.ClientWorld;

/** In-game weather, as far as the client can see it. */
public enum WeatherState {

    CLEAR,
    RAIN,
    THUNDER;

    public static WeatherState of(ClientWorld world) {
        if (world == null) {
            return CLEAR;
        }
        if (world.isThundering()) {
            return THUNDER;
        }
        return world.isRaining() ? RAIN : CLEAR;
    }

    /** Column of this state in textures/weather.png. */
    public int iconIndex() {
        return ordinal();
    }

    public String translationKey() {
        return "timefully.weather." + name().toLowerCase();
    }
}
