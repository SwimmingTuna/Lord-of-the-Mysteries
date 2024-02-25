package net.swimmingtuna.lotm.caps;

import dev._100media.capabilitysyncer.core.CapabilityAttacher;
import dev._100media.capabilitysyncer.network.SimpleEntityCapabilityStatusPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID)
public class BeyonderHolderAttacher extends CapabilityAttacher {
    public static final Capability<BeyonderHolder> CAPABILITY = getCapability(new CapabilityToken<>() {});
    public static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation(LOTM.MOD_ID, "beyonder_class");
    private static final Class<BeyonderHolder> CAPABILITY_CLASS = BeyonderHolder.class;

    @SuppressWarnings("ConstantConditions")
    @Nullable
    public static BeyonderHolder getHolderUnwrap(Player player) {
        return getHolder(player).orElse(null);
    }

    public static LazyOptional<BeyonderHolder> getHolder(Player player) {
        return player.getCapability(CAPABILITY);
    }

    private static void attach(AttachCapabilitiesEvent<Entity> event, Player entity) {
        genericAttachCapability(event, new BeyonderHolder(entity), CAPABILITY, RESOURCE_LOCATION);
    }

    public static void register() {
        CapabilityAttacher.registerCapability(CAPABILITY_CLASS);
        CapabilityAttacher.registerPlayerAttacher(BeyonderHolderAttacher::attach, BeyonderHolderAttacher::getHolder, true);
        SimpleEntityCapabilityStatusPacket.registerRetriever(RESOURCE_LOCATION, BeyonderHolderAttacher::getHolderUnwrap);
    }

}
