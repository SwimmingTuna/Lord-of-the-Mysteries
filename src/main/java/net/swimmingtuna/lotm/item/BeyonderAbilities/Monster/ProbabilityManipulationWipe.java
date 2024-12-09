package net.swimmingtuna.lotm.item.BeyonderAbilities.Monster;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.util.ReachChangeUUIDs;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ProbabilityManipulationWipe extends SimpleAbilityItem {

    private final Lazy<Multimap<Attribute, AttributeModifier>> lazyAttributeMap = Lazy.of(this::createAttributeMap);

    public ProbabilityManipulationWipe(Properties properties) {
        super(properties, BeyonderClassInit.MONSTER, 0, 500, 100, 777, 777);
    }


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
        attributeBuilder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_ENTITY_REACH, "Reach modifier", 777, AttributeModifier.Operation.ADDITION)); // adds a 12 block reach for interacting with entities
        attributeBuilder.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_BLOCK_REACH, "Reach modifier", 777, AttributeModifier.Operation.ADDITION)); // adds a 12 block reach for interacting with blocks, pretty much useless for this item
        return attributeBuilder.build();
    }


    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Shoots out a lightning bolt in the direction you look"));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("200 for blocks/entities, 120 when using on air").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("~1 second").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(SimpleAbilityItem.getPathwayText(BeyonderClassInit.SAILOR.get()));
        tooltipComponents.add(SimpleAbilityItem.getClassText(5, BeyonderClassInit.SAILOR.get()));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) { //add if cursor is on a projectile, lightning goes to projectile and pwoers it
        if (!checkAll(player, BeyonderClassInit.MONSTER.get(), 0, 1000)) {
            return InteractionResult.FAIL;
        }
        probabilityWipeWorld(player);
        addCooldown(player);
        useSpirituality(player, 1000);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useAbilityOnEntity(ItemStack pStack, Player player, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        probabilityWipeEntity(pInteractionTarget);
        addCooldown(player);
        useSpirituality(player);
        return InteractionResult.SUCCESS;
    }

    public static void probabilityWipeEntity(LivingEntity pInteractionTarget) {
        if (!pInteractionTarget.level().isClientSide()) {
            wipeProbablility(pInteractionTarget.getPersistentData());
        }
    }

    public static void probabilityWipeWorld(Player player) {
        Level level = player.level();
        if (!level.isClientSide()) {
            for (Player pPlayer : level.players()) {
                for (LivingEntity livingEntity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(200))) {
                    CompoundTag tag = livingEntity.getPersistentData();
                    if (player.isShiftKeyDown()) {
                        if (livingEntity != player) {
                            wipeProbablility(tag);
                        }
                    } else {
                        wipeProbablility(tag);
                    }
                }
            }
        }
    }

    public static void wipeProbablility(CompoundTag tag) {
        tag.putInt("luckMeteor", 0);
        tag.putInt("luckLightningLOTM", 0);
        tag.putInt("luckParalysis", 0);
        tag.putInt("luckUnequipArmor", 0);
        tag.putInt("luckWarden", 0);
        tag.putInt("luckLightningMC", 0);
        tag.putInt("luckPoison", 0);
        tag.putInt("luckTornado", 0);
        tag.putInt("luckStone", 0);
        tag.putInt("luckDoubleDamage", 0);
        tag.putInt("cantUseAbility", 0);
        tag.putInt("calamityMeteor", 0);
        tag.putInt("calamityLightningStorm", 0);
        tag.putInt("calamityLightningBolt", 0);
        tag.putInt("calamityGroundTremor", 0);
        tag.putInt("calamityGaze", 0);
        tag.putInt("calamityUndeadArmy", 0);
        tag.putInt("calamityBabyZombie", 0);
        tag.putInt("calamityWindArmorRemoval", 0);
        tag.putInt("calamityBreeze", 0);
        tag.putInt("calamityWave", 0);
        tag.putInt("calamityExplosion", 0);
        tag.putInt("calamityTornado", 0);
        tag.putInt("luckIgnoreDamage", 0);
        tag.putInt("luckDiamonds", 0);
        tag.putInt("luckRegeneration", 0);
        tag.putInt("windMovingProjectilesCounter", 0);
        tag.putInt("luckHalveDamage", 0);
        tag.putInt("luckIgnoreMobs", 0);
        tag.putInt("luckAttackerPoisoned", 0);
    }


}
