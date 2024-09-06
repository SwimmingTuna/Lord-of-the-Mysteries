package net.swimmingtuna.lotm.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
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

public class SpiritualityBarOverlay implements IGuiOverlay {
    public static final SpiritualityBarOverlay INSTANCE = new SpiritualityBarOverlay();
    public static final ResourceLocation TEXTURE = new ResourceLocation(LOTM.MOD_ID, "textures/spirituality/icons.png");
    static final int DEFAULT_IMAGE_WIDTH = 98;
    static final int XP_IMAGE_WIDTH = 188;
    static final int IMAGE_HEIGHT = 21;
    static final int HOTBAR_HEIGHT = 25;
    static final int ICON_ROW_HEIGHT = -5;
    static final int CHAR_WIDTH = 6;
    static final int HUNGER_BAR_OFFSET = 50;
    static final int SCREEN_BORDER_MARGIN = 20;
    static final int TEXT_COLOR = ChatFormatting.AQUA.getColor();

    public static boolean shouldShowSpiritualityBar(Player pPlayer) {
        Display display = ClientConfigs.SPIRITUALITY_BAR_DISPLAY.get();
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
        if (holder == null) return false;

        return !pPlayer.isSpectator() && display != Display.NEVER && (display == Display.ALWAYS || holder.getSpirituality() - 1 < holder.getMaxSpirituality());
    }

    private static int getBarX(Anchor anchor, int screenWidth) {
        return switch (anchor) {
            case XP -> screenWidth / 2 - 91 - 3; //Vanilla's Pos - 3
            case HUNGER, CENTER -> screenWidth / 2 - DEFAULT_IMAGE_WIDTH / 2 + (anchor == Anchor.CENTER ? 0 : HUNGER_BAR_OFFSET);
            case TOP_LEFT, BOTTOM_LEFT -> SCREEN_BORDER_MARGIN;
            default -> screenWidth - SCREEN_BORDER_MARGIN - DEFAULT_IMAGE_WIDTH;
        };
    }

    private static int getBarY(Anchor anchor, int screenHeight, ForgeGui gui) {
        return switch (anchor) {
            case XP -> screenHeight - 32 + 3 - 8; //Vanilla's Pos - 8
            case HUNGER -> screenHeight - (getAndIncrementRightHeight(gui) - 2) - IMAGE_HEIGHT / 2;
            case CENTER -> screenHeight - HOTBAR_HEIGHT - (int) (ICON_ROW_HEIGHT * 2.5f) - IMAGE_HEIGHT / 2;
            case TOP_LEFT, TOP_RIGHT -> SCREEN_BORDER_MARGIN;
            default -> screenHeight - SCREEN_BORDER_MARGIN - IMAGE_HEIGHT;
        };
    }

    private static int getAndIncrementRightHeight(ForgeGui gui) {
        int x = gui.rightHeight;
        gui.rightHeight += 10;
        return x;
    }

    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        var player = Minecraft.getInstance().player;

        if (!shouldShowSpiritualityBar(player)) return;
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        if (holder == null) return;
        double maxSpirituality = holder.getMaxSpirituality();
        double spirituality = holder.getSpirituality();
        int barX, barY;
        int configOffsetY = ClientConfigs.SPIRITUALITY_BAR_Y_OFFSET.get();
        int configOffsetX = ClientConfigs.SPIRITUALITY_BAR_X_OFFSET.get();
        Anchor anchor = ClientConfigs.SPIRITUALITY_BAR_ANCHOR.get();
        if (anchor == Anchor.XP && player.getJumpRidingScale() > 0) return;
        barX = getBarX(anchor, screenWidth) + configOffsetX;
        barY = getBarY(anchor, screenHeight, gui) + configOffsetY;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int imageWidth = anchor == Anchor.XP ? XP_IMAGE_WIDTH : DEFAULT_IMAGE_WIDTH;
        int spriteX = anchor == Anchor.XP ? 68 : 0;
        int spriteY = anchor == Anchor.XP ? 40 : 0;
        guiGraphics.blit(TEXTURE, barX, barY, spriteX, spriteY, imageWidth, IMAGE_HEIGHT, 256, 256);
        guiGraphics.blit(TEXTURE, barX, barY, spriteX, spriteY + IMAGE_HEIGHT, (int) (imageWidth * Math.min(spirituality / maxSpirituality, 1)), IMAGE_HEIGHT);

        int textX, textY;
        String spiritualityFraction = (Mth.floor(spirituality)) + "/" + Mth.floor(maxSpirituality);

        textX = barX + imageWidth / 2;
        textY = barY + (anchor == Anchor.XP ? ICON_ROW_HEIGHT / 3 : ICON_ROW_HEIGHT);

        if (ClientConfigs.SPIRITUALITY_BAR_TEXT_VISIBLE.get()) {
            guiGraphics.drawCenteredString(gui.getFont(), spiritualityFraction, textX, textY, TEXT_COLOR);
        }
    }

    public enum Anchor {
        HUNGER,
        XP,
        CENTER,
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }

    public enum Display {
        NEVER,
        ALWAYS,
        CONTEXTUAL
    }
}
