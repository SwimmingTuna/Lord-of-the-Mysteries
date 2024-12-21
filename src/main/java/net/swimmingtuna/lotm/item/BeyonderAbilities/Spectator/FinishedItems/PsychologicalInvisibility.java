package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;


import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class PsychologicalInvisibility extends SimpleAbilityItem {

    public PsychologicalInvisibility(Properties properties) {
        super(properties, BeyonderClassInit.SPECTATOR, 6, 0, 240);
    }

    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        storeAndReleaseArmor(player);
        if (!player.getPersistentData().getBoolean("armorStored")) {
            addCooldown(player);
        }
        return InteractionResult.SUCCESS;
    }

    private static void storeAndReleaseArmor(Player player) {
        if (!player.level().isClientSide()) {
            CompoundTag tag = player.getPersistentData();
            boolean armorStored = tag.getBoolean("armorStored");

            if (!armorStored) {
                for (Mob mob : player.level().getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(50))) {
                    if (mob.getTarget() == player) {
                        mob.setTarget(null);
                    }
                }
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
    }


    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, hypnotize all entities around you to hide your presence. While this is active, both you and your armor will be hidden."));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("2% of your max spirituality per second").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("12 Seconds").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(SimpleAbilityItem.getPathwayText(this.requiredClass.get()));
        tooltipComponents.add(SimpleAbilityItem.getClassText(this.requiredSequence, this.requiredClass.get()));
        super.baseHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}