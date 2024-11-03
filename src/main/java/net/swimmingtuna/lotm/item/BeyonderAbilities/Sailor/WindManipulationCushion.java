package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class WindManipulationCushion extends SimpleAbilityItem {

    public WindManipulationCushion(Properties properties) {
        super(properties, BeyonderClassInit.SAILOR, 7, 150, 120);
    }

    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        addCooldown(player);
        useSpirituality(player);
        windManipulationCushion(player);
        return InteractionResult.SUCCESS;
    }

    public static void windManipulationCushion(Player player) {
        player.getPersistentData().putInt("windManipulationCushion", 20);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, create a cushion of wind that absorbs your fall then sends you in the direction you're looking"));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }


    public static void summonWindCushionParticles(LivingEntity player) {
        if (player.level() instanceof ServerLevel serverLevel) {
            double x = player.getX() - player.getLookAngle().x * 2;
            double y = player.getY() + 1.5; // Slightly above the player's feet
            double z = player.getZ() - player.getLookAngle().z * 2;
            for (int i = 0; i < 10; i++) {
                serverLevel.sendParticles(ParticleTypes.CLOUD, x + serverLevel.random.nextDouble() - 0.5, y + serverLevel.random.nextDouble() - 0.5, z + serverLevel.random.nextDouble() - 0.5,0,0,0,0,0);
            }
        }
    }
}