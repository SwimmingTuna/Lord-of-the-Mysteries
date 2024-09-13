package net.swimmingtuna.lotm.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {

    @Inject(method = "renderExperienceBar", at = @At(value = "HEAD"), cancellable = true)
    public void renderExperienceBar(GuiGraphics guiGraphics, int xPos, CallbackInfo ci) {
        if (
                ClientConfigs.SPIRITUALITY_BAR_ANCHOR.get() == SpiritualityBarOverlay.Anchor.XP &&
                Minecraft.getInstance().player != null &&
                SpiritualityBarOverlay.shouldShowSpiritualityBar(Minecraft.getInstance().player)
        ) {
            ci.cancel();
        }
    }
}
