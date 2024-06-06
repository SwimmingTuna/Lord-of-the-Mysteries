package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
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
            persistentData.putInt("rbParticleHelper", 0);
            pPlayer.getAttribute(ModAttributes.PARTICLE_HELPER1.get()).setBaseValue(1);
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
        if (!pPlayer.level().isClientSide() && event.phase == TickEvent.Phase.END) {
            CompoundTag persistentData = pPlayer.getPersistentData();
            boolean sailorLightning = persistentData.getBoolean("SailorLightning");
            BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(tyrantSequence -> {
                int ragingBlows = persistentData.getInt("ragingBlows");
                int radius = (25 - (tyrantSequence.getCurrentSequence() * 3));
                int damage = (int) (20 - tyrantSequence.getCurrentSequence() * 2);
                if (ragingBlows >= 1) {
                    persistentData.putInt("ragingBlows", ragingBlows + 1);
                }
                if (ragingBlows == 6 || ragingBlows == 12 || ragingBlows == 18 || ragingBlows == 24 || ragingBlows == 30 || ragingBlows == 36 || ragingBlows == 42 ||
                        ragingBlows == 48 || ragingBlows == 54 || ragingBlows == 60 || ragingBlows == 66 || ragingBlows == 72 || ragingBlows == 78 ||
                        ragingBlows == 84 || ragingBlows == 90 || ragingBlows == 96) {
                    pPlayer.level().playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 0.5F, 0.5F);
                    Vec3 playerLookVector = pPlayer.getViewVector(1.0F);
                    Vec3 playerPos = pPlayer.position();
                    for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, new AABB(playerPos.x - radius, playerPos.y - radius, playerPos.z - radius, playerPos.x + radius, playerPos.y + radius, playerPos.z + radius))) {
                        if (entity != pPlayer && playerLookVector.dot(entity.position().subtract(playerPos)) > 0) {
                            entity.hurt(entity.damageSources().generic(), damage);
                            double x = pPlayer.getX() - entity.getX();
                            double z = pPlayer.getZ() - entity.getZ();
                            entity.knockback(0.25, x, z);
                            if (tyrantSequence.getCurrentSequence() <= 7) {
                                double chanceOfDamage = (100.0 - (tyrantSequence.getCurrentSequence() * 12.5));
                                if (Math.random() * 100 < chanceOfDamage && sailorLightning) {
                                    LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, entity.level());
                                    lightningBolt.moveTo(entity.getX(), entity.getY(), entity.getZ());
                                    entity.level().addFreshEntity(lightningBolt);
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
    @SubscribeEvent
    public static void ragingBlowsParticles(TickEvent.PlayerTickEvent event) {
        Player pPlayer = event.player;
        if (!pPlayer.level().isClientSide() && event.phase == TickEvent.Phase.END) {
            CompoundTag tag = pPlayer.getPersistentData();
            int x = tag.getInt("rbParticleHelper");
            Vec3 playerLookVector = pPlayer.getViewVector(1.0F);
            Vec3 playerPos = pPlayer.position();
            AttributeInstance particleHelper = pPlayer.getAttribute(ModAttributes.PARTICLE_HELPER1.get());
            if (particleHelper.getBaseValue() == 1) {
                tag.putInt("rbParticleHelper", x + 1);
            }
            if (x >= 100) {
                tag.putInt("rbParticleHelper", 0);
                x = 0;
                particleHelper.setBaseValue(0);
            }
            if (particleHelper.getBaseValue() == 0) {
                tag.putInt("rbParticleHelper", 0);
                x = 0;
            }
        }
    }
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
        if (entity instanceof Player pPlayer) {
            double ragingBlows = pPlayer.getAttributeBaseValue(ModAttributes.PARTICLE_HELPER1.get());
            if (ragingBlows >= 1) {
                spawnRagingBlowsParticles(pPlayer);
            }
        }
        super.inventoryTick(stack, level, entity, itemSlot, isSelected);
    }
    private static void spawnRagingBlowsParticles(Player pPlayer) {
        BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
        Vec3 playerPos = pPlayer.position();
        Vec3 playerLookVector = pPlayer.getViewVector(1.0F);
        BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(tyrantSequence -> {
            int radius = (25 - (tyrantSequence.getCurrentSequence() * 3));
            CompoundTag persistentData = pPlayer.getPersistentData();
            int particleCounter = persistentData.getInt("ragingBlowsParticleCounter");

            if (particleCounter < 7) {
                double randomDistance = Math.random() * radius;
                Vec3 randomOffset = playerLookVector.scale(randomDistance);

                // Add random horizontal offset
                double randomHorizontalOffset = Math.random() * Math.PI * 2; // Random angle between 0 and 2π
                randomOffset = randomOffset.add(new Vec3(Math.cos(randomHorizontalOffset) * radius / 4, 0, Math.sin(randomHorizontalOffset) * radius / 4));

                // Add random vertical offset
                double randomVerticalOffset = Math.random() * Math.PI / 2 - Math.PI / 4; // Random angle between -π/4 and π/4
                randomOffset = randomOffset.add(new Vec3(0, Math.sin(randomVerticalOffset) * radius / 4, 0));

                double randomX = playerPos.x + randomOffset.x;
                double randomY = playerPos.y + randomOffset.y;
                double randomZ = playerPos.z + randomOffset.z;

                // Check if the random offset vector is in front of the player
                if (playerLookVector.dot(randomOffset) > 0) {
                    pPlayer.level().addParticle(ParticleTypes.EXPLOSION, randomX, randomY, randomZ, 0, 0, 0);
                }

                particleCounter++;
                persistentData.putInt("ragingBlowsParticleCounter", particleCounter);
            } else {
                persistentData.putInt("ragingBlowsParticleCounter", 0);
            }
        });
    }
}