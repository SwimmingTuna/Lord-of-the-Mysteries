package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;


import net.minecraft.ChatFormatting;
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
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PsychologicalInvisibility extends Item  {

    public PsychologicalInvisibility(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!player.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            if (!holder.currentClassMatches(BeyonderClassInit.SPECTATOR)) {
                player.displayClientMessage(Component.literal("You are not of the Spectator pathway").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
            }
            if (holder.getSpirituality() < 75) {
                player.displayClientMessage(Component.literal("You need 75 spirituality in order to use this").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
            }
            if (holder.currentClassMatches(BeyonderClassInit.SPECTATOR) && holder.getCurrentSequence() <= 5 && holder.useSpirituality(0)) {
                storeAndReleaseArmor(player);
                if (!player.getAbilities().instabuild)
                    player.getCooldowns().addCooldown(this, 240);
            }
        }
        return super.use(level, player, hand);
    }

    private static void storeAndReleaseArmor(Player player) {
        CompoundTag tag = player.getPersistentData();
        boolean armorStored = tag.getBoolean("armorStored");

        if (!armorStored) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                    ItemStack armorPiece = player.getItemBySlot(slot);
                    if (!armorPiece.isEmpty()) {
                        ResourceLocation armorIdentifier = ForgeRegistries.ITEMS.getKey(armorPiece.getItem());
                        if (armorIdentifier != null) {
                            tag.putString(slot.getName() + "_armor", armorIdentifier.toString());
                            player.setItemSlot(slot, ItemStack.EMPTY);
                        }
                    }
                }
            }
            player.displayClientMessage(Component.literal("Armor stored."), true);
            tag.putBoolean("armorStored", true);
        } else {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                    String storedArmor = tag.getString(slot.getName() + "_armor");
                    if (!storedArmor.isEmpty()) {
                        ItemStack armorPiece = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(storedArmor)));
                        player.setItemSlot(slot, armorPiece);
                        tag.remove(slot.getName() + "_armor");
                    }
                }
            }
            player.displayClientMessage(Component.literal("Armor restored."), true);
            tag.putBoolean("armorStored", false);
        }
    }


    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, takes off your armor and turns you invisible while draining your spirituality, upon use again, turns you visible and gives you back your armor\n" +
                "Spirituality Used: 40 every second\n" +
                "Cooldown: 10 seconds").withStyle(ChatFormatting.AQUA));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}