package net.swimmingtuna.lotm.world.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class MirrorWorldChunkGenerator extends ChunkGenerator {
    public static final Codec<MirrorWorldChunkGenerator> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(ChunkGenerator::getBiomeSource),
                    ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(generator -> generator.overworldKey)
            ).apply(instance, MirrorWorldChunkGenerator::new));

    private final ResourceKey<Level> overworldKey;
    private ServerLevel overworld;

    public MirrorWorldChunkGenerator(BiomeSource biomeSource, ResourceKey<Level> overworldKey) {
        super(biomeSource);
        this.overworldKey = overworldKey;
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    private ServerLevel getOverworld(WorldGenRegion region) {
        if (overworld == null) {
            MinecraftServer server = region.getServer();
            if (server != null) {
                overworld = server.getLevel(Level.OVERWORLD);
            }
        }
        return overworld;
    }

    @Override
    public void applyCarvers(WorldGenRegion level, long seed, RandomState random, BiomeManager biomeManager,
                             StructureManager structureManager, ChunkAccess chunk, GenerationStep.Carving step) {
        // Skip carving since we're copying the overworld
    }

    @Override
    public void buildSurface(WorldGenRegion region, StructureManager structures, RandomState randomState, ChunkAccess chunk) {
        ServerLevel overworldLevel = getOverworld(region);
        if (overworldLevel != null) {
            ChunkAccess overworldChunk = overworldLevel.getChunk(chunk.getPos().x, chunk.getPos().z);
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = overworldLevel.getMinBuildHeight(); y < overworldLevel.getMaxBuildHeight(); y++) {
                        pos.set(x, y, z);
                        BlockState state = overworldChunk.getBlockState(pos);
                        chunk.setBlockState(pos, state, false);
                    }
                }
            }
        }
    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion region) {
        // Skip mob spawning during generation
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender blender, RandomState random,
                                                        StructureManager structures, ChunkAccess chunk) {
        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public int getGenDepth() {
        return overworld != null ? overworld.getHeight() : 384;
    }

    @Override
    public int getMinY() {
        return overworld != null ? overworld.getMinBuildHeight() : -64;
    }

    @Override
    public int getSeaLevel() {
        return overworld != null ? overworld.getSeaLevel() : 63;
    }

    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor height, RandomState random) {
        if (overworld != null) {
            return new NoiseColumn(height.getMinBuildHeight(),
                    new BlockState[height.getHeight()]); // Empty column, will be filled during buildSurface
        }
        return new NoiseColumn(height.getMinBuildHeight(), new BlockState[0]);
    }

    @Override
    public void addDebugScreenInfo(List<String> list, RandomState randomState, BlockPos blockPos) {
        list.add("Mirror Dimension: Mirroring Overworld");
        if (overworld != null) {
            list.add("Original Height: " + overworld.getHeight());
            list.add("Original Min Y: " + overworld.getMinBuildHeight());
            list.add("Sea Level: " + overworld.getSeaLevel());
        } else {
            list.add("Status: Overworld reference not initialized");
        }
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types types, LevelHeightAccessor levelHeightAccessor,
                             RandomState randomState) {
        if (overworld != null) {
            return overworld.getHeight(types, x, z);
        }
        return levelHeightAccessor.getMinBuildHeight();
    }

    @Override
    public void createStructures(RegistryAccess registryAccess, ChunkGeneratorStructureState chunkGeneratorStructureState,
                                 StructureManager structureManager, ChunkAccess chunk, StructureTemplateManager structureTemplateManager) {
        if (overworld != null) {
            // Mirror structures from the overworld
            ChunkAccess overworldChunk = overworld.getChunk(chunk.getPos().x, chunk.getPos().z);
            chunk.setAllStarts(overworldChunk.getAllStarts());
            chunk.setAllReferences(overworldChunk.getAllReferences());
        }
    }

    @Override
    public void createReferences(WorldGenLevel pLevel, StructureManager pStructureManager, ChunkAccess pChunk) {
        if (overworld != null) {
            ChunkAccess overworldChunk = overworld.getChunk(pChunk.getPos().x, pChunk.getPos().z);
            pChunk.setAllReferences(overworldChunk.getAllReferences());

        }
    }
}