package net.swimmingtuna.lotm.client;

import com.mojang.blaze3d.vertex.PoseStack;
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
        int startX = screenWidth - maxWidth - PADDING * 2 + 15;
        int startY = PADDING - 5;
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.scale(0.7f, 0.7f, 1f);
        int scaledStartX = (int) (startX / 0.7f);
        int scaledStartY = (int) (startY);
        int backgroundWidth = (int)Math.ceil((maxWidth + PADDING));
        int backgroundHeight = (int)Math.ceil((listHeight + PADDING) / 1.1f);

        guiGraphics.fill(scaledStartX - PADDING/2, scaledStartY - PADDING/2, scaledStartX + backgroundWidth, scaledStartY + backgroundHeight, BACKGROUND_COLOR);

        int y = scaledStartY;
        for (Map.Entry<String, String> entry : displayAbilities) {
            String displayText = entry.getKey() + " : " + entry.getValue();
            guiGraphics.drawString(gui.getFont(), displayText, scaledStartX, y, TEXT_COLOR, false);
            y += LINE_HEIGHT;
        }

        poseStack.popPose(); //start higher and more to the right
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