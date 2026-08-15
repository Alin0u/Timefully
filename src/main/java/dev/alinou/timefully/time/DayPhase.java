package dev.alinou.timefully.time;

/**
 * Phase of the Minecraft day cycle, used to pick the HUD icon.
 * Boundaries follow the game's own light-level transitions: dawn begins
 * at tick 23000, dusk at 12000, and night proper at 13000.
 */
public enum DayPhase {

    DAWN,
    DAY,
    DUSK,
    NIGHT;

    public static DayPhase of(long worldTime) {
        int t = GameTime.ticksIntoDay(worldTime);
        if (t >= 23000) {
            return DAWN;
        }
        if (t >= 13000) {
            return NIGHT;
        }
        if (t >= 12000) {
            return DUSK;
        }
        return DAY;
    }

    public String translationKey() {
        return "timefully.phase." + name().toLowerCase();
    }
}
