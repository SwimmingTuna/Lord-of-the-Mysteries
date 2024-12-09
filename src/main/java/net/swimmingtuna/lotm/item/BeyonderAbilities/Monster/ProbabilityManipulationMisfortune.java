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

public class ProbabilityManipulationMisfortune extends SimpleAbilityItem {

    private final Lazy<Multimap<Attribute, AttributeModifier>> lazyAttributeMap = Lazy.of(this::createAttributeMap);

    public ProbabilityManipulationMisfortune(Properties properties) {
        super(properties, BeyonderClassInit.MONSTER, 5, 0, 30, 150, 150);
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
        attributeBuilder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_ENTITY_REACH, "Reach modifier", 150, AttributeModifier.Operation.ADDITION)); // adds a 12 block reach for interacting with entities
        attributeBuilder.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_BLOCK_REACH, "Reach modifier", 150, AttributeModifier.Operation.ADDITION)); // adds a 12 block reach for interacting with blocks, pretty much useless for this item
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
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        probabilityWipeWorld(player);
        addCooldown(player, this, 10 + holder.getCurrentSequence());
        useSpirituality(player, 200);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useAbilityOnEntity(ItemStack pStack, Player player, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        probabilityWipeEntity(pInteractionTarget);
        addCooldown(player, this, 10 + holder.getCurrentSequence());
        useSpirituality(player, 200);
        return InteractionResult.SUCCESS;
    }

    public static void probabilityWipeEntity(LivingEntity pInteractionTarget) {
        if (!pInteractionTarget.level().isClientSide()) {
            giveMisfortuneEvents(pInteractionTarget);
        }
    }

    public static void probabilityWipeWorld(Player player) {
        Level level = player.level();
        if (!level.isClientSide()) {
            for (Player pPlayer : level.players()) {
                for (LivingEntity livingEntity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(200))) {
                    if (player.isShiftKeyDown()) {
                        if (livingEntity != player) {
                            giveMisfortuneEvents(livingEntity);
                        }
                    } else {
                        giveMisfortuneEvents(livingEntity);
                    }
                }
            }
        }
    }

    public static void giveMisfortuneEvents(LivingEntity livingEntity) {
        CompoundTag tag = livingEntity.getPersistentData();

        int luckMeteor = tag.getInt("luckMeteor");
        int luckLightningLOTM = tag.getInt("luckLightningLOTM");
        int luckParalysis = tag.getInt("luckParalysis");
        int luckUnequipArmor = tag.getInt("luckUnequipArmor");
        int luckWarden = tag.getInt("luckWarden");
        int luckLightningMC = tag.getInt("luckLightningMC");
        int luckPoison = tag.getInt("luckPoison");
        int luckTornado = tag.getInt("luckTornado");
        int luckStone = tag.getInt("luckStone");
        int luckDoubleDamage = tag.getInt("luckDoubleDamage");
        int cantUseAbility = tag.getInt("cantUseAbility");
        int calamityLightningStorm = tag.getInt("calamityLightningStorm");
        int calamityGroundTremor = tag.getInt("calamityGroundTremor");
        int calamityGaze = tag.getInt("calamityGaze");
        int calamityUndeadArmy = tag.getInt("calamityUndeadArmy");
        int calamityBabyZombie = tag.getInt("calamityBabyZombie");
        int calamityBreeze = tag.getInt("calamityBreeze");
        int calamityWave = tag.getInt("calamityWave");
        int calamityExplosion = tag.getInt("calamityExplosion");


        if (cantUseAbility == 0) {
            tag.putInt("cantUseAbility", 1);
        }
        if (luckMeteor == 0) {
            tag.putInt("luckMeteor",40);
        }
        if (luckLightningLOTM == 0) {
            tag.putInt("luckLightningLOTM", 20);
        }
        if (luckParalysis == 0) {
            tag.putInt("luckParalysis", 15);
        }
        if (luckUnequipArmor == 0) {
            tag.putInt("luckUnequipArmor", 25);
        }
        if (luckWarden == 0) {
            tag.putInt("luckWarden", 30);
        }
        if (luckLightningMC == 0) {
            tag.putInt("luckLightningMC", 1);
        }
        if (luckPoison == 0) {
            tag.putInt("luckPoison", 1);
        }
        if (luckTornado == 0) {
            tag.putInt("luckTornado", 35);
        }
        if (luckStone == 0) {
            tag.putInt("luckStone", 10);
        }
        if (luckDoubleDamage == 0) {
            tag.putInt("luckDoubleDamage", 1);
        }
        if (calamityLightningStorm == 0) {
            tag.putInt("calamityLightningStorm", (int) Math.max(15, Math.random() * 50));
        }
        if (calamityGroundTremor == 0) {
            tag.putInt("calamityLightningStorm",(int) Math.max(10, Math.random() * 40) );
        }
        if (calamityGaze == 0) {
            tag.putInt("calamityLightningStorm",(int) Math.max(10, Math.random() * 50) );
        }
        if (calamityUndeadArmy == 0) {
            tag.putInt("calamityLightningStorm",(int) Math.max(5, Math.random() * 20) );
        }
        if (calamityBabyZombie == 0) {
            tag.putInt("calamityLightningStorm", (int) Math.max(5, Math.random() * 20) );
        }
        if (calamityBreeze == 0) {
            tag.putInt("calamityLightningStorm", (int) Math.max(10, Math.random() * 25) );
        }
        if (calamityWave == 0) {
            tag.putInt("calamityLightningStorm", (int) Math.max(10, Math.random() * 25) );
        }
        if (calamityExplosion == 0) {
            tag.putInt("calamityExplosion",  (int) Math.max(10, Math.random() * 60));
        }
    }


}
