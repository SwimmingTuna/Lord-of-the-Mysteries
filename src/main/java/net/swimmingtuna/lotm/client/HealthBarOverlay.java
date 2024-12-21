package net.swimmingtuna.lotm.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.swimmingtuna.lotm.LOTM;

public class HealthBarOverlay implements IGuiOverlay {
    private static final Minecraft mc = Minecraft.getInstance();
    public static final HealthBarOverlay INSTANCE = new HealthBarOverlay();

    public static final ResourceLocation FULL = new ResourceLocation(LOTM.MOD_ID, "textures/gui/healthbarfull.png");
    public static final ResourceLocation ABSORPTION = new ResourceLocation(LOTM.MOD_ID, "textures/gui/healthbarabsorption.png");
    public static final ResourceLocation EMPTYTEXTURE = new ResourceLocation(LOTM.MOD_ID, "textures/gui/healthbarempty.png");
    public static final ResourceLocation POISON = new ResourceLocation(LOTM.MOD_ID, "textures/gui/healthbarpoison.png");
    public static final ResourceLocation FROZEN = new ResourceLocation(LOTM.MOD_ID, "textures/gui/healthbarwither.png");
    public static final ResourceLocation WITHER = new ResourceLocation(LOTM.MOD_ID, "textures/gui/healthbarfrozen.png");

    static final int IMAGE_WIDTH = 80;
    static final int IMAGE_HEIGHT = 9;
    static final int TEXT_COLOR = 0xFFFFFF;

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int width, int height) {
        Player player = mc.player;
        if (!shouldShowHealthBar(player)) {
            return;
        }
        ResourceLocation currentBar = determineBarTexture(player);
        int x = width / 2 - IMAGE_WIDTH - 12;
        int y = height - 39;
        RenderSystem.setShaderTexture(0, EMPTYTEXTURE);
        guiGraphics.blit(EMPTYTEXTURE, x, y, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, IMAGE_WIDTH, IMAGE_HEIGHT);
        float healthPercentage = player.getHealth() / player.getMaxHealth();
        int filledWidth = (int) (IMAGE_WIDTH * healthPercentage);
        RenderSystem.setShaderTexture(0, currentBar);
        guiGraphics.blit(currentBar, x, y, 0, 0, filledWidth, IMAGE_HEIGHT, IMAGE_WIDTH, IMAGE_HEIGHT);
        String healthText = String.format("%.1f/%.1f", player.getHealth(), player.getMaxHealth());
        PoseStack stack = guiGraphics.pose();
        stack.pushPose();
        stack.translate(x + IMAGE_WIDTH / 2, y - 5, 0);
        stack.scale(0.5f, 0.5f, 1);
        guiGraphics.drawCenteredString(mc.font, healthText, 1, 15, TEXT_COLOR);
        stack.popPose();
    }

    private ResourceLocation determineBarTexture(Player player) {
        if (player.hasEffect(MobEffects.WITHER)) {
            return WITHER;
        } else if (player.hasEffect(MobEffects.POISON)) {
            return POISON;
        } else if (player.isFullyFrozen()) {
            return FROZEN;
        } else if (player.getAbsorptionAmount() > 0) {
            return ABSORPTION;
        }
        return FULL;
    }

    private static boolean shouldShowHealthBar(Player player) {
        return player != null && !player.isSpectator() && !player.isCreative();
    }
}