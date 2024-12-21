package net.swimmingtuna.lotm.item.SealedArtifacts;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.providers.score.ScoreboardNameProvider;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.DeathKnellBulletEntity;
import net.swimmingtuna.lotm.entity.PlayerMobEntity;
import net.swimmingtuna.lotm.init.EntityInit;

import java.util.Random;

public class DeathKnell extends Item {
    public DeathKnell(Properties pProperties) {
        super(pProperties.stacksTo(1));
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack stack = pPlayer.getItemInHand(pHand);
        if (!pLevel.isClientSide() && pPlayer.getMainHandItem().getItem() instanceof DeathKnell) {
            pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 0.5F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F));
            summonDeathKnellBullet(pPlayer);
            pPlayer.getCooldowns().addCooldown(this, 60);
        }
        return InteractionResultHolder.sidedSuccess(stack, pLevel.isClientSide());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
        if (entity instanceof Player player) {
            if (player.tickCount % 2 == 0 && !level.isClientSide()) {
                if (player.getMainHandItem().getItem() instanceof DeathKnell) {
                    player.displayClientMessage(Component.literal("Death Knell Selection is: " + player.getPersistentData().getInt("deathKnell")).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD), true);
                }
            }
        }
        super.inventoryTick(stack, level, entity, itemSlot, isSelected);
    }

    public static void summonDeathKnellBullet(LivingEntity livingEntity) {
        if (!livingEntity.level().isClientSide()) {
            CompoundTag tag = livingEntity.getPersistentData();
            int x = tag.getInt("deathKnell");

            DeathKnellBulletEntity deathKnellBulletEntity = new DeathKnellBulletEntity(EntityInit.DEATH_KNELL_BULLET_ENTITY.get(), livingEntity.level());
            deathKnellBulletEntity.teleportTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());

            // Particle and sound effects based on x
            Level level = livingEntity.level();
            int particleCount;
            float soundPitch;

            if (x <= 1) {
                particleCount = 20;
                soundPitch = 1.0F;
                if (livingEntity instanceof Player pPlayer) {
                    BeyonderHolderAttacher.getHolderUnwrap(pPlayer).useSpirituality(100);
                } else if (livingEntity instanceof PlayerMobEntity playerMobEntity) {
                    playerMobEntity.useSpirituality(100);
                }
                deathKnellBulletEntity.setDamage(15);
                deathKnellBulletEntity.setWeakness(true);
            } else if (x == 2) {
                particleCount = 30;
                soundPitch = 0.8F;
                deathKnellBulletEntity.setDamage(30);
                deathKnellBulletEntity.setLethal(true);
                if (livingEntity instanceof Player pPlayer) {
                    BeyonderHolderAttacher.getHolderUnwrap(pPlayer).useSpirituality(200);
                } else if (livingEntity instanceof PlayerMobEntity playerMobEntity) {
                    playerMobEntity.useSpirituality(200);
                }
            } else {
                particleCount = 50;
                soundPitch = 0.6F;
                if (livingEntity instanceof Player pPlayer) {
                    BeyonderHolderAttacher.getHolderUnwrap(pPlayer).useSpirituality(300);
                } else if (livingEntity instanceof PlayerMobEntity playerMobEntity) {
                    playerMobEntity.useSpirituality(300);
                }
                deathKnellBulletEntity.setDamage(35);
                deathKnellBulletEntity.setLethal(true);
            }
            double spread = Math.PI / 4;
            if (level instanceof ServerLevel serverLevel) {
                for (int i = 0; i < particleCount; i++) {
                    double yaw = livingEntity.getYRot() * (Math.PI / 180.0);
                    double pitch = livingEntity.getXRot() * (Math.PI / 180.0);
                    double randomYaw = yaw + (Math.random() - 0.5) * spread;
                    double randomPitch = pitch + (Math.random() - 0.5) * spread;
                    double speed = 0.5 + Math.random() * 0.5;
                    double vx = -Math.sin(randomYaw) * Math.cos(randomPitch) * speed;
                    double vy = -Math.sin(randomPitch) * speed;
                    double vz = Math.cos(randomYaw) * Math.cos(randomPitch) * speed;

                    serverLevel.sendParticles(ParticleTypes.SMOKE, livingEntity.getX(), livingEntity.getY() + 1.5, livingEntity.getZ(), 1, vx, vy, vz, 0.0);
                }
                serverLevel.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1.0F, soundPitch);
            }
            deathKnellBulletEntity.shootFromRotation(livingEntity, livingEntity.getXRot(), livingEntity.getYRot(), 0.0F, 10.0F, 0.0F);
            deathKnellBulletEntity.setOwner(livingEntity);
            livingEntity.level().addFreshEntity(deathKnellBulletEntity);
            if (livingEntity instanceof Player player) {
                if (!player.isCreative()) {
                    giveNegativeEffect(livingEntity);
                }
            } else {
                giveNegativeEffect(livingEntity);
            }
        }
    }

    public static void giveNegativeEffect(LivingEntity livingEntity) {
        CompoundTag tag = livingEntity.getPersistentData();
        int sequence = 0;
        if (livingEntity instanceof Player pPlayer) {
            sequence = BeyonderHolderAttacher.getHolderUnwrap(pPlayer).getCurrentSequence();
        } else if (livingEntity instanceof PlayerMobEntity playerMobEntity) {
            sequence = playerMobEntity.getCurrentSequence();
        }
        Random random = new Random();
        int choice = random.nextInt(6);
        switch (choice) {
            case 0:
                if (sequence <= 4) {
                    tag.putInt("deathKnellFireFear", 500);
                } else {
                    tag.putInt("deathKnellFireFear", 250);
                }
                break;
            case 1:
                if (sequence <= 4) {
                    tag.putInt("deathKnellMobFear", 500);
                } else {
                    tag.putInt("deathKnellMobFear", 250);
                }
                break;
            case 2:
                if (sequence <= 4) {
                    tag.putInt("deathKnellPeacefulFear", 500);
                } else {
                    tag.putInt("deathKnellPeacefulFear", 250);
                }
                break;
            case 3:
                if (sequence <= 4) {
                    tag.putInt("deathKnellPlayerFear", 500);
                } else {
                    tag.putInt("deathKnellPlayerFear", 250);
                }
                break;
            case 4:
                if (sequence <= 4) {
                    tag.putInt("deathKnellNightFear", 500);
                } else {
                    tag.putInt("deathKnellNightFear", 250);
                }
                break;
            case 5:
                if (sequence <= 4) {
                    tag.putInt("deathKnellWaterFear", 500);
                } else {
                    tag.putInt("deathKnellWaterFear", 250);
                }
                break;
            default:
                break;
        }
    }

    public static void deathKnellNegativeTick(LivingEntity livingEntity) {
        if (!livingEntity.level().isClientSide()) {
            CompoundTag tag = livingEntity.getPersistentData();
            int a = tag.getInt("deathKnellWaterFear");
            int b = tag.getInt("deathKnellFireFear");
            int c = tag.getInt("deathKnellMobFear");
            int d = tag.getInt("deathKnellPeacefulFear");
            int e = tag.getInt("deathKnellPlayerFear");
            int f = tag.getInt("deathKnellNightFear");
            if (a >= 1 && livingEntity.isInWaterOrRain()) {
                applyFear(livingEntity);
            }
            if (b >= 1) {
                BlockPos entityPos = livingEntity.blockPosition();
                boolean isFireNearby = false;
                for (int x = -15; x <= 15 && !isFireNearby; x++) {
                    for (int y = -15; y <= 15 && !isFireNearby; y++) {
                        for (int z = -15; z <= 15 && !isFireNearby; z++) {
                            BlockPos checkPos = entityPos.offset(x, y, z);
                            if (livingEntity.level().getBlockState(checkPos).is(Blocks.FIRE)) {
                                isFireNearby = true;
                            }
                        }
                    }
                }
                if (isFireNearby) {
                    applyFear(livingEntity);
                }
            }
            if (c >= 1) {
                for (Mob mob : livingEntity.level().getEntitiesOfClass(Mob.class, livingEntity.getBoundingBox().inflate(15))) {
                    if (mob != null) {
                        applyFear(livingEntity);
                    }
                }
            }
            if (d >= 1) {
                for (LivingEntity living : livingEntity.level().getEntitiesOfClass(LivingEntity.class, livingEntity.getBoundingBox().inflate(15))) {
                    if (living != livingEntity) {
                        if (!(living instanceof Mob)) {
                            applyFear(livingEntity);
                        }
                    }
                }
            }
            if (e >= 1) {
                for (Player player : livingEntity.level().getEntitiesOfClass(Player.class, livingEntity.getBoundingBox().inflate(15))) {
                    if (player != livingEntity) {
                        if (player != null) {
                            applyFear(livingEntity);
                        }
                    }
                }
            }
            if (f >= 1) {
                if (livingEntity.level().isNight()) {
                    applyFear(livingEntity);
                }
            }
        }
    }

    public static void applyFear(LivingEntity livingEntity) {
        if (livingEntity.hasEffect(MobEffects.MOVEMENT_SPEED)) {
            livingEntity.removeEffect(MobEffects.MOVEMENT_SPEED);
        }
        if (livingEntity.hasEffect(MobEffects.DAMAGE_BOOST)) {
            livingEntity.removeEffect(MobEffects.MOVEMENT_SPEED);
        }
        livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2, false, false));
        livingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 2, false, false));
    }
}
