package net.swimmingtuna.lotm.spirituality;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AttributeAdder {
    @SubscribeEvent
    public static void modifyAttributes(EntityAttributeModificationEvent event) {
        addToPlayer(event, ModAttributes.SOUL_BODY);
        addToPlayer(event, ModAttributes.MISFORTUNE);
        addToPlayer(event, ModAttributes.NIGHTMARE);
        addToPlayer(event, ModAttributes.ARMORINVISIBLITY);
        addToPlayer(event, ModAttributes.DIR);
        addToPlayer(event, ModAttributes.CORRUPTION);
        addToPlayer(event, ModAttributes.LOTM_LUCK);
        addToPlayer(event, ModAttributes.SANITY);
    }

    @SafeVarargs
    private static void addToPlayer(EntityAttributeModificationEvent event, Supplier<Attribute>... attributes) {
        for (Supplier<Attribute> attribute : attributes) {
            event.add(EntityType.PLAYER, attribute.get());
        }
    }
}
