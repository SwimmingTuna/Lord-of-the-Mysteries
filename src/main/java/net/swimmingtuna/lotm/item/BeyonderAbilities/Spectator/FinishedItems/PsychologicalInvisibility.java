package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;


import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PsychologicalInvisibility extends Item  {

    public PsychologicalInvisibility(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        if (!pPlayer.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
            if (!holder.isSpectatorClass()) {
                pPlayer.displayClientMessage(Component.literal("You are not of the Spectator pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA), true);
            }
            if (holder.getSpirituality() < 75) {
                pPlayer.displayClientMessage(Component.literal("You need 75 spirituality in order to use this").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA), true);
            }
            BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
                if (holder.isSpectatorClass() && spectatorSequence.getCurrentSequence() <= 5 && spectatorSequence.useSpirituality(0)) {
                    storeAndReleaseArmor(pPlayer);
                    if (!pPlayer.getAbilities().instabuild)
                        pPlayer.getCooldowns().addCooldown(this, 240);
                }
            });
        }
        return super.use(level, pPlayer, hand);
    }

    private static void storeAndReleaseArmor(Player pPlayer) {
        CompoundTag tag = pPlayer.getPersistentData();
        boolean armorStored = tag.getBoolean("armorStored");

        if (!armorStored) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                    ItemStack armorPiece = pPlayer.getItemBySlot(slot);
                    if (!armorPiece.isEmpty()) {
                        ResourceLocation armorIdentifier = ForgeRegistries.ITEMS.getKey(armorPiece.getItem());
                        if (armorIdentifier != null) {
                            tag.putString(slot.getName() + "_armor", armorIdentifier.toString());
                            pPlayer.setItemSlot(slot, ItemStack.EMPTY);
                        }
                    }
                }
            }
            pPlayer.displayClientMessage(Component.literal("Armor stored."), true);
            tag.putBoolean("armorStored", true);
        } else {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                    String storedArmor = tag.getString(slot.getName() + "_armor");
                    if (!storedArmor.isEmpty()) {
                        ItemStack armorPiece = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(storedArmor)));
                        pPlayer.setItemSlot(slot, armorPiece);
                        tag.remove(slot.getName() + "_armor");
                    }
                }
            }
            pPlayer.displayClientMessage(Component.literal("Armor restored."), true);
            tag.putBoolean("armorStored", false);
        }
    }


    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, takes off your armor and turns you invisible while draining your spirituality, upon use again, turns you visible and gives you back your armor\n" +
                    "Spirituality Used: 40 every second\n" +
                    "Cooldown: 10 seconds").withStyle(ChatFormatting.AQUA));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }
}