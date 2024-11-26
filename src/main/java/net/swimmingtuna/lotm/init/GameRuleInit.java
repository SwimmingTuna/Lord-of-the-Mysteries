package net.swimmingtuna.lotm.init;


import net.minecraft.world.level.GameRules;

public class GameRuleInit {

    public static final GameRules.Key<GameRules.BooleanValue> NPC_SHOULD_SPAWN = GameRules.register("npcShouldSpawn", GameRules.Category.MOBS, GameRules.BooleanValue.create(false));


}

