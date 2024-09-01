package net.swimmingtuna.lotm.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
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

    public static final RegistryObject<SoundEvent> SIREN_SONG_HARM_1 = registerSoundEvents("siren_song_harm_1");
    public static final RegistryObject<SoundEvent> SIREN_SONG_HARM_2 = registerSoundEvents("siren_song_harm_2");
    public static final RegistryObject<SoundEvent> SIREN_SONG_HARM_3 = registerSoundEvents("siren_song_harm_3");
    public static final RegistryObject<SoundEvent> SIREN_SONG_HARM_4 = registerSoundEvents("siren_song_harm_4");
    public static final RegistryObject<SoundEvent> SIREN_SONG_HARM_5 = registerSoundEvents("siren_song_harm_5");
    public static final RegistryObject<SoundEvent> SIREN_SONG_HARM_6 = registerSoundEvents("siren_song_harm_6");
    public static final RegistryObject<SoundEvent> SIREN_SONG_HARM_7 = registerSoundEvents("siren_song_harm_7");
    public static final RegistryObject<SoundEvent> SIREN_SONG_HARM_8 = registerSoundEvents("siren_song_harm_8");
    public static final RegistryObject<SoundEvent> SIREN_SONG_HARM_9 = registerSoundEvents("siren_song_harm_9");
    public static final RegistryObject<SoundEvent> SIREN_SONG_HARM_10 = registerSoundEvents("siren_song_harm_10");
    public static final RegistryObject<SoundEvent> SIREN_SONG_HARM_11 = registerSoundEvents("siren_song_harm_11");
    public static final RegistryObject<SoundEvent> SIREN_SONG_HARM_12 = registerSoundEvents("siren_song_harm_12");
    public static final RegistryObject<SoundEvent> SIREN_SONG_HARM_13 = registerSoundEvents("siren_song_harm_13");
    public static final RegistryObject<SoundEvent> SIREN_SONG_HARM_14 = registerSoundEvents("siren_song_harm_14");
    public static final RegistryObject<SoundEvent> SIREN_SONG_HARM_15 = registerSoundEvents("siren_song_harm_15");
    public static final RegistryObject<SoundEvent> SIREN_SONG_HARM_16 = registerSoundEvents("siren_song_harm_16");
    public static final RegistryObject<SoundEvent> SIREN_SONG_HARM_17 = registerSoundEvents("siren_song_harm_17");
    public static final RegistryObject<SoundEvent> SIREN_SONG_HARM_18 = registerSoundEvents("siren_song_harm_18");
    public static final RegistryObject<SoundEvent> SIREN_SONG_HARM_19 = registerSoundEvents("siren_song_harm_19");
    public static final RegistryObject<SoundEvent> SIREN_SONG_HARM_20 = registerSoundEvents("siren_song_harm_20");

    public static final RegistryObject<SoundEvent> SIREN_SONG_WEAKEN_1 = registerSoundEvents("siren_song_weaken_1");
    public static final RegistryObject<SoundEvent> SIREN_SONG_WEAKEN_2 = registerSoundEvents("siren_song_weaken_2");
    public static final RegistryObject<SoundEvent> SIREN_SONG_WEAKEN_3 = registerSoundEvents("siren_song_weaken_3");
    public static final RegistryObject<SoundEvent> SIREN_SONG_WEAKEN_4 = registerSoundEvents("siren_song_weaken_4");
    public static final RegistryObject<SoundEvent> SIREN_SONG_WEAKEN_5 = registerSoundEvents("siren_song_weaken_5");
    public static final RegistryObject<SoundEvent> SIREN_SONG_WEAKEN_6 = registerSoundEvents("siren_song_weaken_6");
    public static final RegistryObject<SoundEvent> SIREN_SONG_WEAKEN_7 = registerSoundEvents("siren_song_weaken_7");
    public static final RegistryObject<SoundEvent> SIREN_SONG_WEAKEN_8 = registerSoundEvents("siren_song_weaken_8");
    public static final RegistryObject<SoundEvent> SIREN_SONG_WEAKEN_9 = registerSoundEvents("siren_song_weaken_9");
    public static final RegistryObject<SoundEvent> SIREN_SONG_WEAKEN_10 = registerSoundEvents("siren_song_weaken_10");
    public static final RegistryObject<SoundEvent> SIREN_SONG_WEAKEN_11 = registerSoundEvents("siren_song_weaken_11");
    public static final RegistryObject<SoundEvent> SIREN_SONG_WEAKEN_12 = registerSoundEvents("siren_song_weaken_12");
    public static final RegistryObject<SoundEvent> SIREN_SONG_WEAKEN_13 = registerSoundEvents("siren_song_weaken_13");
    public static final RegistryObject<SoundEvent> SIREN_SONG_WEAKEN_14 = registerSoundEvents("siren_song_weaken_14");
    public static final RegistryObject<SoundEvent> SIREN_SONG_WEAKEN_15 = registerSoundEvents("siren_song_weaken_15");
    public static final RegistryObject<SoundEvent> SIREN_SONG_WEAKEN_16 = registerSoundEvents("siren_song_weaken_16");
    public static final RegistryObject<SoundEvent> SIREN_SONG_WEAKEN_17 = registerSoundEvents("siren_song_weaken_17");
    public static final RegistryObject<SoundEvent> SIREN_SONG_WEAKEN_18 = registerSoundEvents("siren_song_weaken_18");
    public static final RegistryObject<SoundEvent> SIREN_SONG_WEAKEN_19 = registerSoundEvents("siren_song_weaken_19");
    public static final RegistryObject<SoundEvent> SIREN_SONG_WEAKEN_20 = registerSoundEvents("siren_song_weaken_20");

    public static final RegistryObject<SoundEvent> SIREN_SONG_STUN_1 = registerSoundEvents("siren_song_stun_1");
    public static final RegistryObject<SoundEvent> SIREN_SONG_STUN_2 = registerSoundEvents("siren_song_stun_2");
    public static final RegistryObject<SoundEvent> SIREN_SONG_STUN_3 = registerSoundEvents("siren_song_stun_3");
    public static final RegistryObject<SoundEvent> SIREN_SONG_STUN_4 = registerSoundEvents("siren_song_stun_4");
    public static final RegistryObject<SoundEvent> SIREN_SONG_STUN_5 = registerSoundEvents("siren_song_stun_5");
    public static final RegistryObject<SoundEvent> SIREN_SONG_STUN_6 = registerSoundEvents("siren_song_stun_6");
    public static final RegistryObject<SoundEvent> SIREN_SONG_STUN_7 = registerSoundEvents("siren_song_stun_7");
    public static final RegistryObject<SoundEvent> SIREN_SONG_STUN_8 = registerSoundEvents("siren_song_stun_8");
    public static final RegistryObject<SoundEvent> SIREN_SONG_STUN_9 = registerSoundEvents("siren_song_stun_9");
    public static final RegistryObject<SoundEvent> SIREN_SONG_STUN_10 = registerSoundEvents("siren_song_stun_10");
    public static final RegistryObject<SoundEvent> SIREN_SONG_STUN_11 = registerSoundEvents("siren_song_stun_11");
    public static final RegistryObject<SoundEvent> SIREN_SONG_STUN_12 = registerSoundEvents("siren_song_stun_12");
    public static final RegistryObject<SoundEvent> SIREN_SONG_STUN_13 = registerSoundEvents("siren_song_stun_13");
    public static final RegistryObject<SoundEvent> SIREN_SONG_STUN_14 = registerSoundEvents("siren_song_stun_14");
    public static final RegistryObject<SoundEvent> SIREN_SONG_STUN_15 = registerSoundEvents("siren_song_stun_15");
    public static final RegistryObject<SoundEvent> SIREN_SONG_STUN_16 = registerSoundEvents("siren_song_stun_16");
    public static final RegistryObject<SoundEvent> SIREN_SONG_STUN_17 = registerSoundEvents("siren_song_stun_17");
    public static final RegistryObject<SoundEvent> SIREN_SONG_STUN_18 = registerSoundEvents("siren_song_stun_18");
    public static final RegistryObject<SoundEvent> SIREN_SONG_STUN_19 = registerSoundEvents("siren_song_stun_19");
    public static final RegistryObject<SoundEvent> SIREN_SONG_STUN_20 = registerSoundEvents("siren_song_stun_20");

    public static final RegistryObject<SoundEvent> SIREN_SONG_STRENGTHEN_1 = registerSoundEvents("siren_song_strengthen_1");
    public static final RegistryObject<SoundEvent> SIREN_SONG_STRENGTHEN_2 = registerSoundEvents("siren_song_strengthen_2");
    public static final RegistryObject<SoundEvent> SIREN_SONG_STRENGTHEN_3 = registerSoundEvents("siren_song_strengthen_3");
    public static final RegistryObject<SoundEvent> SIREN_SONG_STRENGTHEN_4 = registerSoundEvents("siren_song_strengthen_4");
    public static final RegistryObject<SoundEvent> SIREN_SONG_STRENGTHEN_5 = registerSoundEvents("siren_song_strengthen_5");
    public static final RegistryObject<SoundEvent> SIREN_SONG_STRENGTHEN_6 = registerSoundEvents("siren_song_strengthen_6");
    public static final RegistryObject<SoundEvent> SIREN_SONG_STRENGTHEN_7 = registerSoundEvents("siren_song_strengthen_7");
    public static final RegistryObject<SoundEvent> SIREN_SONG_STRENGTHEN_8 = registerSoundEvents("siren_song_strengthen_8");
    public static final RegistryObject<SoundEvent> SIREN_SONG_STRENGTHEN_9 = registerSoundEvents("siren_song_strengthen_9");
    public static final RegistryObject<SoundEvent> SIREN_SONG_STRENGTHEN_10 = registerSoundEvents("siren_song_strengthen_10");
    public static final RegistryObject<SoundEvent> SIREN_SONG_STRENGTHEN_11 = registerSoundEvents("siren_song_strengthen_11");
    public static final RegistryObject<SoundEvent> SIREN_SONG_STRENGTHEN_12 = registerSoundEvents("siren_song_strengthen_12");
    public static final RegistryObject<SoundEvent> SIREN_SONG_STRENGTHEN_13 = registerSoundEvents("siren_song_strengthen_13");
    public static final RegistryObject<SoundEvent> SIREN_SONG_STRENGTHEN_14 = registerSoundEvents("siren_song_strengthen_14");
    public static final RegistryObject<SoundEvent> SIREN_SONG_STRENGTHEN_15 = registerSoundEvents("siren_song_strengthen_15");
    public static final RegistryObject<SoundEvent> SIREN_SONG_STRENGTHEN_16 = registerSoundEvents("siren_song_strengthen_16");
    public static final RegistryObject<SoundEvent> SIREN_SONG_STRENGTHEN_17 = registerSoundEvents("siren_song_strengthen_17");
    public static final RegistryObject<SoundEvent> SIREN_SONG_STRENGTHEN_18 = registerSoundEvents("siren_song_strengthen_18");
    public static final RegistryObject<SoundEvent> SIREN_SONG_STRENGTHEN_19 = registerSoundEvents("siren_song_strengthen_19");
    public static final RegistryObject<SoundEvent> SIREN_SONG_STRENGTHEN_20 = registerSoundEvents("siren_song_strengthen_20");



    private static RegistryObject<SoundEvent> registerSoundEvents(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(LOTM.MOD_ID, name)));
    }
}
