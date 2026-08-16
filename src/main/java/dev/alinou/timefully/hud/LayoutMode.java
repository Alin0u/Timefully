package dev.alinou.timefully.hud;

/** Whether the widget's elements are positioned together or independently. */
public enum LayoutMode {
    /** Real time, in-game time and weather stack in one block at one position. */
    GROUPED,
    /** Each shown element has its own position and its own background box. */
    SEPARATED;

    public String translationKey() {
        return "timefully.config.layout_mode." + name().toLowerCase();
    }
}
