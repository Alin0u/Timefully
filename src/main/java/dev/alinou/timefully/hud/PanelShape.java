package dev.alinou.timefully.hud;

/** Outline used for the fancy panel. */
public enum PanelShape {

    ROUNDED,
    CIRCLE;

    public String translationKey() {
        return "timefully.config.shape." + name().toLowerCase();
    }
}
