package dev.alinou.timefully.hud;

/** How the panel's background colour is picked. */
public enum BackgroundMode {

    /** Follows the day phase automatically, using the built-in colours. */
    STANDARD,
    /** One fixed colour, regardless of day phase. */
    SINGLE_COLOR,
    /** A separate fixed colour for each day phase. */
    CUSTOM_PER_PHASE;

    public String translationKey() {
        return "timefully.config.background_mode." + name().toLowerCase();
    }
}
