package it.futurearcana.futurearcanaheroes.systems.race;

import java.util.EnumMap;
import java.util.Map;

/** Simple registry/manager for race metadata. Systems should query this. */
public final class RaceManager {
    private static final Map<RaceType, RaceData> REGISTRY = new EnumMap<>(RaceType.class);

    private RaceManager() {}

    public static void register(RaceData data) {
        REGISTRY.put(data.getType(), data);
    }

    public static RaceData get(RaceType type) {
        return REGISTRY.get(type);
    }
}
