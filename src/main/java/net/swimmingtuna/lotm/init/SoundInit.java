package net.swimmingtuna.lotm.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.common.util.ForgeSoundType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.swimmingtuna.lotm.LOTM;

public class SoundInit {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, LOTM.MOD_ID);

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }

    public static final RegistryObject<SoundEvent> SIREN_SONG_HARM = registerSoundEvents("siren_song_harm");
    public static final RegistryObject<SoundEvent> SIREN_SONG_WEAKEN = registerSoundEvents("siren_song_weaken");
    public static final RegistryObject<SoundEvent> SIREN_SONG_STUN = registerSoundEvents("siren_song_stun");
    public static final RegistryObject<SoundEvent> SIREN_SONG_STRENGTHEN = registerSoundEvents("siren_song_strengthen");





    private static RegistryObject<SoundEvent> registerSoundEvents(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(LOTM.MOD_ID, name)));
    }
}
