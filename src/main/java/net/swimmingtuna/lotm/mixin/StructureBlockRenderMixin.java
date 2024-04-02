package net.swimmingtuna.lotm.mixin;

import net.minecraft.client.renderer.blockentity.StructureBlockRenderer;
import net.swimmingtuna.lotm.LOTM;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = StructureBlockRenderer.class, priority = 999)
public class StructureBlockRenderMixin {

    @ModifyConstant(method = "getViewDistance", constant = @Constant(intValue = 96), require = 0)
    public int getrenderDistance(int value) {return LOTM.NEW_STRUCTURE_SIZE / 2;
    }
}
