package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.LightningEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Ability;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.util.ReachChangeUUIDs;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SailorLightning extends Item implements Ability {

    private final Lazy<Multimap<Attribute, AttributeModifier>> lazyAttributeMap = Lazy.of(this::createAttributeMap);

    public SailorLightning(Properties properties) { //IMPORTANT!!!! FIGURE OUT HOW TO MAKE THIS WORK BY CLICKING ON A
        super(properties);
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
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        Vec3 eyePosition = player.getEyePosition();
        Vec3 lookVector = player.getLookAngle();
        double reachDistance = 150;
        Vec3 endPoint = eyePosition.add(lookVector.scale(reachDistance));

        // Check for block interaction
        BlockHitResult blockHit = level.clip(new ClipContext(eyePosition, endPoint, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        if (blockHit.getType() != HitResult.Type.MISS) {
            return InteractionResult.PASS;
        }

        // Check for entity interaction
        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(level, player, eyePosition, endPoint, player.getBoundingBox().inflate(reachDistance), entity -> !entity.isSpectator() && entity.isPickable());
        if (entityHit != null) {
            return InteractionResult.PASS;
        }

        if (!SimpleAbilityItem.checkAll(player, BeyonderClassInit.SAILOR.get(), 5, 120)) return InteractionResult.FAIL;

        // If no block or entity is targeted, proceed with the original functionality
        shootLine(player, level);
        return InteractionResult.SUCCESS;
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

    private static void shootLine(Player player, Level level) {
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

    private static void shootLineBlock(Player player, Level level, Vec3 targetPos) {
        if (!level.isClientSide()) {
            Vec3 lookVec = player.getLookAngle();
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            holder.useSpirituality(200);
            float speed = 15.0f;
            if (!player.isCreative()) {
                ItemStack itemStack = player.getUseItem();
                // TODO: change to actual ability item, maybe make this method non static
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

    public static void shootLineBlockHigh(Player player, Level level) {
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
    public static void shootLineBlockHighPM(LivingEntity player, Level level) {
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


    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        Entity targetEntity = event.getTarget();
        ItemStack stack = player.getItemInHand(event.getHand());
        if (player.level().isClientSide()) return;
        if (stack.getItem() instanceof SailorLightning) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            if (!SimpleAbilityItem.checkAll(player, BeyonderClassInit.SAILOR.get(), 5, 120)) {
                return;
//                event.setCancellationResult(InteractionResult.FAIL);
//                event.setCanceled(true);
            }
            if (!player.getCooldowns().isOnCooldown(stack.getItem()) && holder.useSpirituality(120)) {
                float speed = 15.0f;
                if (targetEntity instanceof AbstractArrow || targetEntity instanceof AbstractHurtingProjectile || targetEntity instanceof Projectile || targetEntity instanceof Arrow) {
                    CompoundTag tag = targetEntity.getPersistentData();
                    if (!player.getAbilities().instabuild) {
                        player.getCooldowns().addCooldown(stack.getItem(), 5);
                    }
                    int x = tag.getInt("sailorLightningProjectileCounter");
                    tag.putInt("sailorLightningProjectileCounter", x + 1);
                    shootLineBlock(player, player.level(), targetEntity.position());
                } else if (targetEntity instanceof LivingEntity) {
                    LightningEntity lightningEntity = new LightningEntity(EntityInit.LIGHTNING_ENTITY.get(), player.level());
                    lightningEntity.setSpeed(speed);
                    if (!player.getAbilities().instabuild) {
                        player.getCooldowns().addCooldown(stack.getItem(), 10 + (holder.getCurrentSequence() * 2));
                    }
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
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
            }
        }
        if (stack.getItem() instanceof LightningRedirection) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            if (holder.currentClassMatches(BeyonderClassInit.SAILOR) && holder.getCurrentSequence() <= 1) {
                for (LightningEntity lightning : player.level().getEntitiesOfClass(LightningEntity.class, player.getBoundingBox().inflate(200))) {
                    lightning.setTargetEntity(targetEntity);
                }
            }
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {
        return super.interactLivingEntity(pStack, pPlayer, pInteractionTarget, pUsedHand);
    }

    @Override
    public InteractionResult useAbilityOnBlock(UseOnContext context) {
        Player player = context.getPlayer();
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        if (!SimpleAbilityItem.checkAll(player, BeyonderClassInit.SAILOR.get(), 5, 200)) {
            return InteractionResult.FAIL;
        }
        shootLineBlock(player, player.level(), context.getClickLocation());
        SimpleAbilityItem.addCooldown(player, this, 15 + holder.getCurrentSequence() * 5);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!level.isClientSide()) {
            return useAbilityOnBlock(context);
        }
        return InteractionResult.PASS;
    }

}
