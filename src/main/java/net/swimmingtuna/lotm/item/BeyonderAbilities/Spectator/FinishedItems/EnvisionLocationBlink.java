package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;


import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class EnvisionLocationBlink extends SimpleAbilityItem {

    public EnvisionLocationBlink(Properties properties) {
        super(properties, BeyonderClassInit.SPECTATOR, 0, 0, 0);
    }

    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        int dreamIntoReality = (int) player.getAttribute(ModAttributes.DIR.get()).getValue();
        int blinkDistance = player.getPersistentData().getInt("BlinkDistance");
        if (!checkAll(player, BeyonderClassInit.SPECTATOR.get(), 0, blinkDistance * 4)) {
            return InteractionResult.FAIL;
        }
        addCooldown(player, this, 20 / dreamIntoReality);
        useSpirituality(player, 1000 / dreamIntoReality);
        envisionLocationBlink(player);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Use in order to envision your location in the direction you're looking"));
        tooltipComponents.add(Component.literal("Left Click for Envision Kingdom"));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("Blinked Distance * 4").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("1 Second").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(SimpleAbilityItem.getPathwayText(this.requiredClass.get()));
        tooltipComponents.add(SimpleAbilityItem.getClassText(this.requiredSequence, this.requiredClass.get()));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

    public void envisionLocationBlink(Player player) {
        if (!player.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            int blinkDistance = player.getPersistentData().getInt("BlinkDistance");
            Level level = player.level();
            if (holder.currentClassMatches(BeyonderClassInit.SPECTATOR) && !player.level().isClientSide() && holder.getCurrentSequence() == 0 && holder.useSpirituality(blinkDistance * 8)) {
                Vec3 lookVector = player.getLookAngle();
                double targetX = player.getX() + blinkDistance * lookVector.x();
                double targetY = (player.getY() + 1) + blinkDistance * lookVector.y();
                double targetZ = player.getZ() + blinkDistance * lookVector.z();
                player.teleportTo(targetX, targetY, targetZ);
                BlockPos playerPos = new BlockPos((int) player.getX(), (int) player.getY(), (int) player.getZ());
                BlockPos playerPos1 = new BlockPos((int) player.getX() + 1, (int) player.getY() + 1, (int) player.getZ() + 1);

                for (int x = -2; x <= 2; x++) {
                    for (int y = -2; y <= 2; y++) {
                        for (int z = -2; z <= 2; z++) {
                            BlockPos targetPos = playerPos.offset(x, y, z);
                            BlockPos targetPos1 = playerPos.offset(x + 1, y + 1, z + 1);
                            BlockState blockState = level.getBlockState(targetPos);
                            if (blockState.is(Blocks.DIRT) || blockState.is(Blocks.STONE) || blockState.is(Blocks.IRON_ORE) || blockState.is(Blocks.COAL_ORE)
                                    || blockState.is(Blocks.NETHERRACK) || blockState.is(Blocks.SNOW_BLOCK) || blockState.is(Blocks.SNOW) || blockState.is(Blocks.END_STONE) ||
                                    blockState.is(Blocks.DEEPSLATE) || blockState.is(Blocks.COPPER_ORE) || blockState.is(Blocks.SOUL_SAND) || blockState.is(Blocks.SOUL_SOIL) || blockState.is(Blocks.DEEPSLATE_COPPER_ORE) || blockState.is(Blocks.DEEPSLATE_COAL_ORE)) {
                                level.setBlockAndUpdate(targetPos, Blocks.AIR.defaultBlockState());
                                level.destroyBlock(playerPos, false);
                                level.destroyBlock(targetPos, false);
                                level.destroyBlock(targetPos1, false);
                                level.destroyBlock(playerPos1, false);
                            }
                        }
                    }
                }
                AttributeInstance dreamIntoReality = player.getAttribute(ModAttributes.DIR.get());
                if (!player.getAbilities().instabuild)

                    player.getCooldowns().addCooldown(this, (int) (20 / dreamIntoReality.getValue()));
            }
        }
    }
}
