package net.swimmingtuna.lotm.events;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems.MindReading;

import java.util.UUID;

public class ReachChangeEvent {

    public static void checkEntityTooFar(PlayerInteractEvent event, Entity entity, Player player, InteractionHand usedHand) {
        if (!MindReading.usedHand(player)) {
            return;
        }
        UUID uuidForOppositeHand = ReachChangeUUIDs.BEYONDER_ENTITY_REACH; //UUID which is able to be used in other files.
        AttributeInstance attackRange = player.getAttribute(ForgeMod.ENTITY_REACH.get()); //grabs reach attribute
        if (attackRange == null) {
            return;
        }
        //if player is able to hit or interact with something from range
        AttributeModifier beyonderModifier = attackRange.getModifier(ReachChangeUUIDs.BEYONDER_ENTITY_REACH); // calls BeyonderEntityReach and sets it to be able to change attack range and calls it beyonderModifier when its used for that
        if (beyonderModifier == null) {
            return;
        }
        //if beyonderModifier is active
        attackRange.removeModifier(beyonderModifier.getId()); //tbh idk
        double range = player.getAttributeValue(ForgeMod.ENTITY_REACH.get());
        double trueReach = range == 0 ? 0 : range + (player.isCreative() ? 3 : 0);
        attackRange.addTransientModifier(beyonderModifier);
    }
}
