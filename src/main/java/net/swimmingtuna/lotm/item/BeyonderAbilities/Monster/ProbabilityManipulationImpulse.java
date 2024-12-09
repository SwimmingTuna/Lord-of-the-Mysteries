package net.swimmingtuna.lotm.item.BeyonderAbilities.Monster;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
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
import net.swimmingtuna.lotm.world.worlddata.CalamityEnhancementData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ProbabilityManipulationImpulse extends SimpleAbilityItem {

    private final Lazy<Multimap<Attribute, AttributeModifier>> lazyAttributeMap = Lazy.of(this::createAttributeMap);

    public ProbabilityManipulationImpulse(Properties properties) {
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
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        probabilityImpulse(player);
        addCooldown(player);
        useSpirituality(player);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useAbilityOnEntity(ItemStack pStack, Player player, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        probabilityImpulseEntity(pInteractionTarget);
        addCooldown(player);
        useSpirituality(player);
        return InteractionResult.SUCCESS;
    }

    public static void probabilityImpulseEntity(LivingEntity pInteractionTarget) {
        if (!pInteractionTarget.level().isClientSide()) {
            finishProbability(pInteractionTarget);
        }
    }

    public static void probabilityImpulse(Player player) {
        Level level = player.level();
        if (!level.isClientSide()) {
            for (Player pPlayer : level.players()) {
                for (LivingEntity livingEntity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(200))) {
                    CompoundTag tag = livingEntity.getPersistentData();
                    if (player.isShiftKeyDown()) {
                        if (livingEntity != player) {
                            finishProbability(player);
                        }
                    } else {
                        finishProbability(player);
                    }
                }
            }
        }
    }

    public static void finishProbability(LivingEntity entity) {
        if (entity.level() instanceof ServerLevel serverLevel) {
            int calamityEnhancement = CalamityEnhancementData.getInstance(serverLevel).getCalamityEnhancement();
            CompoundTag tag = entity.getPersistentData();
            if (tag.getInt("luckMeteor") >= 1) {
                tag.putInt("luckMeteor", 1);
            }
            if (tag.getInt("luckLightningLOTM") >= 1) {
                tag.putInt("luckLightningLOTM", 1);
            }
            if (tag.getInt("luckParalysis") >= 1) {
                tag.putInt("luckParalysis", 1);
            }
            if (tag.getInt("luckUnequipArmor") >= 1) {
                tag.putInt("luckUnequipArmor", 1);
            }
            if (tag.getInt("luckWarden") >= 1) {
                tag.putInt("luckWarden", 1);
            }
            if (tag.getInt("luckLightningMC") >= 1) {
                LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, entity.level());
                lightningBolt.teleportTo(entity.getX(), entity.getY(), entity.getZ());
                lightningBolt.setDamage(10.0f + (calamityEnhancement * 3));
                entity.level().addFreshEntity(lightningBolt);
                tag.putInt("luckLightningMC", tag.getInt("luckLightningMC") - 1);
            }
            if (tag.getInt("luckPoison") >= 1) {
                tag.putInt("luckPoison", 1);
            }
            if (tag.getInt("luckTornado") >= 1) {
                tag.putInt("luckTornado", 1);
            }
            if (tag.getInt("luckStone") >= 1) {
                tag.putInt("luckStone", 1);
            }
            if (tag.getInt("calamityMeteor") >= 1) {
                tag.putInt("calamityMeteor", 1);
            }
            if (tag.getInt("calamityLightningStorm") >= 1) {
                tag.putInt("calamityLightningStorm", 1);
            }
            if (tag.getInt("calamityLightningBolt") >= 1) {
                tag.putInt("calamityLightningBolt", 1);
            }
            if (tag.getInt("calamityGroundTremor") >= 1) {
                tag.putInt("calamityGroundTremor", 1);
            }
            if (tag.getInt("calamityGaze") >= 1) {
                tag.putInt("calamityGaze", 1);
            }
            if (tag.getInt("calamityUndeadArmy") >= 1) {
                tag.putInt("calamityUndeadArmy", 1);
            }
            if (tag.getInt("calamityBabyZombie") >= 1) {
                tag.putInt("calamityBabyZombie", 1);
            }
            if (tag.getInt("calamityWindArmorRemoval") >= 1) {
                tag.putInt("calamityWindArmorRemoval", 1);
            }
            if (tag.getInt("calamityBreeze") >= 1) {
                tag.putInt("calamityBreeze", 1);
            }
            if (tag.getInt("calamityWave") >= 1) {
                tag.putInt("calamityWave", 1);
            }
            if (tag.getInt("calamityExplosion") >= 2) {
                tag.putInt("calamityExplosion", 2);
            }
            if (tag.getInt("calamityTornado") >= 1) {
                tag.putInt("calamityTornado", 1);
            }
        }
    }

}
