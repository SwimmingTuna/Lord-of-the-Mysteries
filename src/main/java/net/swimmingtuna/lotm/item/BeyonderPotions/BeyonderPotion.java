package net.swimmingtuna.lotm.item.BeyonderPotions;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
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
    private final List<Integer> effectDurations = List.of(5000, 2500, 1800, 1200, 1000, 600, 400, 300, 180, 120);

    public BeyonderPotion(Properties properties, Supplier<? extends BeyonderClass> beyonderClassSupplier, int sequence) {
        super(properties);
        this.beyonderClassSupplier = beyonderClassSupplier;
        this.sequence = sequence;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (level.isClientSide) return InteractionResultHolder.pass(itemStack);
        player.getPersistentData().putInt("failedPotion",1);
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        if (holder.getCurrentClass() != null && holder.getCurrentClass() != beyonderClassSupplier.get()) {
            return InteractionResultHolder.fail(itemStack);
        }
        if (holder.getCurrentSequence() != -1 && holder.getCurrentSequence() < sequence) {
            return InteractionResultHolder.fail(itemStack);
        }
        MobEffectInstance blindnessEffect = new MobEffectInstance(MobEffects.BLINDNESS, effectDurations.get(sequence), 1);
        blindnessEffect.setCurativeItems(List.of());
        MobEffectInstance slownessEffect = new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, effectDurations.get(sequence), 4);
        slownessEffect.setCurativeItems(List.of());
        MobEffectInstance nauseaEffect = new MobEffectInstance(MobEffects.CONFUSION, effectDurations.get(sequence), 1);
        nauseaEffect.setCurativeItems(List.of());
        MobEffectInstance poisonEffect = new MobEffectInstance(MobEffects.POISON, effectDurations.get(sequence), 20);
        poisonEffect.setCurativeItems(List.of());
        player.addEffect(blindnessEffect);
        player.addEffect(slownessEffect);
        player.addEffect(nauseaEffect);
        player.addEffect(poisonEffect);
        holder.setClassAndSequence(beyonderClassSupplier.get(), sequence);
        level.playSound(null, player.getOnPos(), SoundEvents.PORTAL_AMBIENT, SoundSource.PLAYERS, 0.5f, level.random.nextFloat() * 0.1F + 0.9F);
        player.sendSystemMessage(Component.translatable("item.lotm.beholder_potion.alert", holder.getCurrentClass().sequenceNames().get(holder.getCurrentSequence())).withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.BOLD));
        player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(beyonderClassSupplier.get().maxHealth().get(sequence));
        if (!player.getAbilities().instabuild) {
            itemStack.shrink(1);
        }
        CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer) player, itemStack);

        return super.use(level, player, hand);
    }
}
