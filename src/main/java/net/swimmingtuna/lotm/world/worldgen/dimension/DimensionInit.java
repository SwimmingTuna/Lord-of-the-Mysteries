package net.swimmingtuna.lotm.world.worldgen.dimension;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.swimmingtuna.lotm.LOTM;

import java.util.List;
import java.util.OptionalLong;

public class DimensionInit {
    public static final ResourceKey<LevelStem> SPIRIT_WORLD_KEY = ResourceKey.create(Registries.LEVEL_STEM,
            new ResourceLocation(LOTM.MOD_ID, "spirit_world"));
    public static final ResourceKey<Level> SPIRIT_WORLD_LEVEL_KEY = ResourceKey.create(Registries.DIMENSION,
            new ResourceLocation(LOTM.MOD_ID, "spirit_world"));
    public static final ResourceKey<DimensionType> SPIRIT_WORLD_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE,
            new ResourceLocation(LOTM.MOD_ID, "spirit_world_type"));

    public static void bootstrapType(BootstapContext<DimensionType> context) {
        context.register(SPIRIT_WORLD_TYPE, new DimensionType(
                OptionalLong.empty(), // Don't fix time
                true,  // hasSkylight
                false, // hasCeiling
                false, // ultraWarm
                true,  // natural
                1.0,   // coordinateScale
                true,  // bedWorks
                true,  // respawnAnchorWorks
                -64,   // minY
                384,   // height
                384,   // logicalHeight
                BlockTags.INFINIBURN_OVERWORLD,
                BuiltinDimensionTypes.OVERWORLD_EFFECTS,
                0.0f,  // ambientLight
                new DimensionType.MonsterSettings(true, true, ConstantInt.of(0), 7)));
    }

    public static void bootstrapStem(BootstapContext<LevelStem> context) {
        HolderGetter<Biome> biomeRegistry = context.lookup(Registries.BIOME);
        HolderGetter<DimensionType> dimTypes = context.lookup(Registries.DIMENSION_TYPE);
        HolderGetter<NoiseGeneratorSettings> noiseGenSettings = context.lookup(Registries.NOISE_SETTINGS);

        NoiseBasedChunkGenerator wrappedChunkGenerator = new NoiseBasedChunkGenerator(
                new FixedBiomeSource(biomeRegistry.getOrThrow(Biomes.PLAINS)),
                noiseGenSettings.getOrThrow(NoiseGeneratorSettings.AMPLIFIED));

        NoiseBasedChunkGenerator noiseBasedChunkGenerator = new NoiseBasedChunkGenerator(
                MultiNoiseBiomeSource.createFromList(
                        new Climate.ParameterList<>(List.of(Pair.of(
                                        Climate.parameters(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), biomeRegistry.getOrThrow(Biomes.PLAINS)),
                                Pair.of(
                                        Climate.parameters(0.1F, 0.2F, 0.0F, 0.2F, 0.0F, 0.0F, 0.0F), biomeRegistry.getOrThrow(Biomes.BIRCH_FOREST)),
                                Pair.of(
                                        Climate.parameters(0.3F, 0.6F, 0.1F, 0.1F, 0.0F, 0.0F, 0.0F), biomeRegistry.getOrThrow(Biomes.OCEAN)),
                                Pair.of(
                                        Climate.parameters(0.4F, 0.3F, 0.2F, 0.1F, 0.0F, 0.0F, 0.0F), biomeRegistry.getOrThrow(Biomes.DARK_FOREST))

                        ))),
                noiseGenSettings.getOrThrow(NoiseGeneratorSettings.AMPLIFIED));

        LevelStem stem = new LevelStem(dimTypes.getOrThrow(DimensionInit.SPIRIT_WORLD_TYPE), noiseBasedChunkGenerator);

        context.register(SPIRIT_WORLD_KEY, stem);
    }
}