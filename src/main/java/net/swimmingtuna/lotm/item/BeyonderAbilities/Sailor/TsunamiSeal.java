package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TsunamiSeal extends Item {
    public TsunamiSeal(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        if (!pPlayer.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
            if (holder == null || !holder.currentClassMatches(BeyonderClassInit.SAILOR)) {
                pPlayer.displayClientMessage(Component.literal("You are not of the Sailor pathway").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
                return super.use(level, pPlayer, hand);
            }
            if (holder.getSpirituality() < 1100) {
                pPlayer.displayClientMessage(Component.literal("You need 1100 spirituality in order to use this").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
                return super.use(level, pPlayer, hand);
            }

            BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(tyrantSequence -> {
                if (tyrantSequence.getCurrentSequence() <= 4 && tyrantSequence.useSpirituality(1100)) {
                    startTsunami(pPlayer);
                }
                if (!pPlayer.getAbilities().instabuild)
                    pPlayer.getCooldowns().addCooldown(this, 1800);
            });
        }
        return super.use(level, pPlayer, hand);
    }

    public static void startTsunami(Player pPlayer) {
        pPlayer.getPersistentData().putInt("sailorTsunami", 600);
        float yaw = pPlayer.getYRot();
        String direction = getDirectionFromYaw(yaw);
        pPlayer.getPersistentData().putString("sailorTsunamiDirection", direction);
        pPlayer.getPersistentData().putInt("sailorTsunamiX", (int) pPlayer.getX());
        pPlayer.getPersistentData().putInt("sailorTsunamiY", (int) pPlayer.getY());
        pPlayer.getPersistentData().putInt("sailorTsunamiZ", (int) pPlayer.getZ());
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
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Creates a massive wave of water in front of you, trapping any entity with more than 100 health in a seal or they're a player\n" +
                    "Spirituality Used: 1100\n" +
                    "Cooldown: 90 seconds").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }

    public static void summonTsunami(Player pPlayer) {
        CompoundTag tag = pPlayer.getPersistentData();
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
                if (pPlayer.level().getBlockState(blockPos).isAir()) {
                    pPlayer.level().setBlock(blockPos, Blocks.WATER.defaultBlockState(), 3);
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
        pPlayer.level().getEntitiesOfClass(LivingEntity.class, tsunamiAABB).forEach(livingEntity -> {
            if (livingEntity != pPlayer) {
                if (livingEntity.getMaxHealth() >= 100 || livingEntity instanceof Player) {
                    pPlayer.getPersistentData().putInt("sailorTsunami", 0);
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
        Player pPlayer = event.getEntity();
        if (!pPlayer.level().isClientSide()) {
            CompoundTag tag = pPlayer.getPersistentData();
            int sealCounter = tag.getInt("sailorSeal");
            if (sealCounter >= 1) {
                event.setCanceled(true);
            }
        }
    }
}
