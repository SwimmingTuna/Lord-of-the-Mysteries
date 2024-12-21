package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.PlayerMobEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class RagingBlows extends SimpleAbilityItem {

    public RagingBlows(Properties properties) {
        super(properties, BeyonderClassInit.SAILOR, 8, 45, 300);
    }

    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        ragingBlows(player);
        addCooldown(player);
        useSpirituality(player);
        return InteractionResult.SUCCESS;
    }

    public static void ragingBlows(Player player) {
        if (!player.level().isClientSide()) {
            CompoundTag persistentData = player.getPersistentData();
            int ragingBlows = persistentData.getInt("ragingBlows");
            persistentData.putInt("ragingBlows", 1);
            ragingBlows = 1;
        }
    }


    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, unleash a fury of blows in the direction you're looking for a period of time"));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("45").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("15 Seconds").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(getPathwayText(this.requiredClass.get()));
        tooltipComponents.add(getClassText(this.requiredSequence, this.requiredClass.get()));
        super.baseHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

    public static void spawnRagingBlowsParticles(Player player) {
        if (player.level() instanceof ServerLevel serverLevel) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            Vec3 playerPos = player.position();
            Vec3 playerLookVector = player.getViewVector(1.0F);
            int radius = (27 - (holder.getCurrentSequence() * 3));
            CompoundTag persistentData = player.getPersistentData();
            int particleCounter = persistentData.getInt("ragingBlowsParticleCounter");

            if (particleCounter < 7) {
                double randomDistance = Math.random() * radius;
                Vec3 randomOffset = playerLookVector.scale(randomDistance);

                // Add random horizontal offset
                double randomHorizontalOffset = Math.random() * Math.PI * 2; // Random angle between 0 and 2π
                randomOffset = randomOffset.add(new Vec3(Math.cos(randomHorizontalOffset) * radius / 4, 0, Math.sin(randomHorizontalOffset) * radius / 4));

                // Add random vertical offset
                double randomVerticalOffset = Math.random() * Math.PI / 2 - Math.PI / 4; // Random angle between -π/4 and π/4
                randomOffset = randomOffset.add(new Vec3(0, Math.sin(randomVerticalOffset) * radius / 4, 0));

                double randomX = playerPos.x + randomOffset.x;
                double randomY = playerPos.y + randomOffset.y;
                double randomZ = playerPos.z + randomOffset.z;

                // Check if the random offset vector is in front of the player
                if (playerLookVector.dot(randomOffset) > 0) {
                    serverLevel.sendParticles(ParticleTypes.EXPLOSION, randomX,randomY, randomZ, 0, 0,0,0,0);
                }

                particleCounter++;
                persistentData.putInt("ragingBlowsParticleCounter", particleCounter);
            } else {
                persistentData.putInt("ragingBlowsParticleCounter", 0);
            }
        }
    }
    public static void spawnRagingBlowsParticlesPM(PlayerMobEntity player) {
        if (player.level() instanceof ServerLevel serverLevel) {
            Vec3 playerPos = player.position();
            Vec3 playerLookVector = player.getViewVector(1.0F);
            int radius = (25 - (player.getCurrentSequence() * 3));
            CompoundTag persistentData = player.getPersistentData();
            int particleCounter = persistentData.getInt("ragingBlowsParticleCounter");

            if (particleCounter < 7) {
                double randomDistance = Math.random() * radius;
                Vec3 randomOffset = playerLookVector.scale(randomDistance);

                // Add random horizontal offset
                double randomHorizontalOffset = Math.random() * Math.PI * 2; // Random angle between 0 and 2π
                randomOffset = randomOffset.add(new Vec3(Math.cos(randomHorizontalOffset) * radius / 4, 0, Math.sin(randomHorizontalOffset) * radius / 4));

                // Add random vertical offset
                double randomVerticalOffset = Math.random() * Math.PI / 2 - Math.PI / 4; // Random angle between -π/4 and π/4
                randomOffset = randomOffset.add(new Vec3(0, Math.sin(randomVerticalOffset) * radius / 4, 0));

                double randomX = playerPos.x + randomOffset.x;
                double randomY = playerPos.y + randomOffset.y;
                double randomZ = playerPos.z + randomOffset.z;

                // Check if the random offset vector is in front of the player
                if (playerLookVector.dot(randomOffset) > 0) {
                    serverLevel.sendParticles(ParticleTypes.EXPLOSION, randomX,randomY, randomZ, 0, 0,0,0,0);
                }

                particleCounter++;
                persistentData.putInt("ragingBlowsParticleCounter", particleCounter);
            } else {
                persistentData.putInt("ragingBlowsParticleCounter", 0);
            }
        }
    }
}