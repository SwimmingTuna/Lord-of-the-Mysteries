package net.swimmingtuna.lotm.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.beyonder.SailorClass;
import net.swimmingtuna.lotm.beyonder.api.BeyonderClass;
import net.swimmingtuna.lotm.beyonder.SpectatorClass;

import java.util.function.Supplier;

public class BeyonderClassInit {
    public static final DeferredRegister<BeyonderClass> BEYONDER_CLASS = DeferredRegister.create(new ResourceLocation(LOTM.MOD_ID, "beyonder_class"), LOTM.MOD_ID);
    private static final Supplier<IForgeRegistry<BeyonderClass>> SUPPLIER = BEYONDER_CLASS.makeRegistry(RegistryBuilder::new);

    public static final RegistryObject<SpectatorClass> SPECTATOR = BEYONDER_CLASS.register("spectator", SpectatorClass::new);
    public static final RegistryObject<SailorClass> TYRANT = BEYONDER_CLASS.register("tyrant", SailorClass::new);

    public static IForgeRegistry<BeyonderClass> getRegistry() {
        return SUPPLIER.get();
    }
}
