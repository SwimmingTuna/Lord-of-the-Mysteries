package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.TornadoEntity;
import net.swimmingtuna.lotm.events.ReachChangeUUIDs;
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
            Vec3 lookVec = pPlayer.getLookAngle();
            pPlayer.getPersistentData().putInt("sailorSonicBoom", 60);
            pPlayer.getPersistentData().putDouble("sailorSonicBoomX", lookVec.x());
            pPlayer.getPersistentData().putDouble("sailorSonicBoomY", lookVec.y());
            pPlayer.getPersistentData().putDouble("sailorSonicBoomZ", lookVec.z());
        }
    }

    @SubscribeEvent
    public static void sonicBoomCancel(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.getPersistentData().getInt("sailorSonicBoomA") >= 1) {
            DamageSource source = event.getSource();
            if ("explosion".equals(source.getMsgId()) || "explosion.player".equals(source.getMsgId())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void tickEvent(LivingEvent.LivingTickEvent event) {
        LivingEntity pPlayer = event.getEntity();
        if (!pPlayer.level().isClientSide()) {
            int x = pPlayer.getPersistentData().getInt("sailorSonicBoomA");
            if (x >= 1) {
                pPlayer.getPersistentData().putInt("sailorSonicBoomA", x - 1);
            }
        }
    }
    @SubscribeEvent
    public static void sonicBoomTick(TickEvent.PlayerTickEvent event) {
        Player pPlayer = event.player;
        if (!pPlayer.level().isClientSide() && event.phase == TickEvent.Phase.END) {
            CompoundTag tag = pPlayer.getPersistentData();
            int sonicBoomCounter = tag.getInt("sailorSonicBoom");
            double x = tag.getDouble("sailorSonicBoomX");
            double y = tag.getDouble("sailorSonicBoomY");
            double z = tag.getDouble("sailorSonicBoomZ");

            if (sonicBoomCounter >= 1) {
                // Calculate the target position 200 blocks forward in the direction the player is looking
                Vec3 targetPos = pPlayer.position().add(x * 200, y * 200, z * 200);

                // Play explosion sound
                pPlayer.level().playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 10.0f, 5.0f);

                // Teleport the player to the target position
                pPlayer.teleportTo(targetPos.x, targetPos.y, targetPos.z);
                pPlayer.hurtMarked = true;

                // Destroy blocks within 8 blocks of the teleportation path
                for (int dx = -8; dx <= 8; dx++) {
                    for (int dy = -8; dy <= 8; dy++) {
                        for (int dz = -8; dz <= 8; dz++) {
                            BlockPos blockPos = new BlockPos((int) (targetPos.x + dx), (int) (targetPos.y + dy), (int) (targetPos.z + dz));
                            Block block = pPlayer.level().getBlockState(blockPos).getBlock();

                            // Check if the block isn't obsidian and remove it
                            if (block != Blocks.AIR && block != Blocks.OBSIDIAN && block != Blocks.BEDROCK) {
                                pPlayer.level().destroyBlock(blockPos, false);
                            }
                        }
                    }
                }

                // Deal damage to entities within 20 blocks of the target position
                for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(20))) {
                    if (entity != pPlayer) {
                        BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
                        int sequence = holder.getCurrentSequence();
                        int duration = 100 - (sequence * 20);
                        int damage = 25 - (sequence * 5);

                        if (!(entity instanceof Player)) {
                            entity.addEffect(new MobEffectInstance(ModEffects.AWE.get(), duration, 1, false, false));
                            entity.hurt(entity.damageSources().generic(), damage);
                        } else if (entity instanceof Player player) {
                            BeyonderHolder holder1 = BeyonderHolderAttacher.getHolder(player).orElse(null);
                            int pSequence = holder1.getCurrentSequence();
                            int pDuration = duration - (50 - (pSequence * 5));
                            int pDamage = (int) (damage - (8 - (pSequence * 0.5)));
                            entity.addEffect(new MobEffectInstance(ModEffects.AWE.get(), pDuration, 1, false, false));
                            entity.hurt(entity.damageSources().generic(), pDamage);
                        }
                    }
                }

                // Decrement the counter
                tag.putInt("sailorSonicBoom", sonicBoomCounter - 1);
            }
        }
    }
}
