package net.swimmingtuna.lotm.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeyBinding {
        public static final String KEY_CATEGORY_TUTORIAL = "key.category.lotm.mystery";
        public static final String KEY_SPIRITUALITY_CHECK = "key.lotm.spirituality_check";

        public static final KeyMapping SPIRITUALITY_KEY = new KeyMapping(KEY_SPIRITUALITY_CHECK, KeyConflictContext.IN_GAME,
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_O, KEY_CATEGORY_TUTORIAL); //defines what the key is and what key is used for something to happen which is defined in above files
    }

