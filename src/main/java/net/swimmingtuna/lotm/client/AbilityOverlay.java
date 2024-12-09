package net.swimmingtuna.lotm.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.swimmingtuna.lotm.util.ClientAbilitiesData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AbilityOverlay implements IGuiOverlay {
    public static final AbilityOverlay INSTANCE = new AbilityOverlay();
    private static final int PADDING = 10;  // Padding from screen edges
    private static final int LINE_HEIGHT = 10;  // Height of each line of text
    private static final int TEXT_COLOR = 0xFFFFFF;  // White text color
    private static final int BACKGROUND_COLOR = 0x80000000;  // Semi-transparent black background
    private static final int MAX_DISPLAY_ABILITIES = 5;  // Maximum abilities to display at once
    private static final int CYCLE_INTERVAL = 5000;  // 5 seconds in milliseconds

    private long lastCycleTime = 0;
    private int currentStartIndex = 0;

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (player == null) return;
        Map<String, String> abilities = ClientAbilitiesData.getAbilities();
        if (abilities.isEmpty()) return;
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCycleTime >= CYCLE_INTERVAL) {
            cycleThroughAbilities(abilities.size());
            lastCycleTime = currentTime;
        }
        List<Map.Entry<String, String>> displayAbilities = getDisplayAbilities(abilities);
        int maxWidth = calculateMaxWidth(gui, displayAbilities);
        int listHeight = displayAbilities.size() * LINE_HEIGHT;
        int startX = screenWidth - maxWidth - PADDING * 2;
        int startY = PADDING;
        guiGraphics.fill(
                startX - PADDING,
                startY - PADDING,
                startX + maxWidth + PADDING,
                startY + listHeight + PADDING,
                BACKGROUND_COLOR
        );

        // Render abilities
        int y = startY;
        for (Map.Entry<String, String> entry : displayAbilities) {
            String displayText = entry.getKey() + " : " + entry.getValue();
            guiGraphics.drawString(
                    gui.getFont(),
                    displayText,
                    startX,
                    y,
                    TEXT_COLOR,
                    false
            );
            y += LINE_HEIGHT;
        }
    }

    private void cycleThroughAbilities(int totalAbilities) {
        currentStartIndex = (currentStartIndex + MAX_DISPLAY_ABILITIES) % totalAbilities;
    }

    private List<Map.Entry<String, String>> getDisplayAbilities(Map<String, String> abilities) {
        List<Map.Entry<String, String>> abilityList = new ArrayList<>(abilities.entrySet());
        int totalAbilities = abilityList.size();
        if (totalAbilities <= MAX_DISPLAY_ABILITIES) {
            return abilityList;
        }
        List<Map.Entry<String, String>> displayAbilities = new ArrayList<>();
        for (int i = 0; i < MAX_DISPLAY_ABILITIES; i++) {
            int index = (currentStartIndex + i) % totalAbilities;
            displayAbilities.add(abilityList.get(index));
        }

        return displayAbilities;
    }

    private int calculateMaxWidth(ForgeGui gui, List<Map.Entry<String, String>> abilities) {
        int maxWidth = 0;
        for (Map.Entry<String, String> entry : abilities) {
            String displayText = entry.getKey() + " : " + entry.getValue();
            int width = gui.getFont().width(displayText);
            maxWidth = Math.max(maxWidth, width);
        }
        return maxWidth;
    }
}