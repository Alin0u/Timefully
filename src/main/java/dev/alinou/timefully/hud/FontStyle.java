package dev.alinou.timefully.hud;

/** How the in-game time is drawn. */
public enum FontStyle {

    DEFAULT,
    BOLD,
    FANCY;

    public String translationKey() {
        return "timefully.config.font." + name().toLowerCase();
    }
}
