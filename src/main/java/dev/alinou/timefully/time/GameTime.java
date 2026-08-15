package dev.alinou.timefully.time;

/**
 * Conversions between Minecraft's day cycle and wall-clock durations.
 *
 * A Minecraft day is 24000 ticks at 20 ticks per second, so a full day
 * takes 20 real minutes at normal tick rate. Servers and datapacks can
 * change the tick rate, so the real-time side of every conversion is
 * derived from a supplied ticks-per-second rather than assumed.
 */
public final class GameTime {

    public static final int TICKS_PER_DAY = 24000;
    public static final int TICKS_PER_HOUR = TICKS_PER_DAY / 24;
    public static final float DEFAULT_TICKS_PER_SECOND = 20.0f;

    /** Tick offset between Minecraft's day start and 00:00 wall time. */
    private static final int MIDNIGHT_OFFSET = 6000;

    private GameTime() {
    }

    /** Ticks since the start of the current in-game day, always 0-23999. */
    public static int ticksIntoDay(long worldTime) {
        return (int) Math.floorMod(worldTime, TICKS_PER_DAY);
    }

    /** In-game hour of day, 0-23. Minecraft's tick 0 is 06:00. */
    public static int hourOfDay(long worldTime) {
        return (ticksIntoDay(worldTime) + MIDNIGHT_OFFSET) % TICKS_PER_DAY / TICKS_PER_HOUR;
    }

    /** In-game minute within the hour, 0-59. */
    public static int minuteOfHour(long worldTime) {
        int intoHour = (ticksIntoDay(worldTime) + MIDNIGHT_OFFSET) % TICKS_PER_HOUR;
        return intoHour * 60 / TICKS_PER_HOUR;
    }

    /** Whole in-game days elapsed since the world began. */
    public static long dayCount(long worldTime) {
        return Math.floorDiv(worldTime, TICKS_PER_DAY);
    }

    /**
     * Ticks from {@code worldTime} until the next occurrence of an in-game
     * time of day. Returns a full day when the target is the current tick,
     * so a repeating alarm does not fire twice for the same moment.
     */
    public static int ticksUntilTimeOfDay(long worldTime, int hour, int minute) {
        int target = Math.floorMod(hour * TICKS_PER_HOUR + minute * TICKS_PER_HOUR / 60 - MIDNIGHT_OFFSET,
                TICKS_PER_DAY);
        int delta = target - ticksIntoDay(worldTime);
        return delta > 0 ? delta : delta + TICKS_PER_DAY;
    }

    /** Real seconds a span of in-game ticks takes at the given tick rate. */
    public static double ticksToRealSeconds(long ticks, float ticksPerSecond) {
        return ticks / (double) ticksPerSecond;
    }

    /** In-game ticks that elapse over a span of real seconds. */
    public static long realSecondsToTicks(double seconds, float ticksPerSecond) {
        return Math.round(seconds * ticksPerSecond);
    }
}
