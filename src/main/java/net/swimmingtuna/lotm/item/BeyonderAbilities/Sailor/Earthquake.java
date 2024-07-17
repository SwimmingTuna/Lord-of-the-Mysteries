package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.AqueousLightEntityPull;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Earthquake extends Item {
    public Earthquake(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        if (!pPlayer.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
            if (!holder.isSailorClass()) {
                pPlayer.displayClientMessage(Component.literal("You are not of the Sailor pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
            }
            if (holder.getSpirituality() < 75) {
                pPlayer.displayClientMessage(Component.literal("You need 75 spirituality in order to use this").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
            }

        BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(tyrantSequence -> {
            if (holder.isSailorClass() && tyrantSequence.getCurrentSequence() <= 4 && tyrantSequence.useSpirituality(75)) {
                shootLight(pPlayer);
            }
            if (!pPlayer.getAbilities().instabuild)
                pPlayer.getCooldowns().addCooldown(this, 60);

        });
            }
        return super.use(level, pPlayer, hand);
    }

    public static void shootLight(Player pPlayer) {
        pPlayer.getPersistentData().putInt("sailorEarthquake", 200);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, shoots a projectile that upon hit, pulls the target towards the user\n" +
                    "Spirituality Used: 75\n" +
                    "Cooldown: 3 seconds"));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }
    @SubscribeEvent
    public static void earthquakeTick(TickEvent.PlayerTickEvent event) {
        Player pPlayer = event.player;
        BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
        if (event.phase == TickEvent.Phase.END && !pPlayer.level().isClientSide()) {
            int x = pPlayer.getPersistentData().getInt("sailorEarthquake");
            if (x == 200 || x == 180 || x == 160 || x == 140 || x == 120 || x == 100 || x == 80 || x == 60 || x == 40 || x == 20) {
                int sequence = holder.getCurrentSequence();
                pPlayer.getPersistentData().putInt("sailorEarthquake", x - 1);
                for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate((200 - (sequence * 30))))) {
                    if (entity != pPlayer) {
                        if (entity.onGround()) {
                            entity.hurt(pPlayer.damageSources().fall(), 35 - (sequence * 5));
                        }
                    }}
            }

        }
    }
}