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
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.client.event.sound.SoundEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.ParticleInit;
import net.swimmingtuna.lotm.init.SoundInit;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SirenSongHarm extends Item {
    public SirenSongHarm(Properties pProperties) {
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
                } else if (holder.isSailorClass() && holder.getCurrentSequence() <= 5 && holder.useSpirituality(150)) {
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
        if (!pPlayer.level().isClientSide()) {
            CompoundTag tag = pPlayer.getPersistentData();
            if (tag.getInt("sirenSongHarm") == 0) {
                pPlayer.sendSystemMessage(Component.literal("worked"));
                tag.putInt("sirenSongHarm", 400);
                tag.putInt("ssParticleAttributeHelper", 400);
            }
            if (tag.getInt("sirenSongHarm") > 1 && tag.getInt("sirenSongHarm") < 400) {
                tag.putInt("sirenSongHarm", 0);
                tag.putInt("ssParticleAttributeHelper", 1);
            }
            if (tag.getInt("sirenSongWeaken") != 0) {
                tag.putInt("sirenSongWeaken", 0);
                tag.putInt("sirenSongHarm", 400);
                tag.putInt("ssParticleAttributeHelper", 400);

            }
            if (tag.getInt("sirenSongStun") != 0) {
                tag.putInt("sirenSongStun", 0);
                tag.putInt("sirenSongHarm", 400);

                tag.putInt("ssParticleAttributeHelper", 400);
            }
            if (tag.getInt("sirenSongStrengthen") != 0) {
                tag.putInt("sirenSongStrengthen", 0);
                tag.putInt("sirenSongHarm", 400);
                tag.putInt("ssParticleAttributeHelper", 400);
            }
        }
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
    public static void sirenSongTick(TickEvent.PlayerTickEvent event) {
        Player pPlayer = event.player;
        BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
        int sequence = holder.getCurrentSequence();
        CompoundTag tag = pPlayer.getPersistentData();
        int ssHarm = tag.getInt("sirenSongHarm");
        int ssWeaken = tag.getInt("sirenSongWeaken");
        int ssStun = tag.getInt("sirenSongStun");
        int ssStrengthen = tag.getInt("sirenSongStrengthen");

        if (event.phase == TickEvent.Phase.END && !pPlayer.level().isClientSide() && holder.isSailorClass() && sequence <= 5) {
            if (ssHarm % 20 == 0 && ssHarm != 0) {
                for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(50 - (sequence * 6)))) {
                    if (entity != pPlayer) {
                        entity.hurt(entity.damageSources().magic(), 10 - sequence);
                    }
                }

            }
            if (ssHarm == 400) {
                pPlayer.level().playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundInit.SIREN_SONG_HARM.get(), SoundSource.NEUTRAL, 1f, 1f);
            }
            if (ssHarm >= 1) {
                tag.putInt("sirenSongHarm", ssHarm - 1);
            }

            if (ssWeaken % 20 == 0 && ssWeaken != 0) { //make it for 380,360,430 etc.
                for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(50 - (sequence * 6)))) {
                    if (entity != pPlayer) {
                        entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,19,2, false,false));
                        entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION,19,2, false,false));
                    }
                }
            }
            if (ssWeaken == 400) {
                pPlayer.level().playSound(null, pPlayer.getX(), pPlayer.getY(),pPlayer.getZ(), SoundInit.SIREN_SONG_WEAKEN.get(), SoundSource.NEUTRAL,1f,1f);
            }
            if (ssWeaken >= 1) {
                tag.putInt("sirenSongWeaken", ssWeaken - 1);
            }

            if (ssStun % 20 == 0 && ssStun != 0) {
                for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(50 - (sequence * 6)))) {
                    if (entity != pPlayer) {
                        entity.addEffect(new MobEffectInstance(ModEffects.PARALYSIS.get(),19 - (sequence * 2),2, false,false));
                    }
                }
            }
            if (ssStun == 400) {
                pPlayer.level().playSound(null, pPlayer.getX(), pPlayer.getY(),pPlayer.getZ(), SoundInit.SIREN_SONG_STUN.get(), SoundSource.NEUTRAL,1f,1f);
            }
            if (ssStun >= 1) {
                tag.putInt("sirenSongStun", ssStun - 1);
            }
            if (ssStrengthen % 20 == 0 && ssStrengthen != 0) {
                if (pPlayer.hasEffect(MobEffects.DAMAGE_BOOST)) {
                    int x = pPlayer.getEffect(MobEffects.DAMAGE_BOOST).getAmplifier();
                    pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 19,x + 2));
                } else if (!pPlayer.hasEffect(MobEffects.DAMAGE_BOOST)) {
                    pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,19,2));
                }
                if (pPlayer.hasEffect(MobEffects.REGENERATION)) {
                    int x = pPlayer.getEffect(MobEffects.REGENERATION).getAmplifier();
                    pPlayer.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 19,x + 2));
                }
                else if (!pPlayer.hasEffect(MobEffects.REGENERATION)) {
                    pPlayer.addEffect(new MobEffectInstance(MobEffects.REGENERATION,19,2));
                }
            }
            if (ssStrengthen == 400) {
                pPlayer.level().playSound(null, pPlayer.getX(), pPlayer.getY(),pPlayer.getZ(), SoundInit.SIREN_SONG_STRENGTHEN.get(), SoundSource.NEUTRAL,1f,1f);
            }
            if (ssStrengthen >= 1) {
                tag.putInt("sirenSongStrengthen", ssStrengthen - 1);
            }
        }
        if (!pPlayer.level().isClientSide() && event.phase == TickEvent.Phase.END) {
            int x = tag.getInt("ssParticleAttributeHelper");
            if (x >= 1) {
                tag.putInt("ssParticleAttributeHelper", x - 1);
                pPlayer.getAttribute(ModAttributes.PARTICLE_HELPER2.get()).setBaseValue(1);
            }
            if (x < 1) {
                pPlayer.getAttribute(ModAttributes.PARTICLE_HELPER2.get()).setBaseValue(0);
            }
        }


        if (event.phase == TickEvent.Phase.END && holder.isSailorClass() && sequence <= 5) {
            AttributeInstance particleAttribute = pPlayer.getAttribute(ModAttributes.PARTICLE_HELPER2.get());
            int y = 50 - (sequence * 6);
            if (particleAttribute.getBaseValue() == 1) {
                spawnParticlesInSphere(pPlayer, y);
            } else {
                particleAttribute.setBaseValue(0);
            }
        }
    }
    private static void spawnParticlesInSphere(Player player, int radius) {
        Level level = player.level();
        Random random = new Random();

        for (int i = 0; i < 20; i++) { // Adjust the number of particles as needed
            double x = player.getX() + (random.nextDouble() * 2 - 1) * radius;
            double y = player.getY() + (random.nextDouble() * 2 - 1) * radius;
            double z = player.getZ() + (random.nextDouble() * 2 - 1) * radius;

            // Check if the point is within the sphere
            if (isInsideSphere(player.getX(), player.getY(), player.getZ(), x, y, z, radius)) {
                double noteValue = random.nextInt(25) / 24.0;
                level.addParticle(ParticleTypes.NOTE, x, y, z, noteValue, 0, 0);
            }
        }
    }

    // Helper method to check if a point is inside the sphere
    private static boolean isInsideSphere(double centerX, double centerY, double centerZ, double x, double y, double z, double radius) {
        double distance = Math.sqrt(
                Math.pow(x - centerX, 2) +
                        Math.pow(y - centerY, 2) +
                        Math.pow(z - centerZ, 2)
        );
        return distance <= radius;
    }
}