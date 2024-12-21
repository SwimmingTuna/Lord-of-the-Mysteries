package net.swimmingtuna.lotm.item.BeyonderAbilities.Monster;


import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RebootSelf extends SimpleAbilityItem {

    public RebootSelf(Properties properties) {
        super(properties, BeyonderClassInit.MONSTER, 1, 2000, 1200);
    }

    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        activateSpiritVision(player);
        if (!player.isShiftKeyDown()) {
            useSpirituality(player);
            addCooldown(player);
        }
        return InteractionResult.SUCCESS;
    }

    private void activateSpiritVision(Player player) {
        if (!player.level().isClientSide()) {
            if (player.isShiftKeyDown()) {
                saveDataReboot(player, player.getPersistentData());
                player.displayClientMessage(Component.literal("Saved State.").withStyle(ChatFormatting.GREEN), true);
            } else {
                restoreDataReboot(player, player.getPersistentData());
                player.displayClientMessage(Component.literal("Loaded State.").withStyle(ChatFormatting.GREEN), true);
            }
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, summons a colossal wave in the direction you're looking"));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("2000").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("1 Minute").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(SimpleAbilityItem.getPathwayText(this.requiredClass.get()));
        tooltipComponents.add(SimpleAbilityItem.getClassText(this.requiredSequence, this.requiredClass.get()));
        super.baseHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

    private static void saveDataReboot(Player player, CompoundTag tag) {
        Collection<MobEffectInstance> activeEffects = player.getActiveEffects();
        tag.putInt("monsterRebootPotionEffectsCount", activeEffects.size());
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        int i = 0;
        for (MobEffectInstance effect : activeEffects) {
            CompoundTag effectTag = new CompoundTag();
            effect.save(effectTag);
            tag.put("monsterRebootPotionEffect_" + i, effectTag);
            i++;
        }
        double luck = tag.getDouble("luck");
        double misfortune = tag.getDouble("misfortune");
        AttributeInstance sanity = player.getAttribute(ModAttributes.SANITY.get());
        double corruption = tag.getDouble("corruption");
        tag.putInt("monsterRebootLuck", (int) luck);
        tag.putInt("monsterRebootMisfortune", (int) misfortune);
        tag.putInt("monsterRebootSanity", (int) sanity.getValue());
        tag.putInt("monsterRebootCorruption", (int) corruption);
        tag.putInt("monsterRebootHealth", (int) player.getHealth());
        tag.putInt("monsterRebootSpirituality", holder.getCurrentSequence());
        List<Item> beyonderAbilities = BeyonderUtil.getAbilities(player);
        for (Item item : beyonderAbilities) {
            if (item != ItemInit.REBOOTSELF.get()) {
                String itemCooldowns = item.getDescription().toString();
                tag.putFloat("monsterRebootCooldown" + itemCooldowns, player.getCooldowns().getCooldownPercent(item, 0));
            }
        }
    }

    private static void restoreDataReboot(Player player, CompoundTag tag) {
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        for (MobEffectInstance activeEffect : new ArrayList<>(player.getActiveEffects())) {
            player.removeEffect(activeEffect.getEffect());
        }
        int effectCount = tag.getInt("monsterRebootPotionEffectsCount");
        for (int i = 0; i < effectCount; i++) {
            CompoundTag effectTag = tag.getCompound("monsterRebootPotionEffect_" + i);
            MobEffectInstance effect = MobEffectInstance.load(effectTag);
            if (effect != null) {
                player.addEffect(effect);
            }
        }
        double luck = tag.getDouble("luck");
        double misfortune = tag.getDouble("misfortune");
        AttributeInstance sanity = player.getAttribute(ModAttributes.SANITY.get());
        double corruption = tag.getDouble("corruption");
        player.getAttribute(ModAttributes.SANITY.get()).setBaseValue(Math.max(5, tag.getInt("monsterRebootSanity")));
        tag.putDouble("corruption", tag.getInt("monsterRebootCorruption"));
        tag.putDouble("luck", tag.getInt("monsterRebootLuck"));
        tag.putDouble("misfortune", tag.getInt("monsterRebootMisfortune"));
        holder.setSpirituality(tag.getInt("monsterRebootSpirituality"));
        player.setHealth(Math.max(1, player.getHealth()));
        List<Item> beyonderAbilities = BeyonderUtil.getAbilities(player);
        for (Item item : beyonderAbilities) {
            if (item instanceof SimpleAbilityItem simpleAbilityItem) {
                String itemCooldowns = item.getDescription().toString();
                float savedCooldownPercent = tag.getFloat("monsterRebootCooldown" + itemCooldowns);
                int remainingCooldownTicks = (int) (simpleAbilityItem.getCooldown() * savedCooldownPercent);
                player.getCooldowns().addCooldown(item, remainingCooldownTicks);
            }
        }
    }
}
