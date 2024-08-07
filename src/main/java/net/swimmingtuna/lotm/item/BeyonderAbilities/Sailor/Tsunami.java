package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.phys.Vec3;
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
        pPlayer.getPersistentData().putInt("sailorTsunami", 200);
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
            int playerX = tag.getInt("sailorTsunamiX");
            int playerY = tag.getInt("sailorTsunamiY");
            int playerZ = tag.getInt("sailorTsunamiZ");
            int tsunami = tag.getInt("sailorTsunami");
            if (tsunami >= 1) {
                tag.putInt("sailorTsunami", tsunami - 1);
                summonTsunami(pPlayer);
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
                offsetZ = -1;
                break;
            case "NE":
                offsetX = 1;
                offsetZ = -1;
                break;
            case "E":
                offsetX = 1;
                break;
            case "SE":
                offsetX = 1;
                offsetZ = 1;
                break;
            case "S":
                offsetZ = 1;
                break;
            case "SW":
                offsetX = -1;
                offsetZ = 1;
                break;
            case "W":
                offsetX = -1;
                break;
            case "NW":
                offsetX = -1;
                offsetZ = -1;
                break;
        }

        for (int height = 0; height < 10; height++) {
            for (int width = -5; width <= 5; width++) {
                int x = playerX + (offsetX * (tsunami / 10)) + (offsetZ == 0 ? width : 0);
                int y = playerY + height;
                int z = playerZ + (offsetZ * (tsunami / 10)) + (offsetX == 0 ? width : 0);

                pPlayer.level().setBlock(new BlockPos(x, y, z), Blocks.WATER.defaultBlockState(), 3);
            }
        }

        if (tsunami > 0) {
            tag.putInt("sailorTsunami", tsunami - 1);
        }
    }

}