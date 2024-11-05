package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.ReachChangeUUIDs;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ProphesizeDemise extends SimpleAbilityItem {

    public ProphesizeDemise(Properties properties) {
        super(properties, BeyonderClassInit.SPECTATOR, 1, 1000, 0, 400, 400);
    }

    @Override
    public InteractionResult useAbilityOnEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        int dir = (int) player.getAttribute(ModAttributes.DIR.get()).getValue();
        addCooldown(player, stack.getItem(), 3000 / dir);
        useSpirituality(player);
        prophesizeDemise(interactionTarget);
        return InteractionResult.SUCCESS;
    }

    private final Lazy<Multimap<Attribute, AttributeModifier>> lazyAttributeMap = Lazy.of(this::createAttributeMap);

    @SuppressWarnings("deprecation")
    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            return this.lazyAttributeMap.get();
        }
        return super.getDefaultAttributeModifiers(slot);
    }

    private Multimap<Attribute, AttributeModifier> createAttributeMap() {

        ImmutableMultimap.Builder<Attribute, AttributeModifier> attributeBuilder = ImmutableMultimap.builder();
        attributeBuilder.putAll(super.getDefaultAttributeModifiers(EquipmentSlot.MAINHAND));
        attributeBuilder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_ENTITY_REACH, "Reach modifier", 400, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with entities
        attributeBuilder.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_BLOCK_REACH, "Reach modifier", 400, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with blocks, p much useless for this item
        return attributeBuilder.build();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use on a living entity, prophesizes their demise, making it so they have to stay still for 10 seconds out of 30 seconds, otherwise they die\n" +
                "Left Click for Prophesize Teleport Block" +
                "Spirituality Used: 70\n" +
                "Cooldown: 2 seconds").withStyle(ChatFormatting.AQUA));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

    public void prophesizeDemise(LivingEntity interactionTarget) {
        if (!interactionTarget.level().isClientSide()) {
            interactionTarget.addEffect(new MobEffectInstance(ModEffects.SPECTATORDEMISE.get(), 600, 1, false, false));
        }
    }

    @SubscribeEvent
    public static void handlePlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (!player.level().isClientSide()) {
            CompoundTag persistentData = player.getPersistentData();

            if (persistentData.contains("DemiseCounter")) {
                int demiseCounter = persistentData.getInt("DemiseCounter");

                if (!persistentData.contains("EntityDemise") || persistentData.getInt("EntityDemise") == 0) {
                    player.getPersistentData().putInt("EntityDemise", demiseCounter);
                }
            } else {
                if (!persistentData.contains("EntityDemise") || persistentData.getInt("EntityDemise") == 0) {

                    player.getPersistentData().putInt("EntityDemise", 0);
                }
            }
        }
    }

    @SubscribeEvent
    public static void handlePlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        if (!player.level().isClientSide()) {
            CompoundTag persistentData = player.getPersistentData();

            // Check if the persistent data contains the "DemiseCounter" key
            if (persistentData.contains("DemiseCounter")) {
                // Retrieve the demise counter value from persistent data
                int demiseCounter = persistentData.getInt("DemiseCounter");

                // Check if the "EntityDemise" key doesn't exist or its value is 0
                if (!persistentData.contains("EntityDemise") || persistentData.getInt("EntityDemise") == 0) {
                    // Update the demise counter for the player
                    player.getPersistentData().putInt("EntityDemise", demiseCounter);
                }
            } else {
                // If the persistent data doesn't contain the "DemiseCounter" key,
                // check if the "EntityDemise" key doesn't exist or its value is 0
                if (!persistentData.contains("EntityDemise") || persistentData.getInt("EntityDemise") == 0) {
                    // Initialize the demise counter to 0
                    player.getPersistentData().putInt("EntityDemise", 0);
                }
            }
        }
    }

}