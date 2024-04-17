package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;


import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import org.jetbrains.annotations.NotNull;
import virtuoel.pehkui.api.*;
import virtuoel.pehkui.util.PehkuiEntityExtensions;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DreamIntoReality extends Item {
    private static final String CAN_FLY = "CanFly";
    private AtomicInteger spiritualityUseCounter;


    public DreamIntoReality(Properties pProperties) {
        super(pProperties);
        this.spiritualityUseCounter = new AtomicInteger(0);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        if (!pPlayer.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
            if (!holder.isSpectatorClass()) {
                pPlayer.displayClientMessage(Component.literal("You are not of the Spectator pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA), true);
            }
            if (holder.getSpirituality() < 50) {
                pPlayer.displayClientMessage(Component.literal("You need 300 spirituality in order to use this").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA), true);
            }
            BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
                if (holder.isSpectatorClass() && spectatorSequence.getCurrentSequence() <= 2) {
                    toggleFlying(pPlayer);
                    boolean canFly = pPlayer.getPersistentData().getBoolean(CAN_FLY);
                    if (canFly && !pPlayer.getAbilities().instabuild) {
                        pPlayer.getCooldowns().addCooldown(this, 300);
                    }
                }
            });
        }
        ItemStack itemStack = pPlayer.getItemInHand(hand);
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemStack);
    }

    private void toggleFlying(Player pPlayer) {
        BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
                boolean canFly = pPlayer.getPersistentData().getBoolean(CAN_FLY);
                if (canFly) {
                    stopFlying(pPlayer);
                } else {
                    startFlying(pPlayer);
                }
        });
    }

    private void startFlying(Player pPlayer) {
        AttributeInstance dreamIntoReality = pPlayer.getAttribute(ModAttributes.DIR.get());
        if (dreamIntoReality.getValue() != 3) {
        pPlayer.getPersistentData().putBoolean(CAN_FLY, true);
        Abilities playerAbilities = pPlayer.getAbilities();
        dreamIntoReality.setBaseValue(4);
        if (!playerAbilities.instabuild) {
        playerAbilities.mayfly = true;
        playerAbilities.flying = true;
        playerAbilities.setFlyingSpeed(0.2F);}
        ScaleData scaleData = ScaleTypes.BASE.getScaleData(pPlayer);
        scaleData.setTargetScale(scaleData.getBaseScale() * 4);
        scaleData.markForSync(true);
        pPlayer.onUpdateAbilities();
        if (pPlayer instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundPlayerAbilitiesPacket(playerAbilities));}}
    }

    private void stopFlying(Player pPlayer) {
        AttributeInstance dreamIntoReality = pPlayer.getAttribute(ModAttributes.DIR.get());
        pPlayer.getPersistentData().putBoolean(CAN_FLY, false);
        Abilities playerAbilities = pPlayer.getAbilities();
        CompoundTag compoundTag = pPlayer.getPersistentData();
        int mindscape = compoundTag.getInt("inMindscape");
        if (!playerAbilities.instabuild || mindscape >= 1) {
        playerAbilities.mayfly = false;
        playerAbilities.flying = false;}
        dreamIntoReality.setBaseValue(1);
        playerAbilities.setFlyingSpeed(0.05F);
        pPlayer.onUpdateAbilities();
        ScaleData scaleData = ScaleTypes.BASE.getScaleData(pPlayer);
        scaleData.setTargetScale(1);
        scaleData.markForSync(true);
        if (pPlayer instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundPlayerAbilitiesPacket(playerAbilities));
        }
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
        if (entity instanceof Player) {
            Player pPlayer = (Player) entity;
            if (!pPlayer.level().isClientSide) {
                boolean canFly = pPlayer.getPersistentData().getBoolean(CAN_FLY);

                final int[] counterValue = {spiritualityUseCounter.get()};

                if (canFly) {
                    BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
                        if (!pPlayer.getAbilities().instabuild) {
                            if (spectatorSequence.getSpirituality() > 300) {
                                counterValue[0]++;
                                if (counterValue[0] >= 20) {
                                    spectatorSequence.useSpirituality(301);
                                    counterValue[0] = 0;
                                }
                            } else {
                                stopFlying(pPlayer);
                                counterValue[0] = 0;
                            }
                        }
                    });
                }
                spiritualityUseCounter.set(counterValue[0]);
            }
        }
        super.inventoryTick(stack, level, entity, itemSlot, isSelected);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, makes your dreams into reality, turning into a giant with shorter cooldowns on abilities and more health as well as strengthed damage resistance, regeneration, and strength\n" +
                    "Spirituality Used: 300 every second\n" +
                    "Cooldown: 30 seconds"));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }
    @SubscribeEvent
    public static void tickEvent(TickEvent.PlayerTickEvent event) {
        Player pPlayer = event.player;
        boolean canFly = pPlayer.getPersistentData().getBoolean(CAN_FLY);
        if (!pPlayer.level().isClientSide) {
            BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
                if (canFly) {
                    if (spectatorSequence.getCurrentSequence() == 2) {
                        pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 2, false, false));
                        pPlayer.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 3, false, false));
                        pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, 4, false, false));
                    }
                    if (spectatorSequence.getCurrentSequence() == 1) {
                        pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 3, false, false));
                        pPlayer.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 3, false, false));
                        pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, 4, false, false));
                    }
                    if (spectatorSequence.getCurrentSequence() == 0) {
                        pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 3, false, false));
                        pPlayer.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 4, false, false));
                        pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, 5, false, false));
                    }
                }
            });
        }
    }
}