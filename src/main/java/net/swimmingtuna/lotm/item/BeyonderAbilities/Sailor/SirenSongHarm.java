package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class SirenSongHarm extends Item {
    public SirenSongHarm(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            if (holder != null) {
                if (!holder.currentClassMatches(BeyonderClassInit.SAILOR)) {
                    player.displayClientMessage(Component.literal("You are not of the Sailor pathway").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
                } else if (holder.getSpirituality() < 300) {
                    player.displayClientMessage(Component.literal("You need 300 spirituality in order to use this").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
                } else if (holder.currentClassMatches(BeyonderClassInit.SAILOR) && holder.getCurrentSequence() <= 5 && holder.useSpirituality(300)) {
                    shootAcidicRain(player, level);
                    if (!player.getAbilities().instabuild) {
                        player.getCooldowns().addCooldown(this, 40);
                    }
                }
            }
        }
        return super.use(level, player, hand);
    }

    private static void shootAcidicRain(Player player, Level level) {
        if (!player.level().isClientSide()) {
            CompoundTag tag = player.getPersistentData();
            if (tag.getInt("sirenSongHarm") == 0) {
                tag.putInt("sirenSongHarm", 400);
                tag.putInt("ssParticleAttributeHelper", 400);
            }
            if (tag.getInt("sirenSongHarm") > 1 && tag.getInt("sirenSongHarm") < 400) {
                tag.putInt("sirenSongHarm", 0);
                tag.putInt("ssParticleAttributeHelper", 1);
            }
            if (tag.getInt("sirenSongWeaken") != 0) {
                tag.putInt("sirenSongWeaken", 0);
                tag.putInt("sirenSongHarm", 400);
                tag.putInt("ssParticleAttributeHelper", 400);

            }
            if (tag.getInt("sirenSongStun") != 0) {
                tag.putInt("sirenSongStun", 0);
                tag.putInt("sirenSongHarm", 400);

                tag.putInt("ssParticleAttributeHelper", 400);
            }
            if (tag.getInt("sirenSongStrengthen") != 0) {
                tag.putInt("sirenSongStrengthen", 0);
                tag.putInt("sirenSongHarm", 400);
                tag.putInt("ssParticleAttributeHelper", 400);
            }
        }
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
        if (entity instanceof Player player) {
            if (player.getAttribute(ModAttributes.PARTICLE_HELPER2.get()).getValue() == 1) {
                BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
                spawnParticlesInSphere(player, 50 - (holder.getCurrentSequence() * 6));
            }
        }
        super.inventoryTick(stack, level, entity, itemSlot, isSelected);
    }

    public static void spawnParticlesInSphere(Player player, int radius) {
        Level level = player.level();
        Random random = new Random();

        for (int i = 0; i < 20; i++) { // Adjust the number of particles as needed
            double x = player.getX() + (random.nextDouble() * 2 - 1) * radius;
            double y = player.getY() + (random.nextDouble() * 2 - 1) * radius;
            double z = player.getZ() + (random.nextDouble() * 2 - 1) * radius;

            // Check if the point is within the sphere
            if (isInsideSphere(player.getX(), player.getY(), player.getZ(), x, y, z, radius)) {
                double noteValue = random.nextInt(25) / 24.0;
                level.addParticle(ParticleTypes.NOTE, x, y, z, noteValue, 0, 0);
            }
        }
    }
    public static boolean isInsideSphere(double centerX, double centerY, double centerZ, double x, double y, double z, double radius) {
        double distance = Math.sqrt(
                Math.pow(x - centerX, 2) +
                        Math.pow(y - centerY, 2) +
                        Math.pow(z - centerZ, 2)
        );
        return distance <= radius;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            tooltipComponents.add(Component.literal("Upon use, start singing a song that causes harm to all entities around you\n" +
                    "Spirituality Used: 300\n" +
                    "Cooldown: 50 seconds").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE));
        }
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }


}