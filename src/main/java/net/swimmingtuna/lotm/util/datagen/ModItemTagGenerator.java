package net.swimmingtuna.lotm.util.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.swimmingtuna.lotm.LOTM;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagGenerator extends ItemTagsProvider {


    public ModItemTagGenerator(PackOutput pOutPut, CompletableFuture<HolderLookup.Provider> pFuture, CompletableFuture<TagLookup<Block>> pFuture2, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutPut, pFuture, pFuture2, LOTM.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

    }
}
