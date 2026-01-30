package ru.alextrof94.immersive_measurements;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue MIN_DISTANCE_BETWEEN_MAGNETIT = BUILDER
            .comment("Do what it do")
            .defineInRange("minDistance", 100, 10, 10000);

    public static final ModConfigSpec.IntValue RADAR_PAGE_SIZE = BUILDER
            .comment("Page size for radar. Yes.")
            .defineInRange("radarPageSize", 4, 1, 10);

    public static final ModConfigSpec.DoubleValue RADAR_MAP_SIZE = BUILDER
            .comment("Map size for radar. Yes.")
            .defineInRange("radarMapSize", 64f, 10, 600);

    public static final ModConfigSpec.BooleanValue DEBUG_POSITIONS = BUILDER
            .comment("Don't use it, you don't need it")
            .define("debugPositions", false);

    static final ModConfigSpec SPEC = BUILDER.build();

    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }
}
