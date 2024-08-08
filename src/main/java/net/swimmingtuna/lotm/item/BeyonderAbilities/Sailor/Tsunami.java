package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Tsunami extends Item {
    public Tsunami(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        if (!pPlayer.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
            if (holder == null || !holder.isSailorClass()) {
                pPlayer.displayClientMessage(Component.literal("You are not of the Sailor pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
                return super.use(level, pPlayer, hand);
            }
            if (holder.getSpirituality() < 75) {
                pPlayer.displayClientMessage(Component.literal("You need 75 spirituality in order to use this").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
                return super.use(level, pPlayer, hand);
            }

            BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(tyrantSequence -> {
                if (tyrantSequence.getCurrentSequence() <= 4 && tyrantSequence.useSpirituality(75)) {
                    startTsunami(pPlayer);
                }
                if (!pPlayer.getAbilities().instabuild)
                    pPlayer.getCooldowns().addCooldown(this, 60 * 20); // 60 seconds cooldown
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

    private static String getDirectionFromYaw(float yaw) {
        if (yaw < 0) {
            yaw += 360;
        }
        if (yaw >= 337.5 || yaw < 22.5) {
            return "N";
        } else if (yaw >= 22.5 && yaw < 67.5) {
            return "NE";
        } else if (yaw >= 67.5 && yaw < 112.5) {
            return "E";
        } else if (yaw >= 112.5 && yaw < 157.5) {
            return "SE";
        } else if (yaw >= 157.5 && yaw < 202.5) {
            return "S";
        } else if (yaw >= 202.5 && yaw < 247.5) {
            return "SW";
        } else if (yaw >= 247.5 && yaw < 292.5) {
            return "W";
        } else if (yaw >= 292.5 && yaw < 337.5) {
            return "NW";
        }
        return "N";
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Creates a massive wave of water in front of you\n" +
                    "Spirituality Used: 75\n" +
                    "Cooldown: 60 seconds"));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }

    @SubscribeEvent
    public static void tsunamiTick(TickEvent.PlayerTickEvent event) {
        Player pPlayer = event.player;
        if (!pPlayer.level().isClientSide() && event.phase == TickEvent.Phase.END) {
            CompoundTag tag = pPlayer.getPersistentData();
            int tsunami = tag.getInt("sailorTsunami");
            if (tsunami >= 1) {
                tag.putInt("sailorTsunami", tsunami - 5);
                summonTsunami(pPlayer);
            } else {
                tag.remove("sailorTsunamiDirection");
                tag.remove("sailorTsunamiX");
                tag.remove("sailorTsunamiY");
                tag.remove("sailorTsunamiZ");
            }
        }
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
                offsetZ = 1;  // Changed from -1 to 1
                break;
            case "NE":
                offsetX = -1;  // Changed from 1 to -1
                offsetZ = 1;  // Changed from -1 to 1
                break;
            case "E":
                offsetX = -1;  // Changed from 1 to -1
                break;
            case "SE":
                offsetX = -1;  // Changed from 1 to -1
                offsetZ = -1;  // Changed from 1 to -1
                break;
            case "S":
                offsetZ = -1;  // Changed from 1 to -1
                break;
            case "SW":
                offsetX = 1;  // Changed from -1 to 1
                offsetZ = -1;  // Changed from 1 to -1
                break;
            case "W":
                offsetX = 1;  // Changed from -1 to 1
                break;
            case "NW":
                offsetX = 1;  // Changed from -1 to 1
                offsetZ = 1;  // Changed from -1 to 1
                break;
        }

        int waveWidth = 80;
        int waveHeight = 10;
        int startDistance = 85;

        for (int w = -waveWidth / 2; w < waveWidth / 2; w++) {
            for (int h = 0; h < waveHeight; h++) {
                // Start the tsunami in front of the player and move it forward
                int x = playerX + (offsetX * startDistance) + (offsetX * (200 - tsunami) / 5);
                int y = playerY + h;
                int z = playerZ + (offsetZ * startDistance) + (offsetZ * (200 - tsunami) / 5);

                // Create a plane perpendicular to the movement direction
                if (offsetX == 0) {
                    x += w;
                } else if (offsetZ == 0) {
                    z += w;
                } else {
                    // For diagonal directions, create a diagonal wall
                    x += (offsetX > 0) ? w : -w;
                    z += (offsetZ > 0) ? w : -w;
                }

                BlockPos blockPos = new BlockPos(x, y, z);
                if (pPlayer.level().getBlockState(blockPos).isAir()) {
                    pPlayer.level().setBlock(blockPos, Blocks.WATER.defaultBlockState(), 3);
                }
            }
        }
    }
}
