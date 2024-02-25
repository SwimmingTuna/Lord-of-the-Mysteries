package net.swimmingtuna.lotm.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.ability.Ability;

import java.util.function.Supplier;
//todo: this is just a reminder of converting them to Keybinds instead of items in the futureu. this code will do nothing at the current time.
public class AbilityInit {
    public static final DeferredRegister<Ability> ABILITIES = DeferredRegister.create(new ResourceLocation(LOTM.MOD_ID, "abilities"), LOTM.MOD_ID);
    public static final RegistryObject<Ability> NONE = ABILITIES.register("none", Ability::new);
    private static final Supplier<IForgeRegistry<Ability>> SUPPLIER = ABILITIES.makeRegistry(RegistryBuilder::new);

    public static IForgeRegistry<Ability> getRegistry() {
        return SUPPLIER.get();
    }

}
