package net.swimmingtuna.lotm.init;


import net.minecraft.client.gui.screens.worldselection.EditGameRulesScreen;
import net.minecraft.world.level.GameRules;

public class GameRuleInit {
    public static final String ID = "disableNPCSpawns";
    public static final boolean DEFAULT_VALUE = false;
    public static final GameRules.Category CATEGORY = GameRules.Category.MOBS;

    public void register(GameRules gameRules) {

    }
}

