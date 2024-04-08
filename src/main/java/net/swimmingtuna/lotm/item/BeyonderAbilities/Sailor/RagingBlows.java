package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;
import net.swimmingtuna.lotm.LOTM;

import net.swimmingtuna.lotm.beyonder.SailorClass;
import net.swimmingtuna.lotm.beyonder.SpectatorClass;
import net.swimmingtuna.lotm.beyonder.api.BeyonderClass;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RagingBlows extends Item {
    public RagingBlows(Properties pProperties) {
        super(pProperties);
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        if (!pPlayer.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
            BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(tyrantSequence -> {
                if (holder.isTyrantClass() && tyrantSequence.getCurrentSequence() <= 8 && tyrantSequence.useSpirituality(20)) {
                    ragingBlows(pPlayer);
                }
                if (!pPlayer.getAbilities().instabuild)
                    pPlayer.getCooldowns().addCooldown(this, 10);
            });
        }
        return super.use(level, pPlayer, hand);
    }

    public static void ragingBlows(Player pPlayer) {
        if (!pPlayer.level().isClientSide()) {
            CompoundTag persistentData = pPlayer.getPersistentData();
            int ragingBlows = persistentData.getInt("ragingBlows");
            persistentData.putInt("ragingBlows", 1);
            ragingBlows = 1;
        }
        }
    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, shoots a dragons breath\n" +
                    "Spirituality Used: 100\n" +
                    "Cooldown: 0.5 seconds"));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }
    @SubscribeEvent
    public static void blowsCounter(TickEvent.PlayerTickEvent event) {
        Player pPlayer = event.player;
        CompoundTag persistentData = pPlayer.getPersistentData();
        BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(tyrantSequence -> {
            int ragingBlows = persistentData.getInt("ragingBlows");
        int radius = (45 - (tyrantSequence.getCurrentSequence() * 5));
        int damage = (int) (20 - tyrantSequence.getCurrentSequence() * 1.5);
        if (ragingBlows >= 1) {
            persistentData.putInt("ragingBlows", ragingBlows + 1);
        }
        if (ragingBlows == 8) {
            pPlayer.level().playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 0.5F, 0.5F);
            for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(radius))) {
                if (entity != pPlayer) {
                    entity.hurt(entity.damageSources().generic(),damage);
                    double x = pPlayer.getX() - entity.getX();
                    double z = pPlayer.getZ() - entity.getZ();
                    entity.knockback(0.25, x, z);
                }
             }
          }
            if (ragingBlows == 16) {
                pPlayer.level().playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 0.5F, 0.5F);
                for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(radius))) {
                    if (entity != pPlayer) {
                        entity.hurt(entity.damageSources().generic(),damage);
                        double x = pPlayer.getX() - entity.getX();
                        double z = pPlayer.getZ() - entity.getZ();
                        entity.knockback(0.25, x, z);
                    }
                }
            }
            if (ragingBlows == 24) {
                pPlayer.level().playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 0.5F, 0.5F);
                for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(radius))) {
                    if (entity != pPlayer) {
                        entity.hurt(entity.damageSources().generic(),damage);
                        double x = pPlayer.getX() - entity.getX();
                        double z = pPlayer.getZ() - entity.getZ();
                        entity.knockback(0.25, x, z);
                    }
                }
            }
            if (ragingBlows == 32) {
                pPlayer.level().playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 0.5F, 0.5F);
                for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(radius))) {
                    if (entity != pPlayer) {
                        entity.hurt(entity.damageSources().generic(), damage);
                        double x = pPlayer.getX() - entity.getX();
                        double z = pPlayer.getZ() - entity.getZ();
                        entity.knockback(0.25, x, z);
                    }
                    }
                }
                if (ragingBlows == 40) {
                    pPlayer.level().playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 0.5F, 0.5F);
                    for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(radius))) {
                        if (entity != pPlayer) {
                            entity.hurt(entity.damageSources().generic(), damage);
                            double x = pPlayer.getX() - entity.getX();
                            double z = pPlayer.getZ() - entity.getZ();
                            entity.knockback(0.25, x, z);
                        }
                    }
                }
                    if (ragingBlows == 48) {
                        pPlayer.level().playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 0.5F, 0.5F);
                        for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(radius))) {
                            if (entity != pPlayer) {
                                entity.hurt(entity.damageSources().generic(), damage);
                                double x = pPlayer.getX() - entity.getX();
                                double z = pPlayer.getZ() - entity.getZ();
                                entity.knockback(0.25, x, z);
                            }
                        }
                    }
                    if (ragingBlows == 56) {
                        pPlayer.level().playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 0.5F, 0.5F);
                        for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(radius))) {
                            if (entity != pPlayer) {
                                entity.hurt(entity.damageSources().generic(), damage);
                                double x = pPlayer.getX() - entity.getX();
                                double z = pPlayer.getZ() - entity.getZ();
                                entity.knockback(0.25, x, z);
                            }
                        }
                    }
                    if (ragingBlows == 64) {
                        pPlayer.level().playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 0.5F, 0.5F);
                        for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(radius))) {
                            if (entity != pPlayer) {
                                entity.hurt(entity.damageSources().generic(), damage);
                                double x = pPlayer.getX() - entity.getX();
                                double z = pPlayer.getZ() - entity.getZ();
                                entity.knockback(0.25, x, z);
                            }
                        }
                    }
                    if (ragingBlows == 72) {
                        pPlayer.level().playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 0.5F, 0.5F);
                        for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(radius))) {
                            if (entity != pPlayer) {
                                entity.hurt(entity.damageSources().generic(), damage);
                                double x = pPlayer.getX() - entity.getX();
                                double z = pPlayer.getZ() - entity.getZ();
                                entity.knockback(0.25, x, z);
                            }
                        }
                    }
                    if (ragingBlows == 80) {
                        pPlayer.level().playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 0.5F, 0.5F);
                        for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(radius))) {
                            if (entity != pPlayer) {
                                entity.hurt(entity.damageSources().generic(), damage);
                                double x = pPlayer.getX() - entity.getX();
                                double z = pPlayer.getZ() - entity.getZ();
                                entity.knockback(0.25, x, z);
                            }
                        }
                    }
                    if (ragingBlows == 88) {
                        pPlayer.level().playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 0.5F, 0.5F);
                        for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(radius))) {
                            if (entity != pPlayer) {
                                entity.hurt(entity.damageSources().generic(), damage);
                                double x = pPlayer.getX() - entity.getX();
                                double z = pPlayer.getZ() - entity.getZ();
                                entity.knockback(0.25, x, z);
                            }
                        }
                    }
                    if (ragingBlows == 96) {
                        pPlayer.level().playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 0.5F, 0.5F);
                        for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(radius))) {
                            if (entity != pPlayer) {
                                entity.hurt(entity.damageSources().generic(), damage);
                                double x = pPlayer.getX() - entity.getX();
                                double z = pPlayer.getZ() - entity.getZ();
                                entity.knockback(0.25, x, z);
                            }
                        }
                    }
                    if (ragingBlows >= 100) {
                        ragingBlows = 0;
                        persistentData.putInt("ragingBlows", 0);
                    }
         });
    }
}