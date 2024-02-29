package net.swimmingtuna.lotm.events;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.item.custom.BeyonderAbilities.PsychologicalInvisibility;
import net.swimmingtuna.lotm.spirituality.ModAttributes;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID)
public class ModEvents {

    @SubscribeEvent
    public static void onPlayerJoinWorld(EntityJoinLevelEvent event) {
    }
    private static int nightmareTimer = 0;

    @SubscribeEvent
    public static void attributeHandler(TickEvent.PlayerTickEvent event) {
        Player pPlayer = event.player;
        AttributeInstance nightmareAttribute = pPlayer.getAttribute(ModAttributes.NIGHTMARE.get());

        if (!event.player.level().isClientSide && event.phase == TickEvent.Phase.START) {
            if (nightmareAttribute.getValue() >= 1) {
                nightmareTimer++;
                if (nightmareTimer >= 600) {
                    nightmareAttribute.setBaseValue(0);
                    nightmareTimer = 0;
                }
            } else {
                nightmareTimer = 0;
            }
        }
    }
}
