package net.swimmingtuna.lotm.events;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import javax.swing.text.html.parser.Entity;
import java.util.UUID;

import static net.swimmingtuna.lotm.events.ReachChangeUUIDs.BEYONDER_ENTITY_REACH;
import static net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems.MindReading.pUsedHand;

public class ReachChangeEvent {

    public static void checkEntityTooFar(PlayerInteractEvent event, Entity pEntity, Player pPlayer, InteractionHand pUsedHand) {
        if(pUsedHand(pPlayer)) {
            UUID uuidforOppositeHand = BEYONDER_ENTITY_REACH; //UUID which is able to be used in other files.
            AttributeInstance attackRange = pPlayer.getAttribute(ForgeMod.ENTITY_REACH.get()); //grabs reach attribute
            if (attackRange != null) { //if player is able to hit or interact with something from range
                AttributeModifier beyonderModifier = attackRange.getModifier(BEYONDER_ENTITY_REACH); // calls BeyonderEntityReach and sets it to be able to change attack range and calls it beyonderModifier when its used for that
                if (beyonderModifier != null) { //if beyonderModifier is active
                    attackRange.removeModifier(beyonderModifier.getId()); //tbh idk
                    double range = pPlayer.getAttributeValue(ForgeMod.ENTITY_REACH.get());
                    double trueReach = range == 0 ? 0 : range + (pPlayer.isCreative() ? 3 : 0);
                    attackRange.addTransientModifier(beyonderModifier);
                }
            }
        }
    }
}
