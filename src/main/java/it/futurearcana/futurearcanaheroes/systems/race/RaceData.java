package it.futurearcana.futurearcanaheroes.systems.race;

import java.util.Objects;

public final class RaceData {
    private final RaceType type;
    private final String displayName;

    public RaceData(RaceType type, String displayName) {
        this.type = Objects.requireNonNull(type);
        this.displayName = Objects.requireNonNull(displayName);
    }

    public RaceType getType() {
        return type;
    }

    public String getDisplayName() {
        return displayName;
    }
}
