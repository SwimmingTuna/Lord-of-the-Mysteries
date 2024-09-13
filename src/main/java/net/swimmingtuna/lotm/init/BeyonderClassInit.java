package net.swimmingtuna.lotm.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.beyonder.*;
import net.swimmingtuna.lotm.beyonder.api.BeyonderClass;

import java.util.function.Supplier;

public class BeyonderClassInit {
    public static final DeferredRegister<BeyonderClass> BEYONDER_CLASS = DeferredRegister.create(new ResourceLocation(LOTM.MOD_ID, "beyonder_class"), LOTM.MOD_ID);
    private static final Supplier<IForgeRegistry<BeyonderClass>> SUPPLIER = BEYONDER_CLASS.makeRegistry(RegistryBuilder::new);

    public static final RegistryObject<SpectatorClass> SPECTATOR = BEYONDER_CLASS.register("spectator", SpectatorClass::new);
    public static final RegistryObject<SailorClass> SAILOR = BEYONDER_CLASS.register("sailor", SailorClass::new);
    public static final RegistryObject<SeerClass> SEER = BEYONDER_CLASS.register("seer", SeerClass::new);
    public static final RegistryObject<ApprenticeClass> APPRENTICE = BEYONDER_CLASS.register("apprentice", ApprenticeClass::new);
    public static final RegistryObject<MarauderClass> MARAUDER = BEYONDER_CLASS.register("marauder", MarauderClass::new);
    public static final RegistryObject<SecretsSupplicantClass> SECRETSSUPPLICANT = BEYONDER_CLASS.register("secrets_supplicant", SecretsSupplicantClass::new);
    public static final RegistryObject<BardClass> BARD = BEYONDER_CLASS.register("bard", BardClass::new);
    public static final RegistryObject<ReaderClass> READER = BEYONDER_CLASS.register("reader", ReaderClass::new);
    public static final RegistryObject<SleeplessClass> SLEEPLESS = BEYONDER_CLASS.register("sleepless", SleeplessClass::new);
    public static final RegistryObject<WarriorClass> WARRIOR = BEYONDER_CLASS.register("warrior", WarriorClass::new);
    public static final RegistryObject<HunterClass> HUNTER = BEYONDER_CLASS.register("hunter", HunterClass::new);
    public static final RegistryObject<AssassinClass> ASSASSIN = BEYONDER_CLASS.register("assassin", AssassinClass::new);
    public static final RegistryObject<SavantClass> SAVANT = BEYONDER_CLASS.register("savant", SavantClass::new);
    public static final RegistryObject<MysteryPryerClass> MYSTERYPRYER = BEYONDER_CLASS.register("mystery_pryer", MysteryPryerClass::new);
    public static final RegistryObject<CorpseCollectorClass> CORPSECOLLECTOR = BEYONDER_CLASS.register("corpse_collector", CorpseCollectorClass::new);
    public static final RegistryObject<LawyerClass> LAWYER = BEYONDER_CLASS.register("lawyer", LawyerClass::new);
    public static final RegistryObject<MonsterClass> MONSTER = BEYONDER_CLASS.register("monster", MonsterClass::new);
    public static final RegistryObject<ApothecaryClass> APOTHECARY = BEYONDER_CLASS.register("apothecary", ApothecaryClass::new);
    public static final RegistryObject<PlanterClass> PLANTER = BEYONDER_CLASS.register("planter", PlanterClass::new);
    public static final RegistryObject<ArbiterClass> ARBITER = BEYONDER_CLASS.register("arbiter", ArbiterClass::new);
    public static final RegistryObject<PrisonerClass> PRISONER = BEYONDER_CLASS.register("prisoner", PrisonerClass::new);
    public static final RegistryObject<CriminalClass> CRIMINAL = BEYONDER_CLASS.register("criminal", CriminalClass::new);

    public static IForgeRegistry<BeyonderClass> getRegistry() {
        return SUPPLIER.get();
    }
}
