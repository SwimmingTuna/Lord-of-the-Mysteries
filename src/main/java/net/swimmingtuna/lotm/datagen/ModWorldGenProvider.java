package net.swimmingtuna.lotm.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.worldgen.dimension.DimensionInit;

import javax.swing.tree.AbstractLayoutCache;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModWorldGenProvider extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.DIMENSION_TYPE, DimensionInit::bootstrapType);

    public ModWorldGenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries ) {
        super(output, registries, BUILDER, Set.of(LOTM.MOD_ID));
    }
}
