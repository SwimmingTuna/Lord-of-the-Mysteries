package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.LavaEntity;
import net.swimmingtuna.lotm.entity.StoneEntity;
import net.swimmingtuna.lotm.entity.TornadoEntity;
import net.swimmingtuna.lotm.events.ReachChangeUUIDs;
import net.swimmingtuna.lotm.init.EntityInit;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import java.text.ParsePosition;
import java.util.Random;

public class VolcanicEruption extends Item implements ReachChangeUUIDs {

    public VolcanicEruption(Properties pProperties) { //IMPORTANT!!!! FIGURE OUT HOW TO MAKE THIS WORK BY CLICKING ON A
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
                if (holder.isSailorClass() && sailorSequence.getCurrentSequence() <= 4 && sailorSequence.useSpirituality(300)) {
                    summonFallingLavaBlocks(level, pPlayer);
                    if (!pPlayer.getAbilities().instabuild)
                        pPlayer.getCooldowns().addCooldown(this, 240);
                }
            });
        }
        return super.use(level, pPlayer, hand);
    }

    private void summonFallingLavaBlocks(Level level, Player pPlayer) {
        BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
        int sequence = holder.getCurrentSequence();
        int spawnCount = 120 - (sequence * 10);
        Random random = new Random();
        BlockPos playerPos = pPlayer.blockPosition();
        for (int i = 0; i < spawnCount; i++) {
            int offsetX = random.nextInt(21) - 10; // Random number between -10 and 10
            int offsetZ = random.nextInt(21) - 10; // Random number between -10 and 10
            BlockPos spawnPos = playerPos.offset(offsetX, 0, offsetZ);
            while (level.isEmptyBlock(spawnPos) && spawnPos.getY() > level.getMinBuildHeight()) {
                spawnPos = spawnPos.below();
            }
            if (!level.isEmptyBlock(spawnPos) && isOnSurface(level, spawnPos)) {
                LavaEntity lavaEntity = new LavaEntity(EntityInit.LAVA_ENTITY.get(), level);
                lavaEntity.teleportTo(spawnPos.getX(), spawnPos.getY() + 3, spawnPos.getZ());
                lavaEntity.setDeltaMovement(0, 3 + (Math.random() * 3), 0); // Random vertical movement between 3 and 6
                lavaEntity.setLavaXRot(random.nextInt(18)); // Random X rotation
                lavaEntity.setLavaYRot(random.nextInt(18)); // Random Y rotation
                ScaleData scaleData = ScaleTypes.BASE.getScaleData(lavaEntity);
                scaleData.setScale(1.0f + random.nextFloat() * 2.0f); // Random scale between 1.0 and 3.0
                level.addFreshEntity(lavaEntity);
            }
        }
    }

    private static boolean isOnSurface(Level level, BlockPos pos) {
    return level.canSeeSky(pos.above()) || !level.getBlockState(pos.above()).isSolid();
    }
}