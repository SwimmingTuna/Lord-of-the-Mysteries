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
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SirenSongStrengthen extends Item {
    public SirenSongStrengthen(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        if (!level.isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
            if (holder != null) {
                if (!holder.isSailorClass()) {
                    pPlayer.displayClientMessage(Component.literal("You are not of the Sailor pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
                } else if (holder.getSpirituality() < 300) {
                    pPlayer.displayClientMessage(Component.literal("You need 300 spirituality in order to use this").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
                } else if (holder.isSailorClass() && holder.getCurrentSequence() <= 5 && holder.useSpirituality(300)) {
                    shootAcidicRain(pPlayer, level);
                    if (!pPlayer.getAbilities().instabuild) {
                        pPlayer.getCooldowns().addCooldown(this, 1000);
                    }
                }
            }
        }
        return super.use(level, pPlayer, hand);
    }

    private static void shootAcidicRain(Player pPlayer, Level level) {
        CompoundTag tag = pPlayer.getPersistentData();
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
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
        if (entity instanceof Player pPlayer) {
            if (pPlayer.getAttribute(ModAttributes.PARTICLE_HELPER2.get()).getValue() == 1) {
                BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
                spawnParticlesInSphere(pPlayer, 50 - (holder.getCurrentSequence() * 6));
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
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, start singing a song that strengthens you by giving you additional strength to your melee attacks and a higher level of regeneration\n" +
                    "Spirituality Used: 300\n" +
                    "Cooldown: 50 seconds").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }
}