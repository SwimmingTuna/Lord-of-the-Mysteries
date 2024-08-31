package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ExtremeColdness extends Item {
    public ExtremeColdness(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        if (!pPlayer.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
            if (!holder.isSailorClass()) {
                pPlayer.displayClientMessage(Component.literal("You are not of the Sailor pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
            }
            if (holder.getSpirituality() < 75) {
                pPlayer.displayClientMessage(Component.literal("You need 75 spirituality in order to use this").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
            }

        BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(tyrantSequence -> {
            if (holder.isSailorClass() && tyrantSequence.getCurrentSequence() <= 4 && tyrantSequence.useSpirituality(75)) {
                useItem(pPlayer);
            }
            if (!pPlayer.getAbilities().instabuild)
                pPlayer.getCooldowns().addCooldown(this, 60);

        });
            }
        return super.use(level, pPlayer, hand);
    }

    public static void useItem(Player pPlayer) {
        pPlayer.getPersistentData().putInt("sailorExtremeColdness", 1);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, shoots a projectile that upon hit, pulls the target towards the user\n" +
                    "Spirituality Used: 75\n" +
                    "Cooldown: 3 seconds"));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }

    @SubscribeEvent
    public static void freezeTick(TickEvent.PlayerTickEvent event) {
        Player pPlayer = event.player;
        BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
        if (event.phase == TickEvent.Phase.END && !pPlayer.level().isClientSide()) {
            CompoundTag tag = pPlayer.getPersistentData();
            int extremeColdness = tag.getInt("sailorExtremeColdness");
            if (extremeColdness >= 150 - (holder.getCurrentSequence()) * 20) {
                tag.putInt("sailorExtremeColdness", 0);
                extremeColdness = 0;
            }
            if (extremeColdness >= 1) {
                pPlayer.sendSystemMessage(Component.literal("x is " + extremeColdness));
                tag.putInt("sailorExtremeColdness", extremeColdness + 1);

                AABB areaOfEffect = pPlayer.getBoundingBox().inflate(extremeColdness);
                List<LivingEntity> entities = pPlayer.level().getEntitiesOfClass(LivingEntity.class, areaOfEffect);
                for (LivingEntity entity : entities) {
                    if (entity != pPlayer) {
                        int affectedBySailorExtremeColdness = entity.getPersistentData().getInt("affectedBySailorExtremeColdness");
                        entity.getPersistentData().putInt("affectedBySailorExtremeColdness", affectedBySailorExtremeColdness + 1);
                        entity.setTicksFrozen(1);
                    }
                }
                List<Entity> entities1 = pPlayer.level().getEntitiesOfClass(Entity.class, areaOfEffect); //test thsi
                for (Entity entity : entities1) {
                    if (!(entity instanceof LivingEntity)) {
                        int affectedBySailorColdness = entity.getPersistentData().getInt("affectedBySailorColdness");
                        entity.getPersistentData().putInt("affectedBySailorColdness", affectedBySailorColdness + 1);
                        if (affectedBySailorColdness == 10) {
                            entity.setDeltaMovement(entity.getDeltaMovement().x() / 5, entity.getDeltaMovement().y() / 5, entity.getDeltaMovement().z() / 5);
                            entity.hurtMarked = true;
                            entity.getPersistentData().putInt("affectedBySailorColdness", 0);
                        }
                    }
                }

                // Additional part: Turn the top 3 surface blocks within radius into ice
                BlockPos playerPos = pPlayer.blockPosition();
                int radius = extremeColdness; // Adjust the division factor as needed
                int blocksToProcessPerTick = 2000;  // Adjust as needed
                int processedBlocks = 0;

                // Cache for heightmap lookups
                Map<BlockPos, Integer> heightMapCache = new HashMap<>();

                for (int dx = -radius; dx <= radius && processedBlocks < blocksToProcessPerTick; dx++) {
                    for (int dz = -radius; dz <= radius && processedBlocks < blocksToProcessPerTick; dz++) {
                        BlockPos surfacePos = playerPos.offset(dx, 0, dz);

                        // Check cache first
                        Integer surfaceY = heightMapCache.get(surfacePos);
                        if (surfaceY == null) {
                            // If not cached, calculate and store in cache
                            surfaceY = pPlayer.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, surfacePos).getY();
                            heightMapCache.put(surfacePos, surfaceY);
                        }

                        for (int dy = 0; dy < 3; dy++) {
                            BlockPos targetPos = new BlockPos(surfacePos.getX(), surfaceY - dy, surfacePos.getZ());
                            if (canFreezeBlock(pPlayer, targetPos)) {
                                pPlayer.level().setBlockAndUpdate(targetPos, Blocks.ICE.defaultBlockState());
                                processedBlocks++;
                            }
                        }
                    }
                }
            }
        }
    }


    private static boolean canFreezeBlock(Player pPlayer, BlockPos targetPos) {
        return pPlayer.level().getBlockState(targetPos).getBlock() != Blocks.BEDROCK &&
                pPlayer.level().getBlockState(targetPos).getBlock() != Blocks.AIR &&
                pPlayer.level().getBlockState(targetPos).getBlock() != Blocks.CAVE_AIR &&
                pPlayer.level().getBlockState(targetPos).getBlock() != Blocks.VOID_AIR &&
                pPlayer.level().getBlockState(targetPos).getBlock() != Blocks.ICE;
    }


    @SubscribeEvent
    public static void freezeTick2(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        CompoundTag tag = entity.getPersistentData();
        int affectedBySailorExtremeColdness = tag.getInt("affectedBySailorExtremeColdness");
        if (!entity.level().isClientSide()) {
            if (entity instanceof Player pPlayer) {
                pPlayer.setTicksFrozen(3);
            }
            if (affectedBySailorExtremeColdness == 5) {
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1,false,false));
            }
            if (affectedBySailorExtremeColdness == 10) {
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 2,false,false));
            }
            if (affectedBySailorExtremeColdness == 15) {
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 3,false,false));
            }
            if (affectedBySailorExtremeColdness >= 20) {
                entity.addEffect(new MobEffectInstance(ModEffects.AWE.get(), 100, 1,false,false));
                tag.putInt("affectedBySailorExtremeColdness", 0);
                affectedBySailorExtremeColdness = 0;
                entity.hurt(entity.damageSources().freeze(), 30);
            }
        }
    }
}