package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.ParticleInit;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class AcidicRain extends Item {
    public AcidicRain(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        if (!level.isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
            if (holder == null) {
                return super.use(level, pPlayer, hand);
            }
            if (!holder.currentClassMatches(BeyonderClassInit.SAILOR)) {
                pPlayer.displayClientMessage(Component.literal("You are not of the Sailor pathway").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
            } else if (holder.getSpirituality() < 175) {
                pPlayer.displayClientMessage(Component.literal("You need 175 spirituality in order to use this").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
            } else if (holder.currentClassMatches(BeyonderClassInit.SAILOR) && holder.getCurrentSequence() <= 5 && holder.useSpirituality(175)) {
                shootAcidicRain(pPlayer);
                if (!pPlayer.getAbilities().instabuild) {
                    pPlayer.getCooldowns().addCooldown(this, 500);
                }
            }
        }
        return super.use(level, pPlayer, hand);
    }

    private static void shootAcidicRain(Player pPlayer) {
        pPlayer.getPersistentData().putInt("sailorAcidicRain", 1);
        AttributeInstance particleAttribute = pPlayer.getAttribute(ModAttributes.PARTICLE_HELPER.get());
        particleAttribute.setBaseValue(1);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, summons an acidic rain around the player that persists for 15 seconds\n" +
                    "Spirituality Used: 175\n" +
                    "Cooldown: 25 seconds").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
        if (entity instanceof Player pPlayer) {
            double acidicRain = pPlayer.getAttributeBaseValue(ModAttributes.PARTICLE_HELPER.get());
            if (acidicRain >= 1) {
                spawnAcidicRainParticles(pPlayer);
            }
        }
        super.inventoryTick(stack, level, entity, itemSlot, isSelected);
    }


    private static void spawnAcidicRainParticles(Player pPlayer) {
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
        if (holder == null) {
            return;
        }
        int sequence = holder.getCurrentSequence();
        double x = pPlayer.getX();
        double y = pPlayer.getY() + 5;
        double z = pPlayer.getZ();
        int maxRadius = 50 - (sequence * 7);
        int maxParticles = 500 - (sequence * 60);

        for (int i = 0; i < maxParticles; i++) {
            double dx = pPlayer.level().random.nextGaussian() * maxRadius;
            double dy = pPlayer.level().random.nextGaussian() * 2; // Adjust this value to control the vertical spread
            double dz = pPlayer.level().random.nextGaussian() * maxRadius;
            double distance = Math.sqrt(dx * dx + dz * dz);

            if (distance < maxRadius) {
                double density = 1.0 - (distance / maxRadius); // Calculate the density based on distance
                if (pPlayer.level().random.nextDouble() < density) {
                    pPlayer.level().addParticle(ParticleInit.ACIDRAIN_PARTICLE.get(), x + dx, y + dy, z + dz, 0, -3, 0);
                }
            }
        }
    }
}