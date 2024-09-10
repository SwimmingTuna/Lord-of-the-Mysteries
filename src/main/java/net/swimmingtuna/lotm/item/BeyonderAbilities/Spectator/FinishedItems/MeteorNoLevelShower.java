package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;


import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
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
import net.swimmingtuna.lotm.entity.MeteorNoLevelEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.ItemInit;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MeteorNoLevelShower extends Item {

    public MeteorNoLevelShower(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!player.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            if (!holder.currentClassMatches(BeyonderClassInit.SPECTATOR)) {
                player.displayClientMessage(Component.literal("You are not of the Spectator pathway").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
            }
            if (holder.getSpirituality() < 2500) {
                player.displayClientMessage(Component.literal("You need 2500 spirituality in order to use this").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
            }

            if (holder.currentClassMatches(BeyonderClassInit.SPECTATOR) && holder.getCurrentSequence() <= 1 && holder.useSpirituality(2500)) {
                meteorShower(player);
                if (!player.getAbilities().instabuild)
                    player.getCooldowns().addCooldown(this, 900);
            }
        }
        return super.use(level, player, hand);
    }

    public static void meteorShower(Player player) {
        MeteorNoLevelEntity.summonMultipleMeteors(player);
        MeteorNoLevelEntity.summonMultipleMeteors(player);
        MeteorNoLevelEntity.summonMultipleMeteors(player);
        MeteorNoLevelEntity.summonMultipleMeteors(player);
        MeteorNoLevelEntity.summonMultipleMeteors(player);
        MeteorNoLevelEntity.summonMultipleMeteors(player);
        MeteorNoLevelEntity.summonMultipleMeteors(player);
        MeteorNoLevelEntity.summonMultipleMeteors(player);
        MeteorNoLevelEntity.summonMultipleMeteors(player);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, summons a meteor shower\n" +
                "Spirituality Used: 1500\n" +
                "Left Click for a version that deals block destruction\n" +
                "Cooldown: 45 seconds").withStyle(ChatFormatting.AQUA));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        Player player = event.getEntity();
        ItemStack heldItem = player.getMainHandItem();
        int activeSlot = player.getInventory().selected;
        if (!heldItem.isEmpty() && heldItem.getItem() instanceof MeteorNoLevelShower) {
            player.getInventory().setItem(activeSlot, new ItemStack(ItemInit.METEOR_SHOWER.get()));
            heldItem.shrink(1);
        }
    }

    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();
        ItemStack heldItem = player.getMainHandItem();
        int activeSlot = player.getInventory().selected;
        if (!player.level().isClientSide && !heldItem.isEmpty() && heldItem.getItem() instanceof MeteorNoLevelShower) {
            player.getInventory().setItem(activeSlot, new ItemStack(ItemInit.METEOR_SHOWER.get()));
            heldItem.shrink(1);
        }
    }
}
