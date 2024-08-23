package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.TornadoEntity;
import net.swimmingtuna.lotm.events.ReachChangeUUIDs;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Hurricane extends Item implements ReachChangeUUIDs {

    public Hurricane(Properties pProperties) { //IMPORTANT!!!! FIGURE OUT HOW TO MAKE THIS WORK BY CLICKING ON A
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        if (!pPlayer.level().isClientSide()) {

            // If no block or entity is targeted, proceed with the original functionality
            BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
            if (!holder.isSailorClass()) {
                pPlayer.displayClientMessage(Component.literal("You are not of the Sailor pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
            }
            if (holder.getSpirituality() < 1500) {
                pPlayer.displayClientMessage(Component.literal("You need 300 spirituality in order to use this").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
            }
            BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(sailorSequence -> {
                if (holder.isSailorClass() && sailorSequence.getCurrentSequence() <= 5 && sailorSequence.useSpirituality(1500)) {
                    pPlayer.getPersistentData().putInt("sailorHurricane", 600);
                    if (!pPlayer.getAbilities().instabuild)
                        pPlayer.getCooldowns().addCooldown(this, 240);
                }
            });
        }
        return super.use(level, pPlayer, hand);
    }
    @SubscribeEvent
    public static void hurricaneTick (TickEvent.PlayerTickEvent event) {
        Player pPlayer = event.player;
        if (!pPlayer.level().isClientSide() && event.phase == TickEvent.Phase.END) {
            CompoundTag tag = pPlayer.getPersistentData();
            boolean x = tag.getBoolean("sailorHurricaneRain");
            BlockPos pos = new BlockPos((int) (pPlayer.getX() + (Math.random() * 100 - 100)), (int) (pPlayer.getY() - 100), (int) (pPlayer.getZ() + (Math.random() * 300 - 300)));
            int hurricane = tag.getInt("sailorHurricane");
            if (hurricane >= 1) {
                if (x) {
                    tag.putInt("sailorHurricane", hurricane - 1);

                    if (hurricane == 600) {
                        if (pPlayer.level() instanceof ServerLevel serverLevel) {
                            serverLevel.setWeatherParameters(0, 700, true, true);
                        }
                    }
                    if (hurricane % 5 == 0) {
                        SailorLightning.shootLineBlockHigh(pPlayer, pPlayer.level());
                    }
                    if (hurricane == 600 || hurricane == 300) {
                        for (int i = 0; i < 5; i++) {
                            TornadoEntity tornado = new TornadoEntity(pPlayer.level(), pPlayer, 0, 0, 0);
                            tornado.teleportTo(pos.getX(), pos.getY() + 100, pos.getZ());
                            tornado.setTornadoRandom(true);
                            tornado.setTornadoHeight(300);
                            tornado.setTornadoRadius(30);
                            tornado.setTornadoPickup(false);
                            pPlayer.level().addFreshEntity(tornado);
                        }
                    }
                }
                if (!x) {
                    if (pPlayer.level() instanceof ServerLevel serverLevel) {
                        serverLevel.setWeatherParameters(0, 700, true, false);
                    }
                }
            }
        }
    }
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        if (!heldItem.isEmpty() && heldItem.getItem() instanceof Hurricane) {
            CompoundTag tag = pPlayer.getPersistentData();
            boolean x = tag.getBoolean("sailorHurricaneRain");
            if (x) {
                pPlayer.displayClientMessage(Component.literal("Hurricane will only cause rain").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
                tag.putBoolean("sailorHurricaneRain", false);
                x = false;
            }
            if (!x) {
                pPlayer.displayClientMessage(Component.literal("Hurricane cause lightning, tornadoes, and rain").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
                tag.putBoolean("sailorHurricaneRain", true);
                x = true;
            }
            event.setCanceled(true);
        }
    }
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        int activeSlot = pPlayer.getInventory().selected;
        if (!pPlayer.level().isClientSide && !heldItem.isEmpty() && heldItem.getItem() instanceof Hurricane) {
            event.setCanceled(true);
        }
    }
}
