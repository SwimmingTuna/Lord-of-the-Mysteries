package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.LavaEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import org.jetbrains.annotations.NotNull;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class VolcanicEruption extends SimpleAbilityItem {

    public VolcanicEruption(Properties properties) {
        super(properties, BeyonderClassInit.SAILOR, 2, 600, 400);
    }


    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        addCooldown(player);
        useSpirituality(player);
        volcanicEruption(player);
        return InteractionResult.SUCCESS;
    }

    private void volcanicEruption(Player player) {
        if (!player.level().isClientSide()) {
            Level level = player.level();
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            int sequence = holder.getCurrentSequence();
            int spawnCount = 120 - (sequence * 10);
            double randomX = (Math.random() * 1) - 0.5;
            double randomZ = (Math.random() * 1) - 0.5;
            Random random = new Random();
            BlockPos playerPos = player.blockPosition();
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
                    lavaEntity.setDeltaMovement(randomX, 3 + (Math.random() * 3), randomZ); // Random vertical movement between 3 and 6
                    lavaEntity.setLavaXRot(random.nextInt(18)); // Random X rotation
                    lavaEntity.setLavaYRot(random.nextInt(18)); // Random Y rotation
                    ScaleData scaleData = ScaleTypes.BASE.getScaleData(lavaEntity);
                    scaleData.setScale(1.0f + random.nextFloat() * 2.0f); // Random scale between 1.0 and 3.0
                    level.addFreshEntity(lavaEntity);
                }
            }
        }
    }
    private static boolean isOnSurface(Level level, BlockPos pos) {
    return level.canSeeSky(pos.above()) || !level.getBlockState(pos.above()).isSolid();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, summons a volcanic eruption from under you, shooting lava into the sky"));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("600").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("20 Seconds").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(SimpleAbilityItem.getPathwayText(this.requiredClass.get()));
        tooltipComponents.add(SimpleAbilityItem.getClassText(this.requiredSequence, this.requiredClass.get()));
        super.baseHoverText(stack, level, tooltipComponents, tooltipFlag);
    }


}
