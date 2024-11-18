package net.swimmingtuna.lotm.spirituality;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.swimmingtuna.lotm.LOTM;

public class ModAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(ForgeRegistries.ATTRIBUTES, LOTM.MOD_ID);

    public static final RegistryObject<Attribute> SOUL_BODY = ATTRIBUTES.register("soul_body",
            ()-> new RangedAttribute("attribute.lotm.soul_body",100.0D,0.0D,10000000).setSyncable(true));
    public static final RegistryObject<Attribute> SANITY = ATTRIBUTES.register("sanity",
            ()-> new RangedAttribute("attribute.lotm.sanity",100.0D,0.0D,100).setSyncable(true));
    public static final RegistryObject<Attribute> MISFORTUNE = ATTRIBUTES.register("misfortune",
            ()-> new RangedAttribute("attribute.lotm.misfortune",0.0D,0.0D,10000000).setSyncable(true));
    public static final RegistryObject<Attribute> NIGHTMARE = ATTRIBUTES.register("nightmare",
            ()-> new RangedAttribute("attribute.lotm.nightmare",0.0D,0.0D,10).setSyncable(true));
    public static final RegistryObject<Attribute> ARMORINVISIBLITY = ATTRIBUTES.register("armorinvisibility",
            ()-> new RangedAttribute("attribute.lotm.armorinvisibility",0.0D,0.0D,10).setSyncable(true));
    public static final RegistryObject<Attribute> DIR = ATTRIBUTES.register("dreamintoreality",
            ()-> new RangedAttribute("attribute.lotm.dreamintoreality",1.0D,0.0D,10).setSyncable(true));
    public static final RegistryObject<Attribute> CORRUPTION = ATTRIBUTES.register("corruption",
            ()-> new RangedAttribute("attribute.lotm.corruption",0.0D,0.0D,100).setSyncable(true));
    public static final RegistryObject<Attribute> LOTM_LUCK = ATTRIBUTES.register("luck",
            ()-> new RangedAttribute("attribute.lotm.luck",0.0D,0.0D,10000).setSyncable(true));



    public static void register(IEventBus eventBus) {
        ATTRIBUTES.register(eventBus);
    }

}