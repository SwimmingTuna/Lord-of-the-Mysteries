package net.swimmingtuna.lotm.item.custom.BeyonderAbilities;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.events.ReachChangeUUIDs;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import org.jetbrains.annotations.NotNull;
import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ManipulateMovement extends Item implements ReachChangeUUIDs {
    private final LazyOptional<Multimap<Attribute, AttributeModifier>> lazyAttributeMap = LazyOptional.of(() -> createAttributeMap());

    boolean anyEntitesAffected = false;

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(@NotNull EquipmentSlot pSlot) {
        if (pSlot == EquipmentSlot.MAINHAND) {
            return lazyAttributeMap.orElseGet(() -> createAttributeMap());
        }
        return super.getDefaultAttributeModifiers(pSlot);
    }

    private Multimap<Attribute, AttributeModifier> createAttributeMap() {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> attributeBuilder = ImmutableMultimap.builder();
        attributeBuilder.putAll(super.getDefaultAttributeModifiers(EquipmentSlot.MAINHAND));
        attributeBuilder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(BeyonderEntityReach, "Reach modifier", 300, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with entities
        attributeBuilder.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(BeyonderBlockReach, "Reach modifier", 300, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with blocks, p much useless for this item
        return attributeBuilder.build();
    }
    private static BlockPos targetPos;  // Store the target position for continuous effect

    public ManipulateMovement(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Player pPlayer = pContext.getPlayer();
        targetPos = pContext.getClickedPos();
        Level level = pContext.getLevel();

        if (!level.isClientSide) {
            BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
                if (spectatorSequence.getCurrentSequence() <= 4 && BeyonderHolderAttacher.getHolderUnwrap(pPlayer).useSpirituality(200)) {
                    manipulateEntities(pPlayer, level, targetPos, spectatorSequence.getCurrentSequence());
                    resetTargetPos(pPlayer);
                    if (!pPlayer.getAbilities().instabuild) {
                        pPlayer.getCooldowns().addCooldown(this, 300);
                    }
                }
            });
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, all living entities 250 blocks around you move to the location you clicked on\n" +
                    "Spirituality Used: 200\n" +
                    "Cooldown: 15 seconds"));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }

    private static void manipulateEntities(Player pPlayer, Level level, BlockPos targetPos, int sequence) {
        double duration = 1200 - (sequence * 200);
        if ((!pPlayer.level().isClientSide)) {
        for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(250))) {
            if (entity != pPlayer && entity.hasEffect(ModEffects.MANIPULATION.get()) && !entity.level().isClientSide) {
                entity.addEffect(new MobEffectInstance(ModEffects.MANIPULATION.get(), (int) duration,1,false,false));
                double deltaX = targetPos.getX() - entity.getX();
                double deltaY = targetPos.getY() - entity.getY();
                double deltaZ = targetPos.getZ() - entity.getZ();

                double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

                if (distance > 0) {
                    double speed = 0.25;
                    double normalizedX = deltaX / distance * speed;
                    double normalizedY = deltaY / distance * speed;
                    double normalizedZ = deltaZ / distance * speed;

                    BlockPos frontBlockPos = new BlockPos((int) (entity.getX() + normalizedX) , (int) (entity.getY() + normalizedY) , (int) (entity.getZ() + normalizedZ));
                    BlockPos frontBlockPos1 = new BlockPos((int) (entity.getX() + normalizedX * 2) , (int) (entity.getY() + normalizedY * 2) , (int) (entity.getZ() + normalizedZ * 2));

                    if (!level.getBlockState(frontBlockPos).isAir() || !level.getBlockState(frontBlockPos1).isAir()) {
                        if (entity.level().getGameTime() % 20 == 0) {
                            entity.teleportTo(entity.getX(), entity.getY() + 1.0, entity.getZ());
                        }
                    }
                    else {
                        entity.setDeltaMovement(normalizedX, entity.getY(), normalizedZ);
                    }
                        if (entity.position().distanceTo(new Vec3(targetPos.getX(), targetPos.getY(), targetPos.getZ())) < 2.0) {
                            entity.removeEffect(ModEffects.MANIPULATION.get());
                        }
                    }
                }
            }
        }
    }
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player pPlayer = event.player;
        if (!pPlayer.level().isClientSide && pPlayer.level().getGameTime() % 2 == 0) {
            BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
                if (ManipulateMovement.targetPos != null) {
                    ManipulateMovement.manipulateEntities(pPlayer, pPlayer.level(), ManipulateMovement.targetPos, spectatorSequence.getCurrentSequence());
                }
            });
        }

    }
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        int activeSlot = pPlayer.getInventory().selected;
        if (!pPlayer.level().isClientSide && !heldItem.isEmpty() && heldItem.getItem() instanceof ManipulateMovement) {
            pPlayer.getInventory().setItem(activeSlot, new ItemStack(ItemInit.ManipulateEmotion.get()));
            heldItem.shrink(1);
            event.setCanceled(true);
        }
    }
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        int activeSlot = pPlayer.getInventory().selected;
        if (!pPlayer.level().isClientSide && !heldItem.isEmpty() && heldItem.getItem() instanceof ManipulateMovement) {
            pPlayer.getInventory().setItem(activeSlot, new ItemStack(ItemInit.ManipulateEmotion.get()));
            heldItem.shrink(1);
            event.setCanceled(true);
        }
    }
    private static void resetTargetPos(Player pPlayer) {
        if ((!pPlayer.level().isClientSide)) {
            boolean anyEntityAffected = false;

            for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(250))) {
                if (entity.hasEffect(ModEffects.MANIPULATION.get()) && !entity.level().isClientSide) {
                    anyEntityAffected = true;
                    break;
                }
            }
            if (!anyEntityAffected) {
                targetPos = null;
                pPlayer.sendSystemMessage(Component.literal("Manipulation Position Reset"));
            }
        }
    }
}
