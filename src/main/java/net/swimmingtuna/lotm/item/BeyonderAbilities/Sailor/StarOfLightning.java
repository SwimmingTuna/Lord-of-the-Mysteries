package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class StarOfLightning extends Item {

    public StarOfLightning(Properties pProperties) { //IMPORTANT!!!! FIGURE OUT HOW TO MAKE THIS WORK BY CLICKING ON A
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        if (!pPlayer.level().isClientSide()) {

            // If no block or entity is targeted, proceed with the original functionality
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
            if (!holder.currentClassMatches(BeyonderClassInit.SAILOR)) {
                pPlayer.displayClientMessage(Component.literal("You are not of the Sailor pathway").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
            }
            if (holder.getSpirituality() < 3000) {
                pPlayer.displayClientMessage(Component.literal("You need 3000 spirituality in order to use this").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
            }
            BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(sailorSequence -> {
                if (holder.currentClassMatches(BeyonderClassInit.SAILOR) && sailorSequence.getCurrentSequence() <= 1 && sailorSequence.useSpirituality(3000)) {
                    summonLightning(pPlayer);
                    if (!pPlayer.getAbilities().instabuild)
                        pPlayer.getCooldowns().addCooldown(this, 800);
                }
            });
        }
        return super.use(level, pPlayer, hand);
    }

    private static void summonLightning(Player pPlayer) {
        if (!pPlayer.level().isClientSide()) {
            pPlayer.getPersistentData().putInt("sailorLightningStar", 40);
        }
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
        if (entity instanceof Player pPlayer) {
            AttributeInstance attributeInstance = pPlayer.getAttribute(ModAttributes.PARTICLE_HELPER4.get());
            if (attributeInstance != null && attributeInstance.getValue() == 1) {
                for (int i = 0; i < 500; i++) {
                    double offsetX = (Math.random() * 5) - 2.5;
                    double offsetY = (Math.random() * 5) - 2.5;
                    double offsetZ = (Math.random() * 5) - 2.5;
                    if (Math.sqrt(offsetX * offsetX + offsetY * offsetY + offsetZ * offsetZ) <= 2.5) {
                        level.addParticle(ParticleTypes.ELECTRIC_SPARK,
                                pPlayer.getX() + offsetX,
                                pPlayer.getY() + offsetY,
                                pPlayer.getZ() + offsetZ,
                                0.0, 0.0, 0.0);
                    }
                }
            }
            if (!pPlayer.level().isClientSide()) {
                pPlayer.level().playSound(pPlayer, pPlayer.getOnPos(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 10,1);
            }
        }
        super.inventoryTick(stack, level, entity, itemSlot, isSelected);
    }
    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, gathers lightning in your body before letting it out in every direction\n" +
                    "Spirituality Used: 3000\n" +
                    "Cooldown: 40 seconds").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }
}
