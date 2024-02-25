package net.swimmingtuna.lotm.item.custom.BeyonderPotions;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.beyonder.api.BeyonderClass;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;

import java.util.List;
import java.util.function.Supplier;

public class BeyonderPotion extends Item {
    private final Supplier<? extends BeyonderClass> beyonderClassSupplier;
    private final int sequence;
    private final List<Integer> effectDurations = List.of(5000,2500,1800,1200,1000,600,400,300,180,120);

    public BeyonderPotion(Properties properties, Supplier<? extends BeyonderClass> beyonderClassSupplier, int sequence) {
        super(properties);
        this.beyonderClassSupplier = beyonderClassSupplier;
        this.sequence = sequence;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        ItemStack itemStack = pPlayer.getItemInHand(hand);
        if (level.isClientSide) return InteractionResultHolder.pass(itemStack);
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
        if (holder == null) return InteractionResultHolder.fail(itemStack);
        if (holder.getCurrentClass()!=null && holder.getCurrentClass() != beyonderClassSupplier.get()) return InteractionResultHolder.fail(itemStack);
        if (holder.getCurrentSequence() < sequence) return InteractionResultHolder.fail(itemStack);
        var effect = new MobEffectInstance(MobEffects.BLINDNESS,effectDurations.get(sequence),1);
        effect.setCurativeItems(List.of());
        var effect1 = new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,effectDurations.get(sequence),4);
        effect1.setCurativeItems(List.of());
        var effect2 = new MobEffectInstance(MobEffects.CONFUSION,effectDurations.get(sequence),1);
        effect2.setCurativeItems(List.of());
        var effect3 = new MobEffectInstance(MobEffects.POISON,effectDurations.get(sequence),20);
        effect3.setCurativeItems(List.of());
        pPlayer.addEffect(effect);
        pPlayer.addEffect(effect1);
        pPlayer.addEffect(effect2);
        pPlayer.addEffect(effect3);
        holder.setClassAndSequence(beyonderClassSupplier.get(),sequence);
        level.playSound(null, pPlayer.getOnPos(), SoundEvents.PORTAL_AMBIENT, SoundSource.PLAYERS, 0.5f, level.random.nextFloat() * 0.1F + 0.9F);
        pPlayer.sendSystemMessage(Component.translatable("item.lotm.beholder_potion.alert", holder.getCurrentClass().sequenceNames().get(holder.getCurrentSequence())).withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.BOLD));
        pPlayer.getAttribute(Attributes.MAX_HEALTH).setBaseValue(beyonderClassSupplier.get().maxHealth().get(sequence));
        if (!pPlayer.getAbilities().instabuild) {
            itemStack.shrink(1);
        }
        CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer) pPlayer, itemStack);

        return super.use(level, pPlayer, hand);
    }

}
