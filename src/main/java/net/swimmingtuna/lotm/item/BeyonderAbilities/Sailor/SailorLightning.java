package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
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
import net.swimmingtuna.lotm.events.ReachChangeUUIDs;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.EntityInit;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SailorLightning extends Item {

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
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (player.level().isClientSide()) {
            return super.use(level, player, hand);
        }
        Vec3 eyePosition = player.getEyePosition();
        Vec3 lookVector = player.getLookAngle();
        double reachDistance = 150;
        Vec3 endPoint = eyePosition.add(lookVector.scale(reachDistance));

        // Check for block interaction
        BlockHitResult blockHit = level.clip(new ClipContext(eyePosition, endPoint, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        if (blockHit.getType() != HitResult.Type.MISS) {
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }

        // Check for entity interaction
        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(level, player, eyePosition, endPoint, player.getBoundingBox().inflate(reachDistance), entity -> !entity.isSpectator() && entity.isPickable());
        if (entityHit != null) {
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }

        // If no block or entity is targeted, proceed with the original functionality
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        if (!holder.currentClassMatches(BeyonderClassInit.SAILOR)) {
            player.displayClientMessage(Component.literal("You are not of the Sailor pathway").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }
        if (holder.getSpirituality() < 200) {
            player.displayClientMessage(Component.literal("You need 200 spirituality in order to use this").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }
        if (holder.currentClassMatches(BeyonderClassInit.SAILOR) && holder.getCurrentSequence() <= 5 && holder.useSpirituality(200)) {
            shootLine(player, level);
            if (!player.getAbilities().instabuild) {
                player.getCooldowns().addCooldown(this, 240);
            }
        }
        return super.use(level, player, hand);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Shoots out a lightning bolt, if used on a block or entity, the lightning bolt travels towards it in exchange for 100 extra spirituality and higher cooldown, otherwise, it moves randomly in the direction you look\n" +
                "Spirituality Used: 200\n" +
                "Cooldown: 2 seconds").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE));
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
            lightningEntity.setOwner(player);
            lightningEntity.teleportTo(player.getX(), player.getY(), player.getZ());
            level.addFreshEntity(lightningEntity);
        }
    }
    private static void shootLineBlock(Player player, Level level, Vec3 targetPos) {
        if (!level.isClientSide()) {
            Vec3 lookVec = player.getLookAngle();
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            holder.useSpirituality(100);
            float speed = 15.0f;
            if (!player.getAbilities().instabuild) {
                ItemStack itemStack = player.getUseItem();
                player.getCooldowns().addCooldown(itemStack.getItem(), 10 + (holder.getCurrentSequence() * 2));
            }
            LightningEntity lightningEntity = new LightningEntity(EntityInit.LIGHTNING_ENTITY.get(), level);
            lightningEntity.setSpeed(speed);
            lightningEntity.setDeltaMovement(lookVec.x, lookVec.y, lookVec.z);
            lightningEntity.setMaxLength(30);
            lightningEntity.setOwner(player);
            lightningEntity.setOwner(player);
            lightningEntity.teleportTo(player.getX(), player.getY(), player.getZ());
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


    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        Entity targetEntity = event.getTarget();
        ItemStack itemStack = player.getItemInHand(event.getHand());
        if (!player.level().isClientSide()) {
            if (itemStack.getItem() instanceof SailorLightning) {
                BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
                if (!holder.currentClassMatches(BeyonderClassInit.SAILOR)) {
                    player.displayClientMessage(Component.literal("You are not of the Sailor pathway").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
                }
                if (holder.getSpirituality() < 120) {
                    player.displayClientMessage(Component.literal("You need 120 spirituality in order to use this").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
                }
                if (!player.getCooldowns().isOnCooldown(itemStack.getItem()) && holder.currentClassMatches(BeyonderClassInit.SAILOR) && !player.level().isClientSide() &&
                        !targetEntity.level().isClientSide() && holder.getCurrentSequence() <= 5 && holder.useSpirituality(120)) {
                    float speed = 15.0f;
                    if (targetEntity instanceof AbstractArrow || targetEntity instanceof AbstractHurtingProjectile || targetEntity instanceof Projectile || targetEntity instanceof Arrow) {
                        CompoundTag tag = targetEntity.getPersistentData();
                        if (!player.getAbilities().instabuild) {
                            player.getCooldowns().addCooldown(itemStack.getItem(), 5);
                        }
                        int x = tag.getInt("sailorLightningProjectileCounter");
                        tag.putInt("sailorLightningProjectileCounter", x + 1);
                        shootLineBlock(player, player.level(), targetEntity.position());
                    } else if (targetEntity instanceof LivingEntity) {
                        LightningEntity lightningEntity = new LightningEntity(EntityInit.LIGHTNING_ENTITY.get(), player.level());
                        lightningEntity.setSpeed(speed);
                        if (!player.getAbilities().instabuild) {
                            player.getCooldowns().addCooldown(itemStack.getItem(), 10 + (holder.getCurrentSequence() * 2));
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
                    event.setCanceled(true);
                }
            }
            if (itemStack.getItem() instanceof LightningRedirection) {
                BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
                if (holder.currentClassMatches(BeyonderClassInit.SAILOR) && holder.getCurrentSequence() <= 1) {
                    for (Entity entity : player.level().getEntitiesOfClass(Entity.class, player.getBoundingBox().inflate(200))) {
                        if (entity instanceof LightningEntity lightning) {
                            lightning.setTargetEntity(targetEntity);
                        }
                    }
                }
                event.setCanceled(true);
            }
        }
    }
    @SubscribeEvent
    public static void onBlockInteract(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        ItemStack itemStack = player.getItemInHand(event.getHand());
        if (!player.level().isClientSide()) {
            if (itemStack.getItem() instanceof SailorLightning) {
                BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
                if (!holder.currentClassMatches(BeyonderClassInit.SAILOR)) {
                    player.displayClientMessage(Component.literal("You are not of the Sailor pathway").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
                }
                if (holder.getSpirituality() < 120) {
                    player.displayClientMessage(Component.literal("You need 120 spirituality in order to use this").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
                }
                if (!player.getCooldowns().isOnCooldown(itemStack.getItem()) && holder.currentClassMatches(BeyonderClassInit.SAILOR) && !player.level().isClientSide() && itemStack.getItem() instanceof SailorLightning && holder.getCurrentSequence() <= 5 && holder.useSpirituality(120)) {
                    shootLineBlock(player, player.level(), event.getPos().getCenter());
                    if (!player.getAbilities().instabuild) {
                        player.getCooldowns().addCooldown(itemStack.getItem(), 8 + holder.getCurrentSequence());
                    }
                    event.setCanceled(true);
                }
            }
            if (itemStack.getItem() instanceof LightningRedirection) {
                BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
                if (holder.currentClassMatches(BeyonderClassInit.SAILOR) && holder.getCurrentSequence() <= 1) {
                    for (Entity entity : player.level().getEntitiesOfClass(Entity.class, player.getBoundingBox().inflate(200))) {
                        if (entity instanceof LightningEntity lightning) {
                            lightning.setTargetPos(event.getPos().getCenter());
                        }
                    }
                }
                event.setCanceled(true);
            }
        }
    }
}
