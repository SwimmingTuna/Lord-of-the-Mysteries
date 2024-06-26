package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
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
import net.swimmingtuna.lotm.init.ParticleInit;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AcidicRain extends Item {
    public AcidicRain(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        if (!level.isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
            if (holder != null) {
                if (!holder.isSailorClass()) {
                    pPlayer.displayClientMessage(Component.literal("You are not of the Sailor pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
                } else if (holder.getSpirituality() < 50) {
                    pPlayer.displayClientMessage(Component.literal("You need 50 spirituality in order to use this").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
                } else if (holder.isSailorClass() && holder.getCurrentSequence() <= 5 && holder.useSpirituality(100)) {
                    shootAcidicRain(pPlayer, level);
                    if (!pPlayer.getAbilities().instabuild) {
                        pPlayer.getCooldowns().addCooldown(this, 40);
                    }
                }
            }
        }
        return super.use(level, pPlayer, hand);
    }

    private static void shootAcidicRain(Player pPlayer, Level level) {
        pPlayer.getPersistentData().putInt("sailorAcidicRain", 1);
        AttributeInstance particleAttribute = pPlayer.getAttribute(ModAttributes.PARTICLE_HELPER.get());
        particleAttribute.setBaseValue(1);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, summons an acid rain effect around the player\n" +
                    "Spirituality Used: 50\n" +
                    "Cooldown: 2 seconds"));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }

    @SubscribeEvent
    public static void acidicRainTick(TickEvent.PlayerTickEvent event) {
        Player pPlayer = event.player;
        if (!pPlayer.level().isClientSide() && event.phase == TickEvent.Phase.START) {
            int acidicRain = pPlayer.getPersistentData().getInt("sailorAcidicRain");
            AttributeInstance particleAttribute = pPlayer.getAttribute(ModAttributes.PARTICLE_HELPER.get());
            if (acidicRain > 0 && particleAttribute.getValue() == 1) {
                pPlayer.getPersistentData().putInt("sailorAcidicRain", acidicRain + 1);
                BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
                int sequence = holder.getCurrentSequence();
                double radius1 = 50 - (sequence * 7);
                double radius2 = 10 - sequence;


                for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(radius1))) {
                    if (entity != pPlayer) {
                        if (entity.hasEffect(MobEffects.POISON)) {
                            int poisonAmp = entity.getEffect(MobEffects.POISON).getAmplifier();
                            if (poisonAmp == 0) {
                                entity.addEffect((new MobEffectInstance(MobEffects.POISON, 60, 1, false, false)));
                            }
                        } else entity.addEffect((new MobEffectInstance(MobEffects.POISON, 60, 1, false, false)));
                    }
                }
                for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(radius2))) {
                    if (entity != pPlayer) {
                        if (entity.hasEffect(MobEffects.POISON)) {
                            int poisonAmp = entity.getEffect(MobEffects.POISON).getAmplifier();
                            if (poisonAmp <= 2) {
                                entity.addEffect((new MobEffectInstance(MobEffects.POISON, 60, 2, false, false)));
                            }
                        } else entity.addEffect((new MobEffectInstance(MobEffects.POISON, 60, 2, false, false)));
                    }
                }


                if (acidicRain > 300) {
                    pPlayer.getPersistentData().putInt("sailorAcidicRain", 0);
                    particleAttribute.setBaseValue(0);
                }
            }
        }
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
        if (entity instanceof Player pPlayer) {
            double acidicRain = pPlayer.getAttributeBaseValue(ModAttributes.PARTICLE_HELPER.get());
            if (acidicRain >= 1) {
                spawnAcidicRainParticles(pPlayer);
            }
        }
        super.inventoryTick(stack, level, entity, itemSlot, isSelected);
    }


    private static void spawnAcidicRainParticles(Player pPlayer) {
        BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
        int sequence = holder.getCurrentSequence();
        double x = pPlayer.getX();
        double y = pPlayer.getY() + 5;
        double z = pPlayer.getZ();
        int maxRadius = 50 - (sequence * 7);
        int maxParticles = 500 - (sequence * 60);

        for (int i = 0; i < maxParticles; i++) {
            double dx = pPlayer.level().random.nextGaussian() * maxRadius;
            double dy = pPlayer.level().random.nextGaussian() * 2; // Adjust this value to control the vertical spread
            double dz = pPlayer.level().random.nextGaussian() * maxRadius;
            double distance = Math.sqrt(dx * dx + dz * dz);

            if (distance < maxRadius) {
                double density = 1.0 - (distance / maxRadius); // Calculate the density based on distance
                if (pPlayer.level().random.nextDouble() < density) {
                    pPlayer.level().addParticle(ParticleInit.ACIDRAIN_PARTICLE.get(), x + dx, y + dy, z + dz, 0, -3, 0);
                }
            }
        }
    }
}