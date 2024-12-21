package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
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
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.Lazy;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.LightningEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.util.ReachChangeUUIDs;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class SailorLightning extends SimpleAbilityItem {

    private final Lazy<Multimap<Attribute, AttributeModifier>> lazyAttributeMap = Lazy.of(this::createAttributeMap);

    public SailorLightning(Properties properties) {
        super(properties, BeyonderClassInit.SAILOR, 5, 0, 30,150,150);
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
        super.baseHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) { //add if cursor is on a projectile, lightning goes to projectile and pwoers it
        if (!checkAll(player, BeyonderClassInit.SAILOR.get(), 5, 120)) {
            return InteractionResult.FAIL;
        }
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        lightningDirection(player, level);
        addCooldown(player, this, 10 + holder.getCurrentSequence());
        useSpirituality(player, 200);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useAbilityOnEntity(ItemStack pStack, Player player, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {
        if (!checkAll(player, BeyonderClassInit.SAILOR.get(), 5, 200)) {
            return InteractionResult.FAIL;
        }
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        addCooldown(player, this, 10 + (holder.getCurrentSequence() * 2));
        useSpirituality(player, 200);
        lightningTargetEntity(pInteractionTarget, player);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useAbilityOnBlock(UseOnContext context) {
        Player player = context.getPlayer();
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        if (!checkAll(player, BeyonderClassInit.SAILOR.get(), 5, 200)) {
            return InteractionResult.FAIL;
        }
        lightningblock(player, player.level(), context.getClickLocation());
        addCooldown(player, this, 10 + holder.getCurrentSequence() * 2);
        return InteractionResult.SUCCESS;
    }


    private static void lightningDirection(Player player, Level level) {
        if (!level.isClientSide()) {
            Vec3 lookVec = player.getLookAngle();
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            holder.useSpirituality(100);
            float speed = 15.0f;
            LightningEntity lightningEntity = new LightningEntity(EntityInit.LIGHTNING_ENTITY.get(), level);
            lightningEntity.setSpeed(speed);
            lightningEntity.setDeltaMovement(lookVec.x, lookVec.y, lookVec.z);
            lightningEntity.setMaxLength(30);
            lightningEntity.setOwner(player);
            lightningEntity.teleportTo(player.getX(), player.getEyeY(), player.getZ());
            level.addFreshEntity(lightningEntity);
        }
    }

    private static void lightningblock(Player player, Level level, Vec3 targetPos) {
        if (!level.isClientSide()) {
            Vec3 lookVec = player.getLookAngle();
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            holder.useSpirituality(200);
            float speed = 15.0f;
            if (!player.isCreative()) {
                ItemStack itemStack = player.getUseItem();
                player.getCooldowns().addCooldown(itemStack.getItem(), 10 + (holder.getCurrentSequence() * 2));
            }
            LightningEntity lightningEntity = new LightningEntity(EntityInit.LIGHTNING_ENTITY.get(), level);
            lightningEntity.setSpeed(speed);
            lightningEntity.setDeltaMovement(lookVec.x, lookVec.y, lookVec.z);
            lightningEntity.setMaxLength(30);
            lightningEntity.setOwner(player);
            lightningEntity.teleportTo(player.getX(), player.getEyeY(), player.getZ());
            lightningEntity.setTargetPos(targetPos);
            level.addFreshEntity(lightningEntity);
        }
    }

    public static void lightningHigh(Player player, Level level) {
        if (!level.isClientSide()) {
            float speed = 10.0f;
            LightningEntity lightningEntity = new LightningEntity(EntityInit.LIGHTNING_ENTITY.get(), level);
            lightningEntity.setSpeed(speed);
            lightningEntity.setDeltaMovement(0, -2, 0);
            lightningEntity.setMaxLength(60);
            lightningEntity.setOwner(player);
            lightningEntity.setOwner(player);
            lightningEntity.teleportTo(player.getX() + ((Math.random() * 150) - 75), player.getY() + 60, player.getZ() + ((Math.random() * 150) - 75));
            level.addFreshEntity(lightningEntity);
        }
    }

    public static void lightningHighPlayerMob(LivingEntity player, Level level) {
        if (!level.isClientSide()) {
            float speed = 10.0f;
            LightningEntity lightningEntity = new LightningEntity(EntityInit.LIGHTNING_ENTITY.get(), level);
            lightningEntity.setSpeed(speed);
            lightningEntity.setDeltaMovement(0, -2, 0);
            lightningEntity.setMaxLength(60);
            lightningEntity.setOwner(player);
            lightningEntity.setOwner(player);
            lightningEntity.teleportTo(player.getX() + ((Math.random() * 150) - 75), player.getY() + 60, player.getZ() + ((Math.random() * 150) - 75));
            level.addFreshEntity(lightningEntity);
        }
    }

    public static void lightningTargetEntity(LivingEntity targetEntity, Player player) {
        if (!player.level().isClientSide()) {
            LightningEntity lightningEntity = new LightningEntity(EntityInit.LIGHTNING_ENTITY.get(), player.level());
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            lightningEntity.setSpeed(15.0f);
            holder.useSpirituality(100);
            Vec3 lookVec = player.getLookAngle();
            lightningEntity.setDeltaMovement(lookVec.x, lookVec.y, lookVec.z);
            lightningEntity.setMaxLength(30);
            lightningEntity.teleportTo(player.getX(), player.getY(), player.getZ());
            lightningEntity.setTargetPos(targetEntity.position());
            lightningEntity.setOwner(player);
            lightningEntity.setOwner(player);
            player.level().addFreshEntity(lightningEntity);
        }
    }
}
