package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class TsunamiSeal extends SimpleAbilityItem {

    public TsunamiSeal(Properties properties) {
        super(properties, BeyonderClassInit.SAILOR, 4, 1100, 1800);
    }

    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        addCooldown(player);
        useSpirituality(player);
        startTsunami(player);
        return InteractionResult.SUCCESS;
    }

    public static void startTsunami(Player player) {
        player.getPersistentData().putInt("sailorTsunami", 600);
        float yaw = player.getYRot();
        String direction = getDirectionFromYaw(yaw);
        player.getPersistentData().putString("sailorTsunamiDirection", direction);
        player.getPersistentData().putInt("sailorTsunamiX", (int) player.getX());
        player.getPersistentData().putInt("sailorTsunamiY", (int) player.getY());
        player.getPersistentData().putInt("sailorTsunamiZ", (int) player.getZ());
    }

    public static String getDirectionFromYaw(float yaw) {
        if (yaw < 0) {
            yaw += 360;
        }
        if (yaw >= 315 || yaw < 45) {
            return "N";
        } else if (yaw >= 45 && yaw < 135) {
            return "E";
        } else if (yaw >= 135 && yaw < 225) {
            return "S";
        } else if (yaw >= 225 && yaw < 315) {
            return "W";
        }
        return "N";
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Creates a massive wave of water in front of you, trapping any entity with more than 100 health in a seal or they're a player\n" +
                "Spirituality Used: 1100\n" +
                "Cooldown: 90 seconds").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

    public static void summonTsunami(Player player) {
        CompoundTag tag = player.getPersistentData();
        int playerX = tag.getInt("sailorTsunamiX");
        int playerY = tag.getInt("sailorTsunamiY");
        int playerZ = tag.getInt("sailorTsunamiZ");
        int tsunami = tag.getInt("sailorTsunami");
        String direction = tag.getString("sailorTsunamiDirection");

        int offsetX = 0;
        int offsetZ = 0;

        switch (direction) {
            case "N":
                offsetZ = 1;
                break;
            case "E":
                offsetX = -1;
                break;
            case "S":
                offsetZ = -1;
                break;
            case "W":
                offsetX = 1;
                break;
        }

        int waveWidth = 80;
        int waveHeight = 10;
        int startDistance = 85;

        for (int w = -waveWidth / 2; w < waveWidth / 2; w++) {
            for (int h = 0; h < waveHeight; h++) {
                int x = playerX + (offsetX * startDistance) + (offsetX * (200 - tsunami) / 5);
                int y = playerY + h;
                int z = playerZ + (offsetZ * startDistance) + (offsetZ * (200 - tsunami) / 5);

                if (offsetX == 0) {
                    x += w;
                } else {
                    z += w;
                }

                BlockPos blockPos = new BlockPos(x, y, z);
                if (player.level().getBlockState(blockPos).isAir()) {
                    player.level().setBlock(blockPos, Blocks.WATER.defaultBlockState(), 3);
                }
            }
        }

        // Create AABB representing the tsunami area
        AABB tsunamiAABB = new AABB(
                playerX + (offsetX * startDistance) + (offsetX * (200 - tsunami) / 5) - waveWidth / 2,
                playerY,
                playerZ + (offsetZ * startDistance) + (offsetZ * (200 - tsunami) / 5) - waveWidth / 2,
                playerX + (offsetX * startDistance) + (offsetX * (200 - tsunami) / 5) + waveWidth / 2,
                playerY + waveHeight,
                playerZ + (offsetZ * startDistance) + (offsetZ * (200 - tsunami) / 5) + waveWidth / 2
        );
        player.level().getEntitiesOfClass(LivingEntity.class, tsunamiAABB).forEach(livingEntity -> {
            if (livingEntity != player) {
                if (livingEntity.getMaxHealth() >= 100 || livingEntity instanceof Player) {
                    player.getPersistentData().putInt("sailorTsunami", 0);
                    livingEntity.getPersistentData().putInt("sailorSeal", 1200);
                    livingEntity.getPersistentData().putInt("sailorSealX", (int) livingEntity.getX());
                    livingEntity.getPersistentData().putInt("sailorSeaY", (int) livingEntity.getY());
                    livingEntity.getPersistentData().putInt("sailorSealZ", (int) livingEntity.getZ());
                }
            }
        });
    }

    @SubscribeEvent
    public static void sealItemCanceler(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        if (!player.level().isClientSide()) {
            CompoundTag tag = player.getPersistentData();
            int sealCounter = tag.getInt("sailorSeal");
            if (sealCounter >= 1) {
                event.setCanceled(true);
            }
        }
    }
}
