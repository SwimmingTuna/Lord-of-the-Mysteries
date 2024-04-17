package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
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
            if (!holder.isSailorClass()) {
                pPlayer.displayClientMessage(Component.literal("You are not of the Sailor pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
            }
            if (holder.getSpirituality() < 20) {
                pPlayer.displayClientMessage(Component.literal("You need 20 spirituality in order to use this").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
            }
            BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(tyrantSequence -> {
                if (holder.isSailorClass() && tyrantSequence.getCurrentSequence() <= 8 && tyrantSequence.useSpirituality(20)) {
                    ragingBlows(pPlayer);
                }
                if (!pPlayer.getAbilities().instabuild)
                    pPlayer.getCooldowns().addCooldown(this, 200);
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
            componentList.add(Component.literal("Upon use, causes the user to shoot punches powerfully all around the user, damaging everything around them\n" +
                    "Spirituality Used: 20\n" +
                    "Cooldown: 10 seconds"));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }
    @SubscribeEvent
    public static void blowsCounter(TickEvent.PlayerTickEvent event) {
        Player pPlayer = event.player;
        if (!pPlayer.level().isClientSide() && event.phase == TickEvent.Phase.START) {
            CompoundTag persistentData = pPlayer.getPersistentData();
            boolean sailorLightning = persistentData.getBoolean("SailorLightning");
            BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(tyrantSequence -> {
                int ragingBlows = persistentData.getInt("ragingBlows");
                int radius = (45 - (tyrantSequence.getCurrentSequence() * 5));
                int damage = (int) (20 - tyrantSequence.getCurrentSequence() * 1.5);
                if (ragingBlows >= 1) {
                    persistentData.putInt("ragingBlows", ragingBlows + 1);
                    pPlayer.sendSystemMessage(Component.literal("blows is" + ragingBlows));
                }
                if (ragingBlows == 8 || ragingBlows == 16 || ragingBlows == 24 || ragingBlows == 32 || ragingBlows == 40 || ragingBlows == 48 || ragingBlows == 56 || ragingBlows == 64 || ragingBlows == 72 || ragingBlows == 80 || ragingBlows == 88 || ragingBlows == 96 || ragingBlows == 4 || ragingBlows == 12 || ragingBlows == 20 || ragingBlows == 28 || ragingBlows == 36 || ragingBlows == 44 || ragingBlows == 52 || ragingBlows == 60 || ragingBlows == 68 || ragingBlows == 76 || ragingBlows == 84 || ragingBlows == 92) {

                }
                if (ragingBlows == 8) {
                    pPlayer.level().playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 0.5F, 0.5F);
                    for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(radius))) {
                        if (entity != pPlayer) {
                            entity.hurt(entity.damageSources().generic(), damage);
                            double x = pPlayer.getX() - entity.getX();
                            double z = pPlayer.getZ() - entity.getZ();
                            entity.knockback(0.25, x, z);
                            if (tyrantSequence.getCurrentSequence() <= 7) {
                                double chanceOfDamage = (100.0 - (tyrantSequence.getCurrentSequence() * 12.5));
                                if (Math.random() * 100 < chanceOfDamage) {
                                    LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, entity.level());
                                    lightningBolt.hurt(entity.damageSources().lightningBolt(), 5);
                                    pPlayer.sendSystemMessage(Component.literal("working!"));
                                }
                            }
                        }
                    }
                }
                if (ragingBlows == 16) {
                    pPlayer.level().playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 0.5F, 0.5F);
                    for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(radius))) {
                        if (entity != pPlayer) {
                            entity.hurt(entity.damageSources().generic(), damage);
                            double x = pPlayer.getX() - entity.getX();
                            double z = pPlayer.getZ() - entity.getZ();
                            entity.knockback(0.25, x, z);
                            if (tyrantSequence.getCurrentSequence() <= 7) {
                                double chanceOfDamage = (100.0 - (tyrantSequence.getCurrentSequence() * 12.5));
                                if (Math.random() * 100 < chanceOfDamage && sailorLightning) {
                                    LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, entity.level());
                                    lightningBolt.hurt(entity.damageSources().lightningBolt(), 5);
                                    pPlayer.sendSystemMessage(Component.literal("working!"));
                                }
                            }
                        }
                    }
                }
                if (ragingBlows == 24) {
                    pPlayer.level().playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 0.5F, 0.5F);
                    for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(radius))) {
                        if (entity != pPlayer) {
                            entity.hurt(entity.damageSources().generic(), damage);
                            double x = pPlayer.getX() - entity.getX();
                            double z = pPlayer.getZ() - entity.getZ();
                            entity.knockback(0.25, x, z);
                            if (tyrantSequence.getCurrentSequence() <= 7) {
                                double chanceOfDamage = (100.0 - (tyrantSequence.getCurrentSequence() * 12.5));
                                if (Math.random() * 100 < chanceOfDamage && sailorLightning) {
                                    LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, entity.level());
                                    lightningBolt.hurt(entity.damageSources().lightningBolt(), 5);
                                    pPlayer.sendSystemMessage(Component.literal("working!"));
                                }
                            }
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
                            if (tyrantSequence.getCurrentSequence() <= 7) {
                                double chanceOfDamage = (100.0 - (tyrantSequence.getCurrentSequence() * 12.5));
                                if (Math.random() * 100 < chanceOfDamage && sailorLightning) {
                                    LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, entity.level());
                                    lightningBolt.hurt(entity.damageSources().lightningBolt(), 5);
                                    pPlayer.sendSystemMessage(Component.literal("working!"));
                                }
                            }
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
                            if (tyrantSequence.getCurrentSequence() <= 7) {
                                double chanceOfDamage = (100.0 - (tyrantSequence.getCurrentSequence() * 12.5));
                                if (Math.random() * 100 < chanceOfDamage && sailorLightning) {
                                    LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, entity.level());
                                    lightningBolt.hurt(entity.damageSources().lightningBolt(), 5);
                                    pPlayer.sendSystemMessage(Component.literal("working!"));
                                }
                            }
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
                            if (tyrantSequence.getCurrentSequence() <= 7) {
                                double chanceOfDamage = (100.0 - (tyrantSequence.getCurrentSequence() * 12.5));
                                if (Math.random() * 100 < chanceOfDamage && sailorLightning) {
                                    LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, entity.level());
                                    lightningBolt.hurt(entity.damageSources().lightningBolt(), 5);
                                    pPlayer.sendSystemMessage(Component.literal("working!"));
                                }
                            }
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
                            if (tyrantSequence.getCurrentSequence() <= 7) {
                                double chanceOfDamage = (100.0 - (tyrantSequence.getCurrentSequence() * 12.5));
                                if (Math.random() * 100 < chanceOfDamage && sailorLightning) {
                                    LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, entity.level());
                                    lightningBolt.hurt(entity.damageSources().lightningBolt(), 5);
                                    pPlayer.sendSystemMessage(Component.literal("working!"));
                                }
                            }
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
                            if (tyrantSequence.getCurrentSequence() <= 7) {
                                double chanceOfDamage = (100.0 - (tyrantSequence.getCurrentSequence() * 12.5));
                                if (Math.random() * 100 < chanceOfDamage && sailorLightning) {
                                    LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, entity.level());
                                    lightningBolt.hurt(entity.damageSources().lightningBolt(), 5);
                                    pPlayer.sendSystemMessage(Component.literal("working!"));
                                }
                            }
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
                            if (tyrantSequence.getCurrentSequence() <= 7) {
                                double chanceOfDamage = (100.0 - (tyrantSequence.getCurrentSequence() * 12.5));
                                if (Math.random() * 100 < chanceOfDamage && sailorLightning) {
                                    LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, entity.level());
                                    lightningBolt.hurt(entity.damageSources().lightningBolt(), 5);
                                    pPlayer.sendSystemMessage(Component.literal("working!"));
                                }
                            }
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
                            if (tyrantSequence.getCurrentSequence() <= 7) {
                                double chanceOfDamage = (100.0 - (tyrantSequence.getCurrentSequence() * 12.5));
                                if (Math.random() * 100 < chanceOfDamage && sailorLightning) {
                                    LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, entity.level());
                                    lightningBolt.hurt(entity.damageSources().lightningBolt(), 5);
                                    pPlayer.sendSystemMessage(Component.literal("working!"));
                                }
                            }
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
                            if (tyrantSequence.getCurrentSequence() <= 7) {
                                double chanceOfDamage = (100.0 - (tyrantSequence.getCurrentSequence() * 12.5));
                                if (Math.random() * 100 < chanceOfDamage && sailorLightning) {
                                    LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, entity.level());
                                    lightningBolt.hurt(entity.damageSources().lightningBolt(), 5);
                                    pPlayer.sendSystemMessage(Component.literal("working!"));
                                }
                            }
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
                            if (tyrantSequence.getCurrentSequence() <= 7) {
                                double chanceOfDamage = (100.0 - (tyrantSequence.getCurrentSequence() * 12.5));
                                if (Math.random() * 100 < chanceOfDamage && sailorLightning) {
                                    LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, entity.level());
                                    lightningBolt.hurt(entity.damageSources().lightningBolt(), 5);
                                    pPlayer.sendSystemMessage(Component.literal("working!"));
                                }
                            }
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
}