package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)

public class ManipulateEmotion extends Item {
    public ManipulateEmotion(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!player.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            if (!holder.currentClassMatches(BeyonderClassInit.SPECTATOR)) {
                player.displayClientMessage(Component.literal("You are not of the Spectator pathway").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
            }
            if (holder.getSpirituality() < 500) {
                player.displayClientMessage(Component.literal("You need 500 spirituality in order to use this").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
            }
        }
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        AttributeInstance dreamIntoReality = player.getAttribute(ModAttributes.DIR.get());
        if (holder.currentClassMatches(BeyonderClassInit.SPECTATOR) && holder.getCurrentSequence() <= 4 && holder.useSpirituality(500)) {
            manipulateEmotion(player, holder.getCurrentSequence());
            if (!player.getAbilities().instabuild)
                player.getCooldowns().addCooldown(this, (int) (1200 / dreamIntoReality.getValue()));
        }
        return super.use(level, player, hand);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, all living entities able to be manipulated within 250 blocks fall into despair and harm themselves.\n" +
                "Left Click for Manipulate Fondness\n" +
                "Spirituality Used: 500\n" +
                "Cooldown: 1 minute").withStyle(ChatFormatting.AQUA));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

    private static void manipulateEmotion(Player player, int sequence) {
            float damage = 100 - (sequence * 10);
            for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(250))) {
                if (entity != player && entity.hasEffect(ModEffects.MANIPULATION.get())) {
                    entity.hurt(entity.damageSources().magic(), damage);
                    entity.removeEffect(ModEffects.MANIPULATION.get());
                }
            }
        }
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        Player player = event.getEntity();
        ItemStack heldItem = player.getMainHandItem();
        int activeSlot = player.getInventory().selected;
        if (!heldItem.isEmpty() && heldItem.getItem() instanceof ManipulateEmotion) {
            player.getInventory().setItem(activeSlot, new ItemStack(ItemInit.MANIPULATE_FONDNESS.get()));
            heldItem.shrink(1);
        }
    }
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();
        ItemStack heldItem = player.getMainHandItem();
        int activeSlot = player.getInventory().selected;
        if (!player.level().isClientSide && !heldItem.isEmpty() && heldItem.getItem() instanceof ManipulateEmotion) {
            player.getInventory().setItem(activeSlot, new ItemStack(ItemInit.MANIPULATE_FONDNESS.get()));
            heldItem.shrink(1);
        }
    }
}
