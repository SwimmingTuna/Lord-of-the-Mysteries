package net.swimmingtuna.lotm.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.util.HashMap;
import java.util.Map;

public class AbilityOverlay implements IGuiOverlay {
    public static final AbilityOverlay INSTANCE = new AbilityOverlay();
    private static final int START_X = 10;  // X position for the overlay
    private static final int START_Y = 10;  // Y position for the overlay
    private static final int LINE_HEIGHT = 10;  // Height between each line
    private static final int TEXT_COLOR = 0xFFFFFF; // White color for text

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (player == null) return;

        Map<String, String> abilities = getRegisteredAbilities(player);
        int y = START_Y;

        for (Map.Entry<String, String> entry : abilities.entrySet()) {
            String combination = entry.getKey();
            String abilityName = entry.getValue();
            String displayText = combination + ": " + abilityName;

            // Draw each ability's text at the specified position
            guiGraphics.drawString(gui.getFont(), displayText, START_X, y, TEXT_COLOR, false);
            y += LINE_HEIGHT;
        }
    }

    private static Map<String, String> getRegisteredAbilities(LocalPlayer player) {
        CompoundTag tag = player.getPersistentData().getCompound("RegisteredAbilities");
        Map<String, String> abilitiesMap = new HashMap<>();

        for (String key : tag.getAllKeys()) {
            String ability = tag.getString(key);
            abilitiesMap.put(key, ability);
        }

        return abilitiesMap;
    }
}
