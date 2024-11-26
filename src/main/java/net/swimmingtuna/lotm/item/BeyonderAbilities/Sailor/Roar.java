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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.RoarEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class Roar extends SimpleAbilityItem {

    public Roar(Properties properties) {
        super(properties, BeyonderClassInit.SAILOR, 4, 500, 100);
    }

    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        roar(player);
        addCooldown(player);
        useSpirituality(player);
        return InteractionResult.SUCCESS;
    }

    public static void roar(Player player) {
        if (!player.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            RoarEntity roarEntity = new RoarEntity(EntityInit.ROAR_ENTITY.get(), player.level());
            roarEntity.teleportTo(player.getX(), player.getY(), player.getZ());
            Vec3 lookVec = player.getLookAngle();
            roarEntity.setDeltaMovement(lookVec.scale(10 - holder.getCurrentSequence()).x, lookVec.scale(10 - holder.getCurrentSequence()).y, lookVec.scale(10 - holder.getCurrentSequence()).z);
            roarEntity.hurtMarked = true;
            player.level().addFreshEntity(roarEntity);
            Vec3 startPos = player.getEyePosition();
            Vec3 endPos = startPos.add(lookVec.scale(10));
            BlockPos.betweenClosed(new BlockPos((int) Math.min(startPos.x, endPos.x) - 2, (int) Math.min(startPos.y, endPos.y) - 2, (int) Math.min(startPos.z, endPos.z) - 2), new BlockPos((int) Math.max(startPos.x, endPos.x) + 2, (int) Math.max(startPos.y, endPos.y) + 2, (int) Math.max(startPos.z, endPos.z) + 2)).
                    forEach(pos -> {
                        if (isInCone(startPos, lookVec, new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), 0.5) && startPos.distanceTo(new Vec3(pos.getX(), pos.getY(), pos.getZ())) <= 10) {
                            BlockState state = player.level().getBlockState(pos);
                            if (!state.isAir() && state.getDestroySpeed(player.level(), pos) >= 0) {
                                player.level().destroyBlock(pos, false);
                            }
                        }
                    });
        }
    }

    private static boolean isInCone(Vec3 origin, Vec3 direction, Vec3 point, double radius) {
        Vec3 toPoint = point.subtract(origin);
        double distance = toPoint.length();
        if (distance <= 0) return true;
        double angle = Math.acos(toPoint.dot(direction) / (distance * direction.length()));
        double maxAngle = Math.atan(radius / distance);
        return angle <= maxAngle;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, lets out a devastating roar\n" +
                "Spirituality Used: 500\n" +
                "Cooldown: 5 seconds").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}
