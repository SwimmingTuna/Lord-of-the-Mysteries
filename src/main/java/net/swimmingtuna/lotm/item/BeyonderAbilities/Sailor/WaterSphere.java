package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.MeteorEntity;
import net.swimmingtuna.lotm.entity.MeteorNoLevelEntity;
import net.swimmingtuna.lotm.events.ReachChangeUUIDs;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WaterSphere extends Item implements ReachChangeUUIDs {

    public WaterSphere(Properties pProperties) { //IMPORTANT!!!! FIGURE OUT HOW TO MAKE THIS WORK BY CLICKING ON A
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        if (!pPlayer.level().isClientSide()) {

            // If no block or entity is targeted, proceed with the original functionality
            BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
            if (!holder.isSailorClass()) {
                pPlayer.displayClientMessage(Component.literal("You are not of the Sailor pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
            }
            if (holder.getSpirituality() < 300) {
                pPlayer.displayClientMessage(Component.literal("You need 300 spirituality in order to use this").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
            }
            BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(sailorSequence -> {
                if (holder.isSailorClass() && sailorSequence.getCurrentSequence() <= 5 && sailorSequence.useSpirituality(120)) {
                    pPlayer.getPersistentData().putInt("sailorSphere", 200);
                    if (!pPlayer.getAbilities().instabuild)
                        pPlayer.getCooldowns().addCooldown(this, 240);
                }
            });
        }
        return super.use(level, pPlayer, hand);
    }
    @SubscribeEvent
    public static void waterSphereTick (TickEvent.PlayerTickEvent event) {
        Player pPlayer = event.player;
        if (!pPlayer.level().isClientSide() && event.phase == TickEvent.Phase.END) {
            if (pPlayer.getPersistentData().getInt("sailorSphere") >= 5) {
                for (Entity entity : pPlayer.level().getEntitiesOfClass(Entity.class, pPlayer.getBoundingBox().inflate(4))) {
                    if (!(entity instanceof LivingEntity && !(entity instanceof MeteorEntity) && !(entity instanceof MeteorNoLevelEntity))) {
                        entity.remove(Entity.RemovalReason.DISCARDED);
                    }
                }
                Level level = pPlayer.level();
                BlockPos playerPos = pPlayer.blockPosition();
                double radius = 3.0;
                double minRemovalRadius = 4.0;
                double maxRemovalRadius = 7.0;

                // Create a sphere of water around the player
                for (int x = (int) -radius; x <= radius; x++) {
                    for (int y = (int) -radius; y <= radius; y++) {
                        for (int z = (int) -radius; z <= radius; z++) {
                            double distance = Math.sqrt(x * x + y * y + z * z);
                            if (distance <= radius) {
                                BlockPos blockPos = playerPos.offset(x, y, z);
                                if (level.getBlockState(blockPos).isAir() && !level.getBlockState(blockPos).is(Blocks.WATER)) {
                                    level.setBlock(blockPos, Blocks.WATER.defaultBlockState(), 3);
                                }
                            }
                        }
                    }
                }
                for (int x = (int) -maxRemovalRadius; x <= maxRemovalRadius; x++) {
                    for (int y = (int) -maxRemovalRadius; y <= maxRemovalRadius; y++) {
                        for (int z = (int) -maxRemovalRadius; z <= maxRemovalRadius; z++) {
                            double distance = Math.sqrt(x * x + y * y + z * z);
                            if (distance <= maxRemovalRadius && distance >= minRemovalRadius) {
                                BlockPos blockPos = playerPos.offset(x, y, z);
                                if (level.getBlockState(blockPos).getBlock() == Blocks.WATER) {
                                    level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
                                }
                            }
                        }
                    }
                }
            }
            if (pPlayer.getPersistentData().getInt("sailorSphere") >= 1 && pPlayer.getPersistentData().getInt("sailorSphere") <= 4) {
                pPlayer.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 100, 100,false,false));
                for (int x = -6; x <= 6; x++) {
                    for (int y = -6; y <= 6; y++) {
                        for (int z = -6; z <= 6; z++) {
                            double distance = Math.sqrt(x * x + y * y + z * z);
                            if (distance <= 6) {
                                BlockPos blockPos = pPlayer.getOnPos().offset(x, y, z);
                                if (pPlayer.level().getBlockState(blockPos).getBlock() == Blocks.WATER) {
                                    pPlayer.level().setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
                                }
                            }
                        }
                    }
                }
            }
            if (pPlayer.getPersistentData().getInt("sailorSphere") >= 1) {
                pPlayer.getPersistentData().putInt("sailorSphere", pPlayer.getPersistentData().getInt("sailorSphere") - 1);
            }
        }
    }
}
