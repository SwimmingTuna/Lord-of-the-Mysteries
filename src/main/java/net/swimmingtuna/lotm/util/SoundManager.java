package net.swimmingtuna.lotm.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public final class SoundManager {

    private static final Int2ObjectMap<SoundInstance> PLAYING_RECORDS = new Int2ObjectOpenHashMap<>();

    public static void playMusic(final LivingEntity entity, final RecordItem recorditem) {
        Minecraft.getInstance().gui.setNowPlaying(recorditem.getDisplayName());
        final EntityBoundSoundInstance soundInstance = new EntityBoundSoundInstance(recorditem.getSound(),
                SoundSource.RECORDS, 2, 1, entity, entity.level().random.nextLong());
        PLAYING_RECORDS.put(entity.getId(), soundInstance);
        Minecraft.getInstance().getSoundManager().play(soundInstance);
    }

    public static void stopMusic(final LivingEntity entity) {
        final SoundInstance soundInstance = PLAYING_RECORDS.get(entity.getId());
        if (soundInstance != null) {
            Minecraft.getInstance().getSoundManager().stop(soundInstance);
            PLAYING_RECORDS.remove(entity.getId());
        }
    }

    // New method to play a SoundEvent for all entities nearby
    public static void playSoundForNearbyEntities(final LivingEntity sourceEntity, final SoundEvent soundEvent, final float radius) {
        Level level = sourceEntity.level();
        List<LivingEntity> nearbyEntities = level.getEntitiesOfClass(LivingEntity.class, sourceEntity.getBoundingBox().inflate(radius));

        for (LivingEntity entity : nearbyEntities) {
            EntityBoundSoundInstance soundInstance = new EntityBoundSoundInstance(soundEvent, SoundSource.NEUTRAL, 1.0f, 1.0f, entity, level.random.nextLong());
            Minecraft.getInstance().getSoundManager().play(soundInstance);
            PLAYING_RECORDS.put(entity.getId(), soundInstance);
        }
    }

    public static void stopSoundsForNearbyEntities(final LivingEntity sourceEntity, final float radius) {
        Level level = sourceEntity.level();
        List<LivingEntity> nearbyEntities = level.getEntitiesOfClass(LivingEntity.class, sourceEntity.getBoundingBox().inflate(radius));

        for (LivingEntity entity : nearbyEntities) {
            final SoundInstance soundInstance = PLAYING_RECORDS.get(entity.getId());
            if (soundInstance != null) {
                Minecraft.getInstance().getSoundManager().stop(soundInstance);
                PLAYING_RECORDS.remove(entity.getId());
            }
        }
    }
}
