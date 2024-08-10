package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.TornadoEntity;
import net.swimmingtuna.lotm.events.ReachChangeUUIDs;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.effect.ModEffects;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SonicBoom extends Item implements ReachChangeUUIDs {

    public SonicBoom(Properties pProperties) { //IMPORTANT!!!! FIGURE OUT HOW TO MAKE THIS WORK BY CLICKING ON A
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
            if (holder.getSpirituality() < 300) {
                pPlayer.displayClientMessage(Component.literal("You need 300 spirituality in order to use this").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
            }
            BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(sailorSequence -> {
                if (holder.isSailorClass() && sailorSequence.getCurrentSequence() <= 4 && sailorSequence.useSpirituality(300)) {
                    sonicBoom(pPlayer, holder.getCurrentSequence());
                    if (!pPlayer.getAbilities().instabuild)
                        pPlayer.getCooldowns().addCooldown(this, 240);
                }
            });
        }
        return super.use(level, pPlayer, hand);
    }

    public static void sonicBoom(Player pPlayer, int sequence) {
        if (!pPlayer.level().isClientSide()) {
            pPlayer.level().explode(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), 40 - (sequence * 5), Level.ExplosionInteraction.TNT);
            for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(40 - (sequence * 8)))) {
                if (entity != pPlayer) {
                    entity.getPersistentData().putInt("sailorSonicBoom", 5);
                    int duration = 100 - (sequence * 20);
                    int damage = 25 - (sequence * 5);
                    if (!(entity instanceof Player)) {
                        entity.addEffect((new MobEffectInstance(ModEffects.AWE.get(), duration, 1, false, false)));
                        entity.hurt(entity.damageSources().generic(), damage);
                    } else if ((entity instanceof Player player)) {
                        BeyonderHolder holder1 = BeyonderHolderAttacher.getHolder(player).orElse(null);
                        int pSequence = holder1.getCurrentSequence();
                        int pDuration = duration - (50 - (pSequence * 5));
                        int pDamage = (int) (damage - (8 - (pSequence * 0.5)));
                        entity.addEffect((new MobEffectInstance(ModEffects.AWE.get(), pDuration, 1, false, false)));
                        entity.hurt(entity.damageSources().generic(), pDamage);
                    }
                }
            }
            pPlayer.getPersistentData().putInt("sailorSonicBoom", 10);
            pPlayer.getPersistentData().putInt("sonicBoomBlocksDestroyed", 15);
        }
    }

    @SubscribeEvent
    public static void sonicBoomCancel(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.getPersistentData().getInt("sailorSonicBoom") >= 1) {
            DamageSource source = event.getSource();
            if ("explosion".equals(source.getMsgId()) || "explosion.player".equals(source.getMsgId())) {
                event.setCanceled(true);
                System.out.println("worked");
            }
        }
    }

    @SubscribeEvent
    public static void tickEvent(LivingEvent.LivingTickEvent event) {
        LivingEntity pPlayer = event.getEntity();
        if (!pPlayer.level().isClientSide()) {
            int x = pPlayer.getPersistentData().getInt("sailorSonicBoom");
            if (x >= 1) {
                pPlayer.getPersistentData().putInt("sailorSonicBoom", x - 1);
            }
        }
    }
    @SubscribeEvent
    public static void test(LivingKnockBackEvent event) {

    }

    @SubscribeEvent
    public static void sonicBoomTick(TickEvent.PlayerTickEvent event) {
        Player pPlayer = event.player;
        BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);

        if (!pPlayer.level().isClientSide() && event.phase == TickEvent.Phase.END) {
            CompoundTag tag = pPlayer.getPersistentData();
            AttributeInstance particleHelper = pPlayer.getAttribute(ModAttributes.PARTICLE_HELPER3.get());
            int sonicBoomCounter = tag.getInt("sailorSonicBoom");
            if (sonicBoomCounter >= 1) {
                particleHelper.setBaseValue(1.0);
                tag.putInt("sonicBookCounter", sonicBoomCounter - 1);
                Vec3 lookVector = pPlayer.getLookAngle();
                pPlayer.setDeltaMovement(lookVector.x * 100, lookVector.y * 100, lookVector.z * 100);
                pPlayer.hurtMarked = true;
                int sequence = holder.getCurrentSequence();
                int destroyed = pPlayer.getPersistentData().getInt("sonicBoomBlocksDestroyed");
                if (sonicBoomCounter % 3 == 0) {
                    for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(20 - (sequence * 3)))) {
                        if (entity != pPlayer) {
                            entity.getPersistentData().putInt("sailorSonicBoom", 5);
                            int duration = 100 - (sequence * 20);
                            int damage = 25 - (sequence * 5);
                            if (!(entity instanceof Player)) {
                                entity.addEffect((new MobEffectInstance(ModEffects.AWE.get(), duration, 1, false, false)));
                                entity.hurt(entity.damageSources().generic(), damage);
                            } else if ((entity instanceof Player player)) {
                                BeyonderHolder holder1 = BeyonderHolderAttacher.getHolder(player).orElse(null);
                                int pSequence = holder1.getCurrentSequence();
                                int pDuration = duration - (50 - (pSequence * 5));
                                int pDamage = (int) (damage - (8 - (pSequence * 0.5)));
                                entity.addEffect((new MobEffectInstance(ModEffects.AWE.get(), pDuration, 1, false, false)));
                                entity.hurt(entity.damageSources().generic(), pDamage);
                            }
                        }
                    }
                }
            }
            if (sonicBoomCounter <= 1) {
                if (particleHelper.getBaseValue() == 1) {
                    particleHelper.setBaseValue(0);
                }
                pPlayer.getPersistentData().putInt("sonicBoomBlocksDestroyed", 0);

            }
            if (sonicBoomCounter == 29 || sonicBoomCounter == 1) {
                int sequence = holder.getCurrentSequence();
                pPlayer.level().explode(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), 40 - (sequence * 5), Level.ExplosionInteraction.TNT);
            }
        }
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
        if (entity instanceof Player pPlayer) {
            double explosionParticles = pPlayer.getAttributeBaseValue(ModAttributes.PARTICLE_HELPER3.get());
            if (explosionParticles == 1) {
                spawnExplosionParticles(pPlayer);
                BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
            }
        }
        super.inventoryTick(stack, level, entity, itemSlot, isSelected);
    }

    private void spawnExplosionParticles(Player pPlayer) {
        RandomSource random = pPlayer.level().getRandom();
        BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);

        double radius = 40 - (holder.getCurrentSequence() * 8); // Adjust this based on your sequence calculation
        int particleCount = 20; // Total number of particles to spawn

        for (int i = 0; i < particleCount; i++) {
            // Generate random spherical coordinates
            double theta = random.nextDouble() * 2 * Math.PI;
            double phi = Math.acos(2 * random.nextDouble() - 1);
            double r = radius * Math.cbrt(random.nextDouble()); // Cube root for uniform distribution in sphere

            // Convert spherical to Cartesian coordinates
            double x = r * Math.sin(phi) * Math.cos(theta);
            double y = r * Math.sin(phi) * Math.sin(theta);
            double z = r * Math.cos(phi);

            // Player's position
            Vec3 pos = pPlayer.position();

            // Spawn particles
            if (i % 2 == 0) {
                // Spark particle
                pPlayer.level().addParticle(ParticleTypes.ELECTRIC_SPARK,
                        pos.x + x, pos.y + y, pos.z + z,
                        0, 0, 0);
            } else {
                // Explosion particle
                pPlayer.level().addParticle(ParticleTypes.EXPLOSION,
                        pos.x + x, pos.y + y, pos.z + z,
                        0, 0, 0);
            }
        }
    }
}
