package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class SirenSongStrengthen extends SimpleAbilityItem {

    public SirenSongStrengthen(Properties properties) {
        super(properties, BeyonderClassInit.SAILOR, 5, 300, 1000);
    }

    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        addCooldown(player);
        useSpirituality(player);
        sirenSongStrengthen(player, level);
        return InteractionResult.SUCCESS;
    }

    private static void sirenSongStrengthen(Player player, Level level) {
        CompoundTag tag = player.getPersistentData();
        if (tag.getInt("sirenSongStrengthen") == 0) {
            tag.putInt("sirenSongStrengthen", 400);
            tag.putInt("ssParticleAttributeHelper", 400);
        }
        if (tag.getInt("sirenSongStrengthen") > 1 && tag.getInt("sirenSongStrengthen") < 400) {
            tag.putInt("sirenSongStrengthen", 0);
            tag.putInt("ssParticleAttributeHelper", 1);
        }
        if (tag.getInt("sirenSongHarm") > 1) {
            tag.putInt("sirenSongHarm", 0);
            tag.putInt("sirenSongStrengthen", 400);
            tag.putInt("ssParticleAttributeHelper", 400);

        }
        if (tag.getInt("sirenSongWeaken") > 1) {
            tag.putInt("sirenSongWeaken", 0);
            tag.putInt("sirenSongStrengthen", 400);
            tag.putInt("ssParticleAttributeHelper", 400);
        }
        if (tag.getInt("sirenSongStun") > 1) {
            tag.putInt("sirenSongStun", 0);
            tag.putInt("sirenSongStrengthen", 400);
            tag.putInt("ssParticleAttributeHelper", 400);
        }
    }

    public static void spawnParticlesInSphere(Player player, int radius) {
        Level level = player.level();
        Random random = new Random();
        if (level instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 20; i++) { // Adjust the number of particles as needed
                double x = player.getX() + (random.nextDouble() * 2 - 1) * radius;
                double y = player.getY() + (random.nextDouble() * 2 - 1) * radius;
                double z = player.getZ() + (random.nextDouble() * 2 - 1) * radius;

                // Check if the point is within the sphere
                if (isInsideSphere(player.getX(), player.getY(), player.getZ(), x, y, z, radius)) {
                    float noteValue = (float) (random.nextInt(25) / 24.0);
                    BeyonderUtil.spawnParticlesInSphere(serverLevel, x,y,z, radius, 20, noteValue,0,0, ParticleTypes.NOTE);
                }
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
        tooltipComponents.add(Component.literal("Upon use, start singing a song that strengthens you by giving you additional strength to your melee attacks and a higher level of regeneration\n" +
                "Spirituality Used: 300\n" +
                "Cooldown: 50 seconds").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}