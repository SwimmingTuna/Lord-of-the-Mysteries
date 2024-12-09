package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class CalamityIncarnationTsunami extends SimpleAbilityItem {

    public CalamityIncarnationTsunami(Properties properties) {
        super(properties, BeyonderClassInit.SAILOR, 2,1000,1000);
    }
    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        calamityIncarnationTsunami(player);
        addCooldown(player);
        useSpirituality(player);
        return InteractionResult.SUCCESS;
    }

    public static void calamityIncarnationTsunami(Player player) {
        if (!player.level().isClientSide()) {
            int x = player.getPersistentData().getInt("calamityIncarnationTsunami");
            if (x == 0) {
                player.getPersistentData().putInt("calamityIncarnationTsunami", 200);
            } else {
                player.getPersistentData().putInt("calamityIncarnationTsunami", 0);
                player.displayClientMessage(Component.literal("Tsunami Incarnation Cancelled").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
            }
        }
    }
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, become a tsunami, turning into a massive sphere of water\n" +
                "Spirituality Used: 1000\n" +
                "Cooldown: 50 seconds").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
    public static void calamityIncarnationTsunamiTick(CompoundTag playerPersistentData, Player player, ServerLevel level) {
        //CALAMITY INCARNATION TSUNAMI
        int calamityIncarnationTsunami = playerPersistentData.getInt("calamityIncarnationTsunami");
        if (calamityIncarnationTsunami < 1) {
            return;
        }
        playerPersistentData.putInt("calamityIncarnationTsunami", calamityIncarnationTsunami - 1);
        BlockPos playerPos = player.blockPosition();
        double radius = 23.0;
        double minRemovalRadius = 25.0;
        double maxRemovalRadius = 30.0;

        // Create a sphere of water around the player
        for (int sphereX = (int) -radius; sphereX <= radius; sphereX++) {
            for (int sphereY = (int) -radius; sphereY <= radius; sphereY++) {
                for (int sphereZ = (int) -radius; sphereZ <= radius; sphereZ++) {
                    double distance = Math.sqrt(sphereX * sphereX + sphereY * sphereY + sphereZ * sphereZ);
                    if (distance <= radius) {
                        BlockPos blockPos = playerPos.offset(sphereX, sphereY, sphereZ);
                        if (level.getBlockState(blockPos).isAir() && !level.getBlockState(blockPos).is(Blocks.WATER)) {
                            level.setBlock(blockPos, Blocks.WATER.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }
        for (int sphereX = (int) -maxRemovalRadius; sphereX <= maxRemovalRadius; sphereX++) {
            for (int sphereY = (int) -maxRemovalRadius; sphereY <= maxRemovalRadius; sphereY++) {
                for (int sphereZ = (int) -maxRemovalRadius; sphereZ <= maxRemovalRadius; sphereZ++) {
                    double distance = Math.sqrt(sphereX * sphereX + sphereY * sphereY + sphereZ * sphereZ);
                    if (distance <= maxRemovalRadius && distance >= minRemovalRadius) {
                        BlockPos blockPos = playerPos.offset(sphereX, sphereY, sphereZ);
                        if (level.getBlockState(blockPos).getBlock() == Blocks.WATER) {
                            level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }
    }
}
