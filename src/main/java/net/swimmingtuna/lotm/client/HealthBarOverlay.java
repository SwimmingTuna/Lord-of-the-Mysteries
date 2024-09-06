package net.swimmingtuna.lotm.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;

public class HealthBarOverlay implements IGuiOverlay {
    public static final HealthBarOverlay INSTANCE = new HealthBarOverlay();
    public static final ResourceLocation TEXTURE = new ResourceLocation(LOTM.MOD_ID, "textures/spirituality/healthicons.png");

    static final int IMAGE_WIDTH = 83;
    static final int IMAGE_HEIGHT = 14;
    static final int TEXT_COLOR = 0xFFFFFF;

    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        var player = Minecraft.getInstance().player;

        if (!shouldShowHealthBar(player)) return;
        double maxHealth = player.getMaxHealth();
        double health = player.getHealth();

        int barX = screenWidth / 2 - 91;
        int barY = screenHeight - 46;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, TEXTURE);

        guiGraphics.blit(TEXTURE, barX, barY, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, 256, 256);

        int filledWidth = (int) (IMAGE_WIDTH * Math.min((health / maxHealth), 1));
        guiGraphics.blit(TEXTURE, barX, barY, 0, IMAGE_HEIGHT, filledWidth, IMAGE_HEIGHT, 256, 256);

        // Draw text showing player's health with scaling
        String healthText = Mth.floor(player.getHealth()) + "/" + Mth.floor(player.getMaxHealth());
        int textX = barX + (IMAGE_WIDTH / 2) - (gui.getFont().width(healthText) / 2);
        int textY = barY + 2; // Adjusted to center the text vertically

        guiGraphics.pose().pushPose(); // Save the current pose
        guiGraphics.pose().translate(textX + 2, textY + 6, 0); // Move to the text position
        guiGraphics.pose().scale(0.7f, 0.7f, 0.7f); // Scale down to 50%
        guiGraphics.drawString(gui.getFont(), healthText, 0, 0, TEXT_COLOR, true);
        guiGraphics.pose().popPose(); // Restore the previous pose
    }

    public static boolean shouldShowHealthBar(Player player) {
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        return holder != null && !player.isSpectator() && !player.isCreative();
    }
}