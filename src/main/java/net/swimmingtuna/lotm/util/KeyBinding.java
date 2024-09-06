package net.swimmingtuna.lotm.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeyBinding {
        public static final String KEY_CATEGORY_LOTM = "key.category.lotm.mystery";
        public static final String ABILITY_KEY = "key.lotm.ability_check";

        public static final KeyMapping SPIRIT_VISION = new KeyMapping(ABILITY_KEY, KeyConflictContext.IN_GAME,
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_C, KEY_CATEGORY_LOTM);
    }

