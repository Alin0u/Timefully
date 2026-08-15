package dev.alinou.timefully.hud;

import dev.alinou.timefully.time.DayPhase;

/** Built-in background colours the "Standard" background mode cycles through. */
final class StandardColors {

    static final int DAWN = 0x7A5A62;
    static final int DAY = 0x5A7A86;
    static final int DUSK = 0x6A4A5E;
    static final int NIGHT = 0x262C46;
    static final int RAIN = 0x4A5060;
    static final int THUNDER = 0x2A2D38;

    private StandardColors() {
    }

    static int forPhase(DayPhase phase) {
        return switch (phase) {
            case DAWN -> DAWN;
            case DAY -> DAY;
            case DUSK -> DUSK;
            case NIGHT -> NIGHT;
        };
    }
}
