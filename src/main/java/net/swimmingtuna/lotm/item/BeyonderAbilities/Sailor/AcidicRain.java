package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.ParticleInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class AcidicRain extends SimpleAbilityItem {

    public AcidicRain(Properties properties) {
        super(properties, BeyonderClassInit.SAILOR, 5, 175, 500);
    }

    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        acidicRain(player);
        return InteractionResult.SUCCESS;
    }

    private static void acidicRain(Player player) {
        player.getPersistentData().putInt("sailorAcidicRain", 1);
        AttributeInstance particleAttribute = player.getAttribute(ModAttributes.PARTICLE_HELPER.get());
        particleAttribute.setBaseValue(1);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, summons an acidic rain around the player that persists for 15 seconds\n" +
                "Spirituality Used: 175\n" +
                "Cooldown: 25 seconds").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
        if (entity instanceof Player player) {
            double acidicRain = player.getAttributeBaseValue(ModAttributes.PARTICLE_HELPER.get());
            if (acidicRain >= 1) {
                spawnAcidicRainParticles(player);
            }
        }
        super.inventoryTick(stack, level, entity, itemSlot, isSelected);
    }


    public static void spawnAcidicRainParticles(Player player) {
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        int sequence = holder.getCurrentSequence();
        double x = player.getX();
        double y = player.getY() + 5;
        double z = player.getZ();
        int maxRadius = 50 - (sequence * 7);
        int maxParticles = 500 - (sequence * 60);

        for (int i = 0; i < maxParticles; i++) {
            double dx = player.level().random.nextGaussian() * maxRadius;
            double dy = player.level().random.nextGaussian() * 2; // Adjust this value to control the vertical spread
            double dz = player.level().random.nextGaussian() * maxRadius;
            double distance = Math.sqrt(dx * dx + dz * dz);

            if (distance < maxRadius) {
                double density = 1.0 - (distance / maxRadius); // Calculate the density based on distance
                if (player.level().random.nextDouble() < density) {
                    player.level().addParticle(ParticleInit.ACIDRAIN_PARTICLE.get(), x + dx, y + dy, z + dz, 0, -3, 0);
                }
            }
        }
    }
}