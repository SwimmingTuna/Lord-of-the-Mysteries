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
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.LightningEntity;
import net.swimmingtuna.lotm.events.ReachChangeUUIDs;
import net.swimmingtuna.lotm.init.EntityInit;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SailorLightning extends Item implements ReachChangeUUIDs {
    private final LazyOptional<Multimap<Attribute, AttributeModifier>> lazyAttributeMap = LazyOptional.of(this::createAttributeMap);

    public SailorLightning(Properties pProperties) { //IMPORTANT!!!! FIGURE OUT HOW TO MAKE THIS WORK BY CLICKING ON A
        super(pProperties);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot pSlot) {
        if (pSlot == EquipmentSlot.MAINHAND) {
            return lazyAttributeMap.orElseGet(this::createAttributeMap);
        }
        return super.getDefaultAttributeModifiers(pSlot);
    }

    private Multimap<Attribute, AttributeModifier> createAttributeMap() {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> attributeBuilder = ImmutableMultimap.builder();
        attributeBuilder.putAll(super.getDefaultAttributeModifiers(EquipmentSlot.MAINHAND));
        attributeBuilder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(BeyonderEntityReach, "Reach modifier", 150, AttributeModifier.Operation.ADDITION)); // adds a 12 block reach for interacting with entities
        attributeBuilder.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(BeyonderBlockReach, "Reach modifier", 150, AttributeModifier.Operation.ADDITION)); // adds a 12 block reach for interacting with blocks, pretty much useless for this item
        return attributeBuilder.build();
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        if (!pPlayer.level().isClientSide()) {
            Vec3 eyePosition = pPlayer.getEyePosition();
            Vec3 lookVector = pPlayer.getLookAngle();
            double reachDistance = 150;
            Vec3 endPoint = eyePosition.add(lookVector.scale(reachDistance));

            // Check for block interaction
            BlockHitResult blockHit = level.clip(new ClipContext(eyePosition, endPoint, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, pPlayer));
            if (blockHit.getType() != HitResult.Type.MISS) {
                return InteractionResultHolder.pass(pPlayer.getItemInHand(hand));
            }

            // Check for entity interaction
            EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(level, pPlayer, eyePosition, endPoint, pPlayer.getBoundingBox().inflate(reachDistance), entity -> !entity.isSpectator() && entity.isPickable());
            if (entityHit != null) {
                return InteractionResultHolder.pass(pPlayer.getItemInHand(hand));
            }

            // If no block or entity is targeted, proceed with the original functionality
            BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
            if (!holder.isSailorClass()) {
                pPlayer.displayClientMessage(Component.literal("You are not of the Sailor pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
            }
            if (holder.getSpirituality() < 120) {
                pPlayer.displayClientMessage(Component.literal("You need 75 spirituality in order to use this").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
            }
            BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(sailorSequence -> {
                if (holder.isSailorClass() && sailorSequence.getCurrentSequence() <= 5 && sailorSequence.useSpirituality(120)) {
                    shootLine(pPlayer, level);
                    if (!pPlayer.getAbilities().instabuild)
                        pPlayer.getCooldowns().addCooldown(this, 240);
                }
            });
        }
        return super.use(level, pPlayer, hand);
    }


    private static void shootLine(Player pPlayer, Level level) {
        if (!level.isClientSide()) {
            Vec3 lookVec = pPlayer.getLookAngle();
            float speed = 15.0f;
            LightningEntity lightningEntity = new LightningEntity(EntityInit.LIGHTNING_ENTITY.get(), level);
            lightningEntity.setSpeed(speed);
            lightningEntity.setDeltaMovement(lookVec.x, lookVec.y, lookVec.z);
            lightningEntity.setMaxLength(30);
            lightningEntity.setOwner(pPlayer);
            lightningEntity.setOwner(pPlayer);
            lightningEntity.teleportTo(pPlayer.getX(), pPlayer.getY(), pPlayer.getZ());
            level.addFreshEntity(lightningEntity);
        }
    }
    private static void shootLineBlock(Player pPlayer, Level level, Vec3 targetPos) {
        if (!level.isClientSide()) {
            Vec3 lookVec = pPlayer.getLookAngle();
            float speed = 15.0f;
            LightningEntity lightningEntity = new LightningEntity(EntityInit.LIGHTNING_ENTITY.get(), level);
            lightningEntity.setSpeed(speed);
            lightningEntity.setDeltaMovement(lookVec.x, lookVec.y, lookVec.z);
            lightningEntity.setMaxLength(30);
            lightningEntity.setOwner(pPlayer);
            lightningEntity.setOwner(pPlayer);
            lightningEntity.teleportTo(pPlayer.getX(), pPlayer.getY(), pPlayer.getZ());
            lightningEntity.setTargetPos(targetPos);
            level.addFreshEntity(lightningEntity);
        }
    }
    public static void shootLineBlockHigh(Player pPlayer, Level level) {
        if (!level.isClientSide()) {
            float speed = 10.0f;
            LightningEntity lightningEntity = new LightningEntity(EntityInit.LIGHTNING_ENTITY.get(), level);
            lightningEntity.setSpeed(speed);
            lightningEntity.setDeltaMovement(0, -2, 0);
            lightningEntity.setMaxLength(60);
            lightningEntity.setOwner(pPlayer);
            lightningEntity.setOwner(pPlayer);
            lightningEntity.teleportTo(pPlayer.getX() + ((Math.random() * 150) - 75), pPlayer.getY() + 60, pPlayer.getZ() + ((Math.random() * 150) - 75));
            level.addFreshEntity(lightningEntity);
        }
    }


    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        Player pPlayer = event.getEntity();
        Entity targetEntity = event.getTarget();
        ItemStack itemStack = pPlayer.getItemInHand(event.getHand());
        if (!pPlayer.level().isClientSide()) {
            if (itemStack.getItem() instanceof SailorLightning) {
                BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
                if (!holder.isSailorClass()) {
                    pPlayer.displayClientMessage(Component.literal("You are not of the Sailor pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
                }
                if (holder.getSpirituality() < 120) {
                    pPlayer.displayClientMessage(Component.literal("You need 120 spirituality in order to use this").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
                }
                if (!pPlayer.getCooldowns().isOnCooldown(itemStack.getItem()) && holder.isSailorClass() && !pPlayer.level().isClientSide() && !targetEntity.level().isClientSide() && holder.getCurrentSequence() <= 5 && holder.useSpirituality(120)) {
                    float speed = 15.0f;
                    if (targetEntity instanceof AbstractArrow || targetEntity instanceof AbstractHurtingProjectile || targetEntity instanceof Projectile || targetEntity instanceof Arrow) {
                        CompoundTag tag = targetEntity.getPersistentData();
                        if (!pPlayer.getAbilities().instabuild) {
                            pPlayer.getCooldowns().addCooldown(itemStack.getItem(), 5);
                        }
                        int x = tag.getInt("sailorLightningProjectileCounter");
                        tag.putInt("sailorLightningProjectileCounter", x + 1);
                        shootLineBlock(pPlayer, pPlayer.level(), targetEntity.position());
                    } else if (targetEntity instanceof LivingEntity) {
                        pPlayer.sendSystemMessage(Component.literal("entity check"));
                        LightningEntity lightningEntity = new LightningEntity(EntityInit.LIGHTNING_ENTITY.get(), pPlayer.level());
                        lightningEntity.setSpeed(speed);
                        if (!pPlayer.getAbilities().instabuild) {
                            pPlayer.getCooldowns().addCooldown(itemStack.getItem(), 10 + (holder.getCurrentSequence() * 2));
                        }
                        Vec3 lookVec = pPlayer.getLookAngle();
                        lightningEntity.setDeltaMovement(lookVec.x, lookVec.y, lookVec.z);
                        lightningEntity.setMaxLength(30);
                        lightningEntity.teleportTo(pPlayer.getX(), pPlayer.getY(), pPlayer.getZ());
                        lightningEntity.setTargetPos(targetEntity.position());
                        lightningEntity.setOwner(pPlayer);
                        lightningEntity.setOwner(pPlayer);
                        pPlayer.level().addFreshEntity(lightningEntity);
                    }
                    event.setCanceled(true);
                }
            }
        }
    }
    @SubscribeEvent
    public static void onBlockInteract(PlayerInteractEvent.RightClickBlock event) {
        Player pPlayer = event.getEntity();
        ItemStack itemStack = pPlayer.getItemInHand(event.getHand());
        if (!pPlayer.level().isClientSide()) {
            if (itemStack.getItem() instanceof SailorLightning) {
                BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
                if (!holder.isSailorClass()) {
                    pPlayer.displayClientMessage(Component.literal("You are not of the Sailor pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
                }
                if (holder.getSpirituality() < 120) {
                    pPlayer.displayClientMessage(Component.literal("You need 120 spirituality in order to use this").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
                }
                if (!pPlayer.getCooldowns().isOnCooldown(itemStack.getItem()) && holder.isSailorClass() && !pPlayer.level().isClientSide() && itemStack.getItem() instanceof SailorLightning && holder.getCurrentSequence() <= 5 && holder.useSpirituality(120)) {
                    shootLineBlock(pPlayer, pPlayer.level(), event.getPos().getCenter());
                    pPlayer.sendSystemMessage(Component.literal("block check"));
                    if (!pPlayer.getAbilities().instabuild) {
                        pPlayer.getCooldowns().addCooldown(itemStack.getItem(), 8 + holder.getCurrentSequence());
                    }
                    event.setCanceled(true);
                }
            }
        }
    }
}
